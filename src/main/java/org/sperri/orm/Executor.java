package org.sperri.orm;

import java.util.List;

/**
 * @author Jie Zhao
 * @date 2021/3/15 20:28
 */
public interface Executor {

  <T> List<T> query(Configuration configuration, MappedStatement mappedStatement, Object... params);
}
