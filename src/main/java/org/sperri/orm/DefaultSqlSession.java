package org.sperri.orm;

import java.util.List;

/**
 * @author Jie Zhao
 * @date 2021/3/15 20:27
 */
public class DefaultSqlSession implements SqlSession {

  private Configuration configuration;

  public DefaultSqlSession(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public <T> List<T> selectList(String statementId, Object... params) {

    SimpleExecutor simpleExecutor = new SimpleExecutor();
    List<T> query = simpleExecutor.query(configuration, configuration.getMappedStatementMap().get(statementId), params);
    return query;
  }

  @Override
  public <T> T selectOne(String statementId, Object... params) {
    List<T> objects = selectList(statementId, params);
    if (objects.size() == 1) {
      return objects.get(0);
    } else {
      throw new RuntimeException("查询结果为空，或结果过多");
    }
  }
}
