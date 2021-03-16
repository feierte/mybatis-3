package org.sperri.orm;

import java.io.InputStream;

/**
 * 加载配置文件的工具类
 * @author Jie Zhao
 * @date 2021/3/15 20:24
 */
public class Resources {

  public static InputStream getResourceAsStream(String path) {

    ClassLoader classLoader = Resources.class.getClassLoader();

    InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(path);
    if (inputStream == null) {
      classLoader = Thread.currentThread().getContextClassLoader();
      inputStream = classLoader.getResourceAsStream(path);
    }
    return inputStream;
  }
}
