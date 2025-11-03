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

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.TransactionalCacheManager;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 *
 * @apiNote 二级缓存（全局缓存）
 * 二级缓存是构建在一级缓存之上，在收到查询请求时，MyBatis首先会查询二级缓存，若二级缓存未命中，再去查询一级缓存，一级缓存也没有，再去查询数据库。
 *
 * <p>读取 mybatis-config 全局配置文件的时候会根据我们配置的 Executor 类型来创建对应的三种 Executor 中的一种，然后如果我们开启了二级缓存之后，
 * 只要开启(全局配置文件中配置为true)就会使用 CachingExecutor 来对我们的三种基本 Executor 进行包装，即使 Mapper.xml 映射文件没有开启也会进行包装。
 *
 * <p>二级缓存
 * 一级缓存因为只能在同一个 SqlSession 中共享，所以会存在一个问题，在分布式或者多线程的环境下，不同会话之间对于相同的数据可能会产生不同的结果，
 * 因为跨会话修改了数据是不能互相感知的，所以就有可能存在脏数据的问题，正因为一级缓存存在这种不足，所以我们需要一种作用域更大的缓存，这就是二级缓存。
 *
 * <p>二级缓存的作用范围
 * 一级缓存作用域是 SqlSession 级别，所以它存储的 SqlSession 中的 BaseExecutor 之中，但是二级缓存目的就是要实现作用范围更广，那肯定是要实现跨会话共享的，
 * 在 MyBatis 中二级缓存的作用域是 namespace，也就是作用范围是同一个命名空间，所以很显然二级缓存是需要存储在 SqlSession 之外的，那么二级缓存应该存储在哪里合适呢？
 *
 * 在MyBatis中为了实现二级缓存，专门用了一个装饰器来维护，这就是：CachingExecutor。
 *
 * <p>如何开启二级缓存
 * 二级缓存相关的配置有三个地方：
 * 1、mybatis-config中有一个全局配置属性，这个不配置也行，因为默认就是true。
 *     <setting name="cacheEnabled" value="true"/>
 * 2、在Mapper映射文件内需要配置缓存标签：
 *    <cache/>
 *    或
 *    <cache-ref namespace="com.lonelyWolf.mybatis.mapper.UserAddressMapper"/>
 * 3、在select查询语句标签上配置useCache属性，如下：
 *      <select id="selectUserAndJob" resultMap="JobResultMap2" useCache="true">
 *         select * from lw_user
 *     </select>
 * 以上配置第1点是默认开启的，也就是说我们只要配置第2点就可以打开二级缓存了，而第3点是当我们需要针对某一条语句来配置二级缓存时候则可以使用。
 *
 * <p>注意事项
 * 不过开启二级缓存的时候有两点需要注意：
 *  1、需要commit事务之后才会生效
 *  2、如果使用的是默认缓存，那么结果集对象需要实现序列化接口(Serializable)
 *
 * <p>二级缓存启动时机
 * 既然一级缓存默认是开启的，而二级缓存是需要我们手动开启的，那么我们什么时候应该开启二级缓存呢？
 *    1、因为二级缓存针对的是同一个namespace，所以建议是在单表操作的Mapper中使用，或者是在相关表的Mapper文件中共享同一个缓存。
 *    2、因为所有的update操作(insert,delete,uptede)都会触发缓存的刷新，从而导致二级缓存失效，所以二级缓存适合在读多写少的场景中开启。
 *    这一点不需要多说，所有人都应该清楚。记住，这一点需要保证在1的前提下才可以！
 *
 * <p>自定义缓存
 * 一级缓存可能存在脏读情况，那么二级缓存是否也可能存在呢？
 * 是的，默认的二级缓存毕竟也是存储在本地缓存，所以对于微服务下是可能出现脏读的情况的，所以这时候我们可能会需要自定义缓存，
 * 比如利用 redis 来存储缓存，而不是存储在本地内存当中。
 * MyBatis 官方提供的第三方缓存，如：MyBatis 官方提供的第三方缓存 mybatis-redis
 */
public class CachingExecutor implements Executor {

  private final Executor delegate;
  // 用来管理二级缓存
  private final TransactionalCacheManager tcm = new TransactionalCacheManager();

  public CachingExecutor(Executor delegate) {
    this.delegate = delegate;
    delegate.setExecutorWrapper(this);
  }

  @Override
  public Transaction getTransaction() {
    return delegate.getTransaction();
  }

  @Override
  public void close(boolean forceRollback) {
    try {
      // issues #499, #524 and #573
      if (forceRollback) {
        tcm.rollback();
      } else {
        tcm.commit();
      }
    } finally {
      delegate.close(forceRollback);
    }
  }

  @Override
  public boolean isClosed() {
    return delegate.isClosed();
  }

  @Override
  public int update(MappedStatement ms, Object parameterObject) throws SQLException {
    flushCacheIfRequired(ms);
    return delegate.update(ms, parameterObject);
  }

  @Override
  public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
    flushCacheIfRequired(ms);
    return delegate.queryCursor(ms, parameter, rowBounds);
  }

  @Override
  public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    BoundSql boundSql = ms.getBoundSql(parameterObject);
    CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
    return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
  }

  @Override
  public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql)
      throws SQLException {
    Cache cache = ms.getCache();
    if (cache != null) {
      // 如果sql语句中配置了 flushCache=true，就会执行刷新缓存
      flushCacheIfRequired(ms);
      if (ms.isUseCache() && resultHandler == null) {
        ensureNoOutParams(ms, boundSql); // 存储过程相关的，可忽略
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) tcm.getObject(cache, key); // 从二级缓存中获取结果
        if (list == null) {
          // 二级缓存不存在，就执行查询，这个查询实际也是先走一级缓存查询，一级缓存没有的话，就走数据库查询
          list = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
          // 注意，二级缓存存储的时候会先存储到一个临时属性中，直到事务提交才保存到真实的二级缓存中，目的是防止脏读
          tcm.putObject(cache, key, list); // issue #578 and #116
        }
        return list;
      }
    }
    return delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
  }

  @Override
  public List<BatchResult> flushStatements() throws SQLException {
    return delegate.flushStatements();
  }

  @Override
  public void commit(boolean required) throws SQLException {
    delegate.commit(required);
    tcm.commit();
  }

  @Override
  public void rollback(boolean required) throws SQLException {
    try {
      delegate.rollback(required);
    } finally {
      if (required) {
        tcm.rollback();
      }
    }
  }

  private void ensureNoOutParams(MappedStatement ms, BoundSql boundSql) {
    if (ms.getStatementType() == StatementType.CALLABLE) {
      for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
        if (parameterMapping.getMode() != ParameterMode.IN) {
          throw new ExecutorException("Caching stored procedures with OUT params is not supported.  Please configure useCache=false in " + ms.getId() + " statement.");
        }
      }
    }
  }

  @Override
  public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
    return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
  }

  @Override
  public boolean isCached(MappedStatement ms, CacheKey key) {
    return delegate.isCached(ms, key);
  }

  @Override
  public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
    delegate.deferLoad(ms, resultObject, property, key, targetType);
  }

  @Override
  public void clearLocalCache() {
    delegate.clearLocalCache();
  }

  private void flushCacheIfRequired(MappedStatement ms) {
    Cache cache = ms.getCache();
    if (cache != null && ms.isFlushCacheRequired()) {
      tcm.clear(cache);
    }
  }

  @Override
  public void setExecutorWrapper(Executor executor) {
    throw new UnsupportedOperationException("This method should not be called");
  }

}
