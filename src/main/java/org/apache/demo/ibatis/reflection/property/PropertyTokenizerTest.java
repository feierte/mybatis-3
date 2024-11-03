/**
 *    Copyright 2009-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
