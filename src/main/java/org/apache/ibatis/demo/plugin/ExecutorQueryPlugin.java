package org.apache.ibatis.demo.plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

@Slf4j
@Intercepts({
  @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})}
)
public class ExecutorQueryPlugin implements Interceptor {
  private Properties props = new Properties();

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // todo: pre process
    log.info("pre process before execute query.");
    Object proceed = invocation.proceed();
    // todo: post process
    log.info("post process before execute query.");
    System.out.println(this.props);
    return proceed;
  }

  @Override
  public void setProperties(Properties properties) {
    this.props = properties;
  }
}
