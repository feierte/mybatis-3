package org.sperri.orm;

/**
 * 映射配置类，用于存放mapper.xml解析出来的内容
 * @author Jie Zhao
 * @date 2021/3/15 20:19
 */
public class MappedStatement {

  // id标识
  private String id;
  // 返回值类型
  private String resultType;
  // 参数值类型
  private String parameterType;
  // SQL语句
  private String sql;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public String getParameterType() {
    return parameterType;
  }

  public void setParameterType(String parameterType) {
    this.parameterType = parameterType;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
