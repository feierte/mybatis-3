package org.sperri.orm;

import org.sperri.orm.util.ParameterMapping;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
      for (int i=0; i < parameterMappingList.size(); i++) {
        ParameterMapping parameterMapping = parameterMappingList.get(i);
        String content = parameterMapping.getContent();
        // 反射，获取实体中对应的属性值
        Field declaredField = parameterClass.getDeclaredField(content);
        declaredField.setAccessible(true);
        Object o = declaredField.get(params[0]);
        preparedStatement.setObject(i+1, o);
      }


    } catch (Exception e) {

    }
    return null;
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
   *  1、将#{}用？代替
   *  2、解析出#{}里面的值进行存储
   * @param sql
   * @return
   */
  private BoundSql getBoundSql(String sql) {
    return null;
  }
}
