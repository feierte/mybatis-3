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
package org.apache.ibatis.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 方法参数名称解析器，用于解析我们定义的 Mapper 接口的方法
 */
public class ParamNameResolver {

  public static final String GENERIC_NAME_PREFIX = "param";

  private final boolean useActualParamName;

  /**
   * <p>
   * The key is the index and the value is the name of the parameter.<br />
   * The name is obtained from {@link Param} if specified. When {@link Param} is not specified,
   * the parameter index is used. Note that this index could be different from the actual index
   * when the method has special parameters (i.e. {@link RowBounds} or {@link ResultHandler}).
   * </p>
   * <ul>
   * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
   * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
   * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
   * </ul>
   *
   * @apiNote 根据接口方法按顺序记录下接口参数的定义的名字，如果使用 @Param 指定了名字，就会记录这个名字，
   * 如果没有记录，那么就会使用它的序号作为名字。
   */
  private final SortedMap<Integer, String> names;

  private boolean hasParamAnnotation;


  /**
   * 在构造函数中可以看到，目的是获取到该方法的参数名，将参数顺序与参数名进行映射保存在UnmodifiableSortedMap一个不可变的SortedMap集合中，大致逻辑：
   *  1、如果添加了@Param注解，则参数名称为该注解的value值。
   *  2、没有添加@Param注解则尝试获取真实的参数名。
   *    说明：通过反射获取方法的参数名，我们只能获取到 arg0,arg1 的名称，因为jdk8之后这些变量名称没有被编译到class文件中，编译时需要指定-parameters选项，
   *    方法的参数名才会记录到class文件中，运行时我们就可以通过反射机制获取到。
   *  3、还是没有获取到参数名则使用序号标记，一般不会走到这一步。
   * @param config
   * @param method
   */
  public ParamNameResolver(Configuration config, Method method) {
    this.useActualParamName = config.isUseActualParamName();
    final Class<?>[] paramTypes = method.getParameterTypes();
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      if (isSpecialParameter(paramTypes[paramIndex])) {
        // skip special parameters
        continue;
      }
      String name = null;
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        if (annotation instanceof Param) {
          hasParamAnnotation = true;
          name = ((Param) annotation).value();
          break;
        }
      }
      if (name == null) {
        // @Param was not specified.
        if (useActualParamName) {
          name = getActualParamName(method, paramIndex);
        }
        if (name == null) {
          // use the parameter index as the name ("0", "1", ...)
          // gcode issue #71
          name = String.valueOf(map.size());
        }
      }
      map.put(paramIndex, name);
    }
    names = Collections.unmodifiableSortedMap(map);
  }

  private String getActualParamName(Method method, int paramIndex) {
    return ParamNameUtil.getParamNames(method).get(paramIndex);
  }

  private static boolean isSpecialParameter(Class<?> clazz) {
    return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
  }

  /**
   * Returns parameter names referenced by SQL providers.
   *
   * @return the names
   */
  public String[] getNames() {
    return names.values().toArray(new String[0]);
  }

  /**
   * <p>
   * A single non-special parameter is returned without a name.
   * Multiple parameters are named using the naming rule.
   * In addition to the default names, this method also adds the generic names (param1, param2,
   * ...).
   * </p>
   *
   * @param args
   *          the args
   * @return the named params
   *
   * @apiNote 根据实际入参数组返回参数名与参数值的映射，大致逻辑：
   *  1、实际参数为 null 或者参数个数为 0，则直接返回 null
   *  2、没有使用 @Param 注解并且参数个数为 1，则直接返回参数值
   *  3、根据参数顺序与参数名的映射获取到参数名与参数值的映射，而且还会将（param+参数顺序）与参数值进行映射，最后将两种组合的映射返回
   */
  public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    // 入参为null或没有时，参数转换为null
    if (args == null || paramCount == 0) {
      return null;
      // 没有使用@Param注解并且只有一个参数时，返回这一个参数
    } else if (!hasParamAnnotation && paramCount == 1) {
      Object value = args[names.firstKey()];
      return wrapToMapIfCollection(value, useActualParamName ? names.get(0) : null);
    } else { // 使用了@Param注解或有多个参数时，将参数转换为Map1类型，并且还根据参数顺序存储了key为param1,param2的参数。
      final Map<String, Object> param = new ParamMap<>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        param.put(entry.getValue(), args[entry.getKey()]);
        // add generic param names (param1, param2, ...)
        final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
        // ensure not to overwrite parameter named with @Param
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }

  /**
   * Wrap to a {@link ParamMap} if object is {@link Collection} or array.
   *
   * @param object a parameter object
   * @param actualParamName an actual parameter name
   *                        (If specify a name, set an object to {@link ParamMap} with specified name)
   * @return a {@link ParamMap}
   * @since 3.5.5
   *
   * @apiNote wrapCollection处理的是只有一个参数时，集合和数组的类型转换成Map2类型，并且有默认的Key，
   * 从这里你能大概看到为什么<foreach>中默认情况下写的array和list（Map类型没有默认值map）。
   */
  public static Object wrapToMapIfCollection(Object object, String actualParamName) {
    if (object instanceof Collection) {
      ParamMap<Object> map = new ParamMap<>();
      // 这里是为了支持Set类型，需要等到MyBatis 3.3.0版本才能使用。
      map.put("collection", object);
      if (object instanceof List) {
        map.put("list", object);
      }
      Optional.ofNullable(actualParamName).ifPresent(name -> map.put(name, object));
      return map;
    } else if (object != null && object.getClass().isArray()) {
      ParamMap<Object> map = new ParamMap<>();
      map.put("array", object);
      Optional.ofNullable(actualParamName).ifPresent(name -> map.put(name, object));
      return map;
    }
    return object;
  }

}
