package org.apache.ibatis.demo.mapper;

import org.apache.ibatis.demo.domain.Order;

import java.util.List;

public interface OrderMapper {
  public List<Order> findOrderAndUser();
}
