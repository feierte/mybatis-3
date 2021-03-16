package org.sperri.orm;

import org.sperri.orm.util.ParameterMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jie Zhao
 * @date 2021/3/16 21:50
 */
public class BoundSql {

  private String sqlTest; // 解析过后的sql

  private List<ParameterMapping> parameterMappingList = new ArrayList<>();

  public BoundSql(String sqlTest, List<ParameterMapping> parameterMappingList) {
    this.sqlTest = sqlTest;
    this.parameterMappingList = parameterMappingList;
  }

  public String getSqlTest() {
    return sqlTest;
  }

  public void setSqlTest(String sqlTest) {
    this.sqlTest = sqlTest;
  }

  public List<ParameterMapping> getParameterMappingList() {
    return parameterMappingList;
  }

  public void setParameterMappingList(List<ParameterMapping> parameterMappingList) {
    this.parameterMappingList = parameterMappingList;
  }
}
