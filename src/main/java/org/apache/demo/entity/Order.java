package org.apache.demo.entity;

public class Order {

  private Integer id;
  private Double price;
  private Double gpsy; // 随便造的一个属性

  private User user;
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getGpsy() {
    return gpsy;
  }

  public void setGpsy(Double gpsy) {
    this.gpsy = gpsy;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "Order{" +
      "id=" + id +
      ", price=" + price +
      ", gpsy=" + gpsy +
      ", user=" + user +
      '}';
  }
}
