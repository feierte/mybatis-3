/**
 *    Copyright 2009-2025 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor;

import static org.apache.ibatis.executor.ExecutionPlaceholder.EXECUTION_PLACEHOLDER;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.statement.StatementUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Clinton Begin
 */
public abstract class BaseExecutor implements Executor {

  private static final Log log = LogFactory.getLog(BaseExecutor.class);

  protected Transaction transaction;
  protected Executor wrapper;

  protected ConcurrentLinkedQueue<DeferredLoad> deferredLoads;
  // 一级缓存（本地缓存）的核心存储，默认开启的，不需要任何配置：会话级缓存，生命周期是整个会话 SqlSession，非常短暂，不能直接关闭，不能跨线程使用（非线程安全）
  // 注意这里的 本地缓存是同一个 session 内的缓存，也就是同一个 open session 内
  // 根据 CacheKey 决定是否已经缓存
  /*
   * 修改（执行 sql 前清空缓存）、查询（mapper.xml 的 sql 块上配置了一级缓存作用域 statementType="STATEMENT"，后置清空）、
   * 提交（提交前清空缓存）、回滚（回滚前清空缓存），都可能会清空一级缓存
   */
  protected PerpetualCache localCache;
  // 一级缓存，和存储过程有关
  protected PerpetualCache localOutputParameterCache;
  protected Configuration configuration;

  protected int queryStack;
  private boolean closed;

  protected BaseExecutor(Configuration configuration, Transaction transaction) {
    this.transaction = transaction;
    this.deferredLoads = new ConcurrentLinkedQueue<>();
    this.localCache = new PerpetualCache("LocalCache");
    this.localOutputParameterCache = new PerpetualCache("LocalOutputParameterCache");
    this.closed = false;
    this.configuration = configuration;
    this.wrapper = this;
  }

  @Override
  public Transaction getTransaction() {
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    return transaction;
  }

  @Override
  public void close(boolean forceRollback) {
    try {
      try {
        rollback(forceRollback);
      } finally {
        if (transaction != null) {
          transaction.close();
        }
      }
    } catch (SQLException e) {
      // Ignore. There's nothing that can be done at this point.
      log.warn("Unexpected exception on closing transaction.  Cause: " + e);
    } finally {
      transaction = null;
      deferredLoads = null;
      localCache = null;
      localOutputParameterCache = null;
      closed = true;
    }
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public int update(MappedStatement ms, Object parameter) throws SQLException {
    ErrorContext.instance().resource(ms.getResource()).activity("executing an update").object(ms.getId());
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    clearLocalCache(); // ← 关键！执行任何 update 都清空一级缓存，这是为了保证数据一致性：修改数据后，旧缓存可能已过期。
    return doUpdate(ms, parameter);
  }

  @Override
  public List<BatchResult> flushStatements() throws SQLException {
    return flushStatements(false);
  }

  public List<BatchResult> flushStatements(boolean isRollBack) throws SQLException {
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    return doFlushStatements(isRollBack);
  }

  @Override
  public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    // 根据传入的参数动态的获取sql语句，最后返回的是BoundSql对象
    BoundSql boundSql = ms.getBoundSql(parameter);
    // 为本次查询创建缓存 key
    CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
    // 执行带缓存的查询
    return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
  }

