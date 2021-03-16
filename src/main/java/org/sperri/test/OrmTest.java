package org.sperri.test;

import org.junit.jupiter.api.Test;
import org.sperri.orm.Resources;

import java.io.InputStream;

/**
 * @author Jie Zhao
 * @date 2021/3/15 21:07
 */
public class OrmTest {

  @Test
  public void test() {
    String path = "sqlMapConfig.xml";
    InputStream inputStream = Resources.getResourceAsStream(path);
    System.out.println(inputStream);
  }
}
