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
