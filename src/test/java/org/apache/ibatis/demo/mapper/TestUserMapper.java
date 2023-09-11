package org.apache.ibatis.demo.mapper;

import org.apache.ibatis.demo.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class TestUserMapper {

  private static SqlSessionFactory sqlSessionFactory;


  @BeforeAll
  public static void beforeAll() {

    try {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("config/mybatis-config.xml"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testFindUserAndRoles() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      List<User> userAndRoles = userMapper.findUserAndRoles();
      System.out.println(userAndRoles);
    }
  }


  @Test
  public void testFindUserById() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      List<User> userAndRoles = userMapper.findUserById(1);
      System.out.println(userAndRoles);
    }
  }
}
