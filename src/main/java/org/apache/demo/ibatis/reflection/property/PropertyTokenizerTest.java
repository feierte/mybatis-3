package org.apache.demo.ibatis.reflection.property;

import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * @author Jie Zhao
 * @date 2021/11/4 21:53
 */
public class PropertyTokenizerTest {

  public static void main(String[] args) {

    PropertyTokenizer tokenizer = new PropertyTokenizer("orders[O].items[O].name");

    System.out.println(tokenizer.getName()); // orders
    System.out.println(tokenizer.getIndexedName()); // orders[O]
    System.out.println(tokenizer.getIndex()); // 0
    System.out.println(tokenizer.getChildren()); // items[O].name
  }
}
