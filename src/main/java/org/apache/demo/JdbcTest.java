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
package org.apache.demo;

import java.sql.*;

public class JdbcTest {

  public static void main(String[] args) {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection connection = DriverManager.getConnection("jdbc:mysql://47.102.156.134:3306/cms?characterEncoding=utf-8", "root", "123456");
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("select * from user");
      System.out.println(resultSet);
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
  }
}
