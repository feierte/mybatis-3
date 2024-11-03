package org.apache.demo.mapper;

/**
 * @author Jie Zhao
 * @date 2024/11/3 10:16
 */
public interface TimeMapper {

  /**
   * 用于演示 mapper 中的 databaseId 属性。
   *
   * <p/>
   * 获取当前数据库的当前时间，如果是 mysql 数据库，获取的就是 mysql 数据库当前时间，
   * 如果是 postgresql 数据库，获取的就是 postgresql 数据库当前时间。
   * @return 当前时间的字符串表示形式
   */
  String getTime();
}
