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

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心配置类：用于存放sqlMapConfig.xml解析出来的内容
 * @author Jie Zhao
 * @date 2021/3/15 20:19
 */
public class Configuration {

  private DataSource dataSource;

  /**
   * key: statementId
   */
  private Map<String, MappedStatement> mappedStatementMap = new HashMap<>();

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Map<String, MappedStatement> getMappedStatementMap() {
    return mappedStatementMap;
  }

  public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
    this.mappedStatementMap = mappedStatementMap;
  }
}
