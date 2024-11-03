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

import org.sperri.orm.util.ParameterMapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jie Zhao
 * @date 2021/3/15 20:30
 */
public class SimpleExecutor implements Executor {


  @Override
  public <T> List<T> query(Configuration configuration, MappedStatement mappedStatement, Object... params) {

    try {
      Connection connection = configuration.getDataSource().getConnection();
      /*
       * SELECT user_id AS "id", user_name AS "username"
       *       FROM `user`
       *       WHERE user_id = #{id} and user_name = #{username}
       */
      String sql = mappedStatement.getSql();
      BoundSql boundSql = getBoundSql(sql);

      PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlTest());

      String parameterType = mappedStatement.getParameterType();
      Class<?> parameterClass = getClassType(parameterType);
      // 设置sql参数
      List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
      for (int i = 0; i < parameterMappingList.size(); i++) {
        ParameterMapping parameterMapping = parameterMappingList.get(i);
        String content = parameterMapping.getContent();
        // 反射，获取实体中对应的属性值
        Field declaredField = parameterClass.getDeclaredField(content);
        declaredField.setAccessible(true);
        Object o = declaredField.get(params[0]);
        preparedStatement.setObject(i + 1, o);
      }

      // 执行sql
      ResultSet resultSet = preparedStatement.executeQuery();


      String resultType = mappedStatement.getResultType();
      Class<?> resultTypeClass = getClassType(resultType);

      Object o = resultTypeClass.newInstance();
      List<Object> list = new ArrayList<>();
      // 封装sql返回结果
      while (resultSet.next()) {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
          String columnName = metaData.getColumnName(i);
          Object value = resultSet.getObject(columnName);


          PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
          Method writeMethod = propertyDescriptor.getWriteMethod();
          writeMethod.invoke(o, value);
        }
        list.add(o);
      }

      return (List<T>) list;

    } catch (Exception e) {
      return null;
    }
  }

  private Class<?> getClassType(String parameterType) {
    if (parameterType != null) {
      Class<?> aClass = null;
      try {
        aClass = Class.forName(parameterType);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      return aClass;
    }
    return null;
  }

  /**
   * 完成对#{}解析工作：
   * 1、将#{}用？代替
   * 2、解析出#{}里面的值进行存储
   *
   * @param sql
   * @return
   */
  private BoundSql getBoundSql(String sql) {
    return null;
  }
}
