package org.apache.ibatis.demo;

import org.apache.ibatis.demo.util.ReflectionUtils;

public class MybatisMain {

  public static void main(String[] args) throws Exception {
    System.out.println("Hello World!!!");

    Class<MybatisMain> mainClass = MybatisMain.class;
    ReflectionUtils.findClassesInPackage(mainClass.getPackageName());
  }
}
