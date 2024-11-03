/**
 *    Copyright 2009-2024 the original author or authors.
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
