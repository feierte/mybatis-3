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

import static org.junit.jupiter.api.Assertions.assertSame;

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

  /**
   * 测试 Mybatis 的一级缓存
   */
  @Test
  public void testFirstLevelCache() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
      // 第一次查询
      List<Order> orders1 = orderMapper.findOrderAndUser();
      System.out.println(orders1);

      // 第二次查询
      List<Order> orders2 = orderMapper.findOrderAndUser();
      System.out.println(orders2);
      // assertNotSame(orders1, orders2);
      assertSame(orders1, orders2);
    }
  }
}
