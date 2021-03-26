package org.apache.demo.mapper;

import org.apache.demo.entity.Order;

import java.util.List;

public interface OrderMapper {

  public List<Order> findOrders();
}
