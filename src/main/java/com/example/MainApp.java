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
package com.example;

import com.example.mapper.UserMapper;
import com.example.model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.h2.tools.Server;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Jie Zhao
 * @date 2025/11/3 21:27
 */
public class MainApp {
  public static void main(String[] args) throws SQLException {
    // 启动 H2 Web 控制台
    Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
    System.out.println("H2 Web Console started at: " + webServer.getURL());

    // mybatis 代码
    SqlSessionFactory sqlSessionFactory = null;
    SqlSession sqlSession = null;

    try {
      // 1. 读取MyBatis配置文件
      String resource = "config/mybatis-config.xml";
      InputStream inputStream = Resources.getResourceAsStream(resource);
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      inputStream.close();

      // 2. 获取SqlSession对象
      sqlSession = sqlSessionFactory.openSession();

      // 3. 获取Mapper接口的代理实现
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);


      // 提交事务 (因为使用了JDBC事务管理器)
      sqlSession.commit();

    } catch (Exception e) {
      e.printStackTrace();
      if (sqlSession != null) {
        sqlSession.rollback(); // 出错时回滚
      }
    } finally {
      if (sqlSession != null) {
        sqlSession.close(); // 关闭资源
      }
    }
  }

  /**
   * 手动创建users表
   */
  private static void createTable(SqlSession session) throws SQLException {
    String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
      "id INT AUTO_INCREMENT PRIMARY KEY, " +
      "name VARCHAR(100) NOT NULL, " +
      "email VARCHAR(100) UNIQUE NOT NULL" +
      ")";
    session.getConnection().createStatement().execute(createTableSQL);
    System.out.println("表 'users' 已创建或已存在。");
  }
}