  /**
   * 1、先查本地缓存，没有再去查数据库，注意这里的 本地缓存是同一个session内的缓存，也就是同一个opensession内。
   * 2、通过configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT来看，可以设置参数，将本地缓存去掉，不使用本地缓存。
   */
  @SuppressWarnings("unchecked")
  @Override
  public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    // queryStack为0 && mapper.xml的sql块上配置了flushCache=true，前置清空缓存
    if (queryStack == 0 && ms.isFlushCacheRequired()) {
      clearLocalCache();  // 如果是 UPDATE/INSERT/DELETE，默认 flushCache=true，清空缓存
    }
    List<E> list;
    try {
      queryStack++;
      // 尝试从一级缓存中获取
      list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
      if (list != null) {
        handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
      } else {
        // 一级缓存未命中，再去查数据库，查询到结果放入一级缓存中
        list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
      }
    } finally {
      queryStack--;
    }
    if (queryStack == 0) {
      // 延迟加载
      for (DeferredLoad deferredLoad : deferredLoads) {
        deferredLoad.load();
      }
      // issue #601
      deferredLoads.clear();
      // mapper.xml的sql块上配置了一级缓存作用域statementType="STATEMENT"，后置清空
      // 通过configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT来看，可以设置参数，将本地缓存去掉，不使用本地缓存。
      if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
        // issue #482 清除本地缓存
        clearLocalCache();
      }
    }
    return list;
  }

  @Override
  public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
    BoundSql boundSql = ms.getBoundSql(parameter);
    return doQueryCursor(ms, parameter, rowBounds, boundSql);
  }

  @Override
  public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    DeferredLoad deferredLoad = new DeferredLoad(resultObject, property, key, localCache, configuration, targetType);
    if (deferredLoad.canLoad()) {
      deferredLoad.load();
    } else {
      deferredLoads.add(new DeferredLoad(resultObject, property, key, localCache, configuration, targetType));
    }
  }

  @Override
  public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    /*
     * 从这里可以看出一级缓存的命中条件：
     * 1、同一个SqlSession会话。不是同一个会话，就不是同一个localCache，这点很重要！！！
     * 2、StatementId相同 。com.test.UserMapper.selectById
     * 3、分页参数RowBounds相同。limit 1
     * 4、SQL语句相同。select id from user where id = ?
     * 5、SQL查询参数相同。 id =1
     * 6、环境相同。environmentId=development 通常不会跨环境开发，可以忽略
     */
    CacheKey cacheKey = new CacheKey();
    cacheKey.update(ms.getId());              // 1. SQL ID（如 com.UserMapper.selectById）
    cacheKey.update(rowBounds.getOffset());   // 2. 分页偏移
    cacheKey.update(rowBounds.getLimit());    // 3. 分页大小
    cacheKey.update(boundSql.getSql());       // 4. 最终 SQL 字符串
    // 5. 参数值（遍历 ParameterMapping）
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    TypeHandlerRegistry typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
    // mimic DefaultParameterHandler logic
    for (ParameterMapping parameterMapping : parameterMappings) {
      if (parameterMapping.getMode() != ParameterMode.OUT) {
        Object value;
        String propertyName = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(propertyName)) {
          value = boundSql.getAdditionalParameter(propertyName);
        } else if (parameterObject == null) {
          value = null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
          value = parameterObject;
        } else {
          MetaObject metaObject = configuration.newMetaObject(parameterObject);
          value = metaObject.getValue(propertyName);
        }
        cacheKey.update(value); // ← sql 参数值参与 hash，参数对象的引用地址不影响缓存键，只看值是否相等。只要参数值相同，即使传入新对象，也能命中缓存。
      }
    }
    // 6. Environment ID（多数据源场景）
    if (configuration.getEnvironment() != null) {
      // issue #176
      cacheKey.update(configuration.getEnvironment().getId());
    }
    return cacheKey;
  }

  @Override
  public boolean isCached(MappedStatement ms, CacheKey key) {
    return localCache.getObject(key) != null;
  }

  @Override
  public void commit(boolean required) throws SQLException {
    if (closed) {
      throw new ExecutorException("Cannot commit, transaction is already closed");
    }
    clearLocalCache();
    flushStatements();
    if (required) {
      transaction.commit();
    }
  }

  @Override
  public void rollback(boolean required) throws SQLException {
    if (!closed) {
      try {
        clearLocalCache();
        flushStatements(true);
      } finally {
        if (required) {
          transaction.rollback();
        }
      }
    }
  }

  @Override
  public void clearLocalCache() {
    if (!closed) {
      localCache.clear();
      localOutputParameterCache.clear();
    }
  }

  protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;

  protected abstract List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException;

  protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
      throws SQLException;

  protected abstract <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql)
      throws SQLException;

  protected void closeStatement(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  /**
   * Apply a transaction timeout.
   *
   * @param statement
   *          a current statement
   * @throws SQLException
   *           if a database access error occurs, this method is called on a closed <code>Statement</code>
   * @since 3.4.0
   * @see StatementUtil#applyTransactionTimeout(Statement, Integer, Integer)
   */
  protected void applyTransactionTimeout(Statement statement) throws SQLException {
    StatementUtil.applyTransactionTimeout(statement, statement.getQueryTimeout(), transaction.getTimeout());
  }

  private void handleLocallyCachedOutputParameters(MappedStatement ms, CacheKey key, Object parameter, BoundSql boundSql) {
    if (ms.getStatementType() == StatementType.CALLABLE) {
      final Object cachedParameter = localOutputParameterCache.getObject(key);
      if (cachedParameter != null && parameter != null) {
        final MetaObject metaCachedParameter = configuration.newMetaObject(cachedParameter);
        final MetaObject metaParameter = configuration.newMetaObject(parameter);
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
          if (parameterMapping.getMode() != ParameterMode.IN) {
            final String parameterName = parameterMapping.getProperty();
            final Object cachedValue = metaCachedParameter.getValue(parameterName);
            metaParameter.setValue(parameterName, cachedValue);
          }
        }
      }
    }
  }


  private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    List<E> list;
    // 在缓存中，添加占位对象。先放一个占位符（防止循环引用或递归查询时死锁）
    // 使用 EXECUTION_PLACEHOLDER 是为了防止在结果映射过程中再次触发相同查询（如嵌套查询），导致无限递归。
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
    try {
      // 真正执行 SQL 查询，执行数据库读操作
      list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    } finally {
      // 从缓存中，移除占位对象，确保占位符被清理（异常时也清理）
      localCache.removeObject(key);
    }
    // 将查询结果缓存到一级缓存中
    localCache.putObject(key, list);
    // 暂时忽略，此处和存储过程相关
    if (ms.getStatementType() == StatementType.CALLABLE) {
      localOutputParameterCache.putObject(key, parameter);
    }
    return list;
  }

  protected Connection getConnection(Log statementLog) throws SQLException {
    Connection connection = transaction.getConnection();
    if (statementLog.isDebugEnabled()) {
      return ConnectionLogger.newInstance(connection, statementLog, queryStack);
    } else {
      return connection;
    }
  }

  @Override
  public void setExecutorWrapper(Executor wrapper) {
    this.wrapper = wrapper;
  }

  /**
   * 延迟加载：就是在需要用到数据时才进行加载，不需要用到数据时就不加载数据。延迟加载也称为懒加载。
   * <p>问题：在开发过程中很多时候我们并不需要总是在加载用户信息时就一定要加载他的订单信息。此时就是我们说的延迟加载。
   * <p>示例:
   * 在一对多中，当我们有一个用户，它有100个订单：
   *  （1）、在查询用户的时候，要不要把关联的订单查出来？
   *  （2）、在查询订单的时候，要不要把关联的用户查出来？
   *
   * 答：在查询用户时，用户下的订单应该是，什么时候用，什么时候查询。
   *    在查询订单时，订单所属的用户信息应该是随着订单一起查询出来。
   *
   * <p>优点：先从单表查询，需要时在从关联表去关联查询，大大提高数据库性能，因为查询单表要比关联查询多张表速度要快。
   *    缺点：因为只有当需要用到数据时，才会进行数据库查询，这样在大批量数据查询时，因为查询工作也需要消耗时间，所以可能造成用户等待时间变长，造成用户体验下降。
   *
   * <p>应用场景：
   *    一对多，多对多：通常情况下采用延迟加载。
   *    一对一（多对一）：通常情况下采用立即加载。
   *
   * <p>注意事项：
   *   延迟加载是基于嵌套查询来实现的
   */
  private static class DeferredLoad {

    private final MetaObject resultObject;
    private final String property;
    private final Class<?> targetType;
    private final CacheKey key;
    private final PerpetualCache localCache;
    private final ObjectFactory objectFactory;
    private final ResultExtractor resultExtractor;

    // issue #781
    public DeferredLoad(MetaObject resultObject,
                        String property,
                        CacheKey key,
                        PerpetualCache localCache,
                        Configuration configuration,
                        Class<?> targetType) {
      this.resultObject = resultObject;
      this.property = property;
      this.key = key;
      this.localCache = localCache;
      this.objectFactory = configuration.getObjectFactory();
      this.resultExtractor = new ResultExtractor(configuration, objectFactory);
      this.targetType = targetType;
    }

    public boolean canLoad() {
      return localCache.getObject(key) != null && localCache.getObject(key) != EXECUTION_PLACEHOLDER;
    }

    public void load() {
      @SuppressWarnings("unchecked")
      // we suppose we get back a List
      List<Object> list = (List<Object>) localCache.getObject(key);
      Object value = resultExtractor.extractObjectFromList(list, targetType);
      resultObject.setValue(property, value);
    }

  }

}
