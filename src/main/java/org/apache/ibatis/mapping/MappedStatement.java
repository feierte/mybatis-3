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
package org.apache.ibatis.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * @author Clinton Begin
 *
 * @apiNote
 * 一个 MappedStatement 对象对应一个 mapper.xml 中的一个SQL节点（select、insert、update、delete）。
 * MappedStatement 是不可变对象（immutable），一旦构建完成，其内容不可更改。
 * <p>MappedStatement 实例由 XMLStatementBuilder 或 MapperAnnotationBuilder 在 MyBatis 启动时解析 Mapper XML 或注解时创建，
 * 并注册到 Configuration.mappedStatements（一个 Map<String, MappedStatement>）中。
 */
public final class MappedStatement {

  private String resource; // mapper映射文件的路径，例如：com/example/mapper/UserMapper.xml
  private Configuration configuration;
  // 节点的 id 属性加命名空间: namespace.id，例如：com.lucky.mybatis.dao.UserMapper.selectByExample
  private String id;
  // 尝试影响驱动程序每次批量返回的结果行数和这个设置值相等
  private Integer fetchSize;
  // SQL超时时间
  private Integer timeout;
  // Statement的类型，STATEMENT（直接操作SQL，不进行预编译）/PREPARE（预处理参数，进行预编译，获取数据）/CALLABLE（执行存储过程）
  // 决定在操作数据库时（Executor中创建StatementHandler时起作用），使用的是Statement、PreparedStatement还是CallableStatement
  private StatementType statementType;
  // 结果集类型，FORWARD_ONLY / SCROLL_SENSITIVE/SCROLL_INSENSITIVE
  private ResultSetType resultSetType;
  // 解析后的 SQL 语句（可能包含动态 SQL）
  private SqlSource sqlSource;
  // 二级缓存
  private Cache cache; // 执行 CRUD 时，所使用的缓存对象
  // 请求参数映射，已废弃，目前该属性已经被行内参数映射和 parameterType 属性所取代
  private ParameterMap parameterMap;
  // 结果映射列表（支持多结果集），对应 Mapper.xml 文件中的 resultMap
  private List<ResultMap> resultMaps;
  // 是否清空缓存（对 UPDATE/INSERT/DELETE 默认为 true）
  // 控制在执行 sql 后，是否刷新缓存，对应 flushCache 属性
  private boolean flushCacheRequired;
  // 是否使用二级缓存（SELECT 默认为 true），对应 useCache 属性
  private boolean useCache;
  // 用于嵌套结果映射是否有序
  private boolean resultOrdered;
  // SQL 类型，INSERT/SELECT/DELETE/UPDATE
  private SqlCommandType sqlCommandType;
  // 主键生成器（如 Jdbc3KeyGenerator）
  private KeyGenerator keyGenerator;
  // 主键属性名（用于回填）
  private String[] keyProperties;
  // 主键列名（数据库列）
  private String[] keyColumns;
  // 是否包含嵌套 ResultMap，是否存在嵌套映射结果集
  private boolean hasNestedResultMaps;
  // 数据库厂商标识（用于多数据库适配），用来区分不同环境
  // MyBatis 会加载带有匹配当前数据库的 databaseId 属性的语句和所有不带 databaseId 属性的语句。 如果同时找到带有
  // databaseId 和不带 databaseId 的相同语句，则后者会被舍弃。
  private String databaseId;
  // 日志对象
  private Log statementLog;
  // SQL 语言驱动（如 XMLLanguageDriver）
  private LanguageDriver lang; // 语言解释器
  // 多结果集名称（用于存储过程）
  private String[] resultSets;

  MappedStatement() {
    // constructor disabled
  }

  public static class Builder {
    private MappedStatement mappedStatement = new MappedStatement();

    public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType) {
      mappedStatement.configuration = configuration;
      mappedStatement.id = id;
      mappedStatement.sqlSource = sqlSource;
      mappedStatement.statementType = StatementType.PREPARED;
      mappedStatement.resultSetType = ResultSetType.DEFAULT;
      mappedStatement.parameterMap = new ParameterMap.Builder(configuration, "defaultParameterMap", null, new ArrayList<>()).build();
      mappedStatement.resultMaps = new ArrayList<>();
      mappedStatement.sqlCommandType = sqlCommandType;
      mappedStatement.keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
      String logId = id;
      if (configuration.getLogPrefix() != null) {
        logId = configuration.getLogPrefix() + id;
      }
      mappedStatement.statementLog = LogFactory.getLog(logId);
      mappedStatement.lang = configuration.getDefaultScriptingLanguageInstance();
    }

    public Builder resource(String resource) {
      mappedStatement.resource = resource;
      return this;
    }

    public String id() {
      return mappedStatement.id;
    }

    public Builder parameterMap(ParameterMap parameterMap) {
      mappedStatement.parameterMap = parameterMap;
      return this;
    }

    public Builder resultMaps(List<ResultMap> resultMaps) {
      mappedStatement.resultMaps = resultMaps;
      for (ResultMap resultMap : resultMaps) {
        mappedStatement.hasNestedResultMaps = mappedStatement.hasNestedResultMaps || resultMap.hasNestedResultMaps();
      }
      return this;
    }

    public Builder fetchSize(Integer fetchSize) {
      mappedStatement.fetchSize = fetchSize;
      return this;
    }

