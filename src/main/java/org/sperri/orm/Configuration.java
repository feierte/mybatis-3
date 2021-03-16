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
