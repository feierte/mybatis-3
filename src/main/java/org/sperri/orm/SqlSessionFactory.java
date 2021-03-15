package org.sperri.orm;

/**
 * @author Jie Zhao
 * @date 2021/3/15 20:25
 */
public interface SqlSessionFactory {

  SqlSession openSqlSession();
}
