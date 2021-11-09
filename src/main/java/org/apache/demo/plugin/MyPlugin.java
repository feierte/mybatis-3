package org.apache.demo.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;

/**
 * @author Jie Zhao
 * @date 2021/3/27 13:52
 */
@Intercepts({ // 注意看这个大括号，也就是说这里可以定义多个 @Signature 对多个地方拦截，都用这个拦截器
  @Signature(type = StatementHandler.class, // 这是指拦截哪个接口
    method = "prepare", // 拦截这个接口内的哪个方法，注意方法名不要拼错了
    args = {Connection.class, Integer.class} // 这是拦截方法的入参，按顺序写到这里，不要多也不要少，如果方法重载，可以通过方法名和入参来唯一确定的
  )
})
public class MyPlugin implements Interceptor {

  /**
   * 这是是每次执行操作的时候，都会进入这个拦截器的方法内
   * @param invocation
   * @return
   * @throws Throwable
   */
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 增强逻辑
    System.out.println("对方法进行了增强.....");
    Object result = invocation.proceed(); // 执行原方法
    return result;
  }

  /**
   * 主要是为了把这个拦截器拦截的接口生成一个代理放到拦截器链中
   * @param target 要拦截的接口
   * @return 返回生成的代理对象
   */
  @Override
  public Object plugin(Object target) {
    System.out.println("将要包装的目标对象：" + target);
    return Plugin.wrap(target, this);
  }
}
