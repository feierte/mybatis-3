package org.apache.ibatis.demo.mapper;

import org.apache.ibatis.demo.domain.Order;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class TestOrderMapper {

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
  public void testFindOrderAndUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
      List<Order> orders = orderMapper.findOrderAndUser();
      System.out.println(orders);
    }
  }
}