    public Builder timeout(Integer timeout) {
      mappedStatement.timeout = timeout;
      return this;
    }

    public Builder statementType(StatementType statementType) {
      mappedStatement.statementType = statementType;
      return this;
    }

    public Builder resultSetType(ResultSetType resultSetType) {
      mappedStatement.resultSetType = resultSetType == null ? ResultSetType.DEFAULT : resultSetType;
      return this;
    }

    public Builder cache(Cache cache) {
      mappedStatement.cache = cache;
      return this;
    }

    public Builder flushCacheRequired(boolean flushCacheRequired) {
      mappedStatement.flushCacheRequired = flushCacheRequired;
      return this;
    }

    public Builder useCache(boolean useCache) {
      mappedStatement.useCache = useCache;
      return this;
    }

    public Builder resultOrdered(boolean resultOrdered) {
      mappedStatement.resultOrdered = resultOrdered;
      return this;
    }

    public Builder keyGenerator(KeyGenerator keyGenerator) {
      mappedStatement.keyGenerator = keyGenerator;
      return this;
    }

    public Builder keyProperty(String keyProperty) {
      mappedStatement.keyProperties = delimitedStringToArray(keyProperty);
      return this;
    }

    public Builder keyColumn(String keyColumn) {
      mappedStatement.keyColumns = delimitedStringToArray(keyColumn);
      return this;
    }

    public Builder databaseId(String databaseId) {
      mappedStatement.databaseId = databaseId;
      return this;
    }

    public Builder lang(LanguageDriver driver) {
      mappedStatement.lang = driver;
      return this;
    }

    public Builder resultSets(String resultSet) {
      mappedStatement.resultSets = delimitedStringToArray(resultSet);
      return this;
    }

    /**
     * Resul sets.
     *
     * @param resultSet
     *          the result set
     * @return the builder
     * @deprecated Use {@link #resultSets}
     */
    @Deprecated
    public Builder resulSets(String resultSet) {
      mappedStatement.resultSets = delimitedStringToArray(resultSet);
      return this;
    }

    public MappedStatement build() {
      assert mappedStatement.configuration != null;
      assert mappedStatement.id != null;
      assert mappedStatement.sqlSource != null;
      assert mappedStatement.lang != null;
      mappedStatement.resultMaps = Collections.unmodifiableList(mappedStatement.resultMaps);
      return mappedStatement;
    }
  }

  public KeyGenerator getKeyGenerator() {
    return keyGenerator;
  }

  public SqlCommandType getSqlCommandType() {
    return sqlCommandType;
  }

  public String getResource() {
    return resource;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public String getId() {
    return id;
  }

  public boolean hasNestedResultMaps() {
    return hasNestedResultMaps;
  }

  public Integer getFetchSize() {
    return fetchSize;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public StatementType getStatementType() {
    return statementType;
  }

  public ResultSetType getResultSetType() {
    return resultSetType;
  }

  public SqlSource getSqlSource() {
    return sqlSource;
  }

  public ParameterMap getParameterMap() {
    return parameterMap;
  }

  public List<ResultMap> getResultMaps() {
    return resultMaps;
  }

  public Cache getCache() {
    return cache;
  }

  public boolean isFlushCacheRequired() {
    return flushCacheRequired;
  }

  public boolean isUseCache() {
    return useCache;
  }

  public boolean isResultOrdered() {
    return resultOrdered;
  }

  public String getDatabaseId() {
    return databaseId;
  }

  public String[] getKeyProperties() {
    return keyProperties;
  }

  public String[] getKeyColumns() {
    return keyColumns;
  }

  public Log getStatementLog() {
    return statementLog;
  }

  public LanguageDriver getLang() {
    return lang;
  }

  public String[] getResultSets() {
    return resultSets;
  }

  /**
   * Gets the resul sets.
   *
   * @return the resul sets
   * @deprecated Use {@link #getResultSets()}
   */
  @Deprecated
  public String[] getResulSets() {
    return resultSets;
  }

  /**
   * 该类中核心方法。它根据传入的参数对象，结合 SqlSource，生成最终可执行的 BoundSql 对象。
   * @param parameterObject
   * @return
   */
  public BoundSql getBoundSql(Object parameterObject) {
    // 解析 sql 生成最终 SQL 字符串（已替换 #{} 为 ?）
    // MappedStatement 的 SqlSource 在解析 #{} 时会生成 ParameterMapping，而 ${} 会被直接替换为字符串（无参数绑定）。
    BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings == null || parameterMappings.isEmpty()) {
      boundSql = new BoundSql(configuration, boundSql.getSql(), parameterMap.getParameterMappings(), parameterObject);
    }

    // check for nested result maps in parameter mappings (issue #30)
    for (ParameterMapping pm : boundSql.getParameterMappings()) {
      String rmId = pm.getResultMapId();
      if (rmId != null) {
        ResultMap rm = configuration.getResultMap(rmId);
        if (rm != null) {
          hasNestedResultMaps |= rm.hasNestedResultMaps();
        }
      }
    }

    return boundSql;
  }

  private static String[] delimitedStringToArray(String in) {
    if (in == null || in.trim().length() == 0) {
      return null;
    } else {
      return in.split(",");
    }
  }

}
