package org.sperri.orm;

import java.io.InputStream;

/**
 * 加载配置文件的工具类
 *
 * @author Jie Zhao
 * @date 2021/3/15 20:24
 */
public class Resources {

  public static InputStream getResourceAsStream(String path) {

    return Resources.class.getClassLoader().getResourceAsStream(path);
  }
}
