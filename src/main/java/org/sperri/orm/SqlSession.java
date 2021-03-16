package org.sperri.orm;

import java.util.List;

/**
 * @author Jie Zhao
 * @date 2021/3/15 20:26
 */
public interface SqlSession {

  <T> List<T> selectList(String statementId, Object...params);


  <T> T selectOne(String statementId, Object...params);
}
