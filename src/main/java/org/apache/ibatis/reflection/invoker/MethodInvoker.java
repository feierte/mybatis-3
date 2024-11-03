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
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.ibatis.reflection.Reflector;

/**
 * @author Clinton Begin
 *
 * @apiNote 对类中属性对应 set 方法或者 get 方法的封装
 *
 * <p>在其构造函数中，设置 set 方法或者 get 方法，并获取其参数类型或者返回值类型进行保存，也就是该属性的类型。
 * 如果类中有些属性没有 set 或者 get 方法，那么这些属性会被封装成下面两个对象（final static修饰的字段不会被封装）：
 *  org.apache.ibatis.reflection.invoker.SetFieldInvoker、
 *  org.apache.ibatis.reflection.invoker.GetFieldInvoker
 * 用于设置或者获取他们的值
 */
public class MethodInvoker implements Invoker {

  private final Class<?> type;
  private final Method method;

  public MethodInvoker(Method method) {
    this.method = method;

    if (method.getParameterTypes().length == 1) {
      // 参数个数为1时，一般是 setter 方法，设置 type 为方法参数[0]的类型
      type = method.getParameterTypes()[0];
    } else {
      // 否则，一般是 getter 方法，设置 type 为返回类型
      type = method.getReturnType();
    }
  }

  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    try {
      return method.invoke(target, args);
    } catch (IllegalAccessException e) {
      if (Reflector.canControlMemberAccessible()) {
        method.setAccessible(true);
        return method.invoke(target, args);
      } else {
        throw e;
      }
    }
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
