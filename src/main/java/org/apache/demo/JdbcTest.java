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
