package com.okta.springbootjpa;

import com.okera.springdata.OkeraQueryTransformer;

import java.lang.reflect.Method;
import javax.sql.DataSource;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * This is a class that can be added to a spring boot application to add
 * proxy wrappers to existing datasources to augment this behavior. This
 * can do things such as improving logging, testing for slow queries, dynamic
 * query rewrites, etc.
 *
 * Depending on the application, this can be a very convenient way to augment
 * existing database capabilities with no other application changes. Simply
 * including or removing this class is sufficient.
 *
 * This is done as a Bean processor instead of a just as a custom data source
 * bean in order to make it easy to integrate into the application. While the
 * bean approach (implement 'public DataSource dataSource()' can work as well
 * spring boot has different behavior with custom data sources that interacts
 * with configuration mechanisms. This can force the application to require
 * altering how it does data source configurations as well, which is not ideal.
 *
 * This is heavily based on this repository:
 * https://arnoldgalovics.com/configuring-a-datasource-proxy-in-spring-boot/
 */
@Component
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(
      Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(
      Object bean, String beanName) throws BeansException {
    if (bean instanceof DataSource) {
      // Only proxy creation of DataSource objects
      ProxyFactory factory = new ProxyFactory(bean);
      factory.setProxyTargetClass(true);
      factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
      return factory.getProxy();
    }
    return bean;
  }

  private static class ProxyDataSourceInterceptor implements MethodInterceptor {
    private final DataSource dataSource;

    public ProxyDataSourceInterceptor(final DataSource dataSource) {
      super();
      //this.dataSource = createLoggingDatasource(dataSource, false);
      this.dataSource = createQueryRewriter(dataSource);
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
      Method proxyMethod = ReflectionUtils.findMethod(
        dataSource.getClass(), invocation.getMethod().getName());
      if (proxyMethod != null) {
        return proxyMethod.invoke(dataSource, invocation.getArguments());
      }
      return invocation.proceed();
    }
  }

  /**
   * Default query formatter.
   */
  private static final class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
    private Formatter formatter = FormatStyle.BASIC.getFormatter();

    @Override
    protected String formatQuery(String query) {
      return this.formatter.format(query);
    }
  }

  /**
   * Creates a logging wrapper, for demonstration, on every call to the underlying
   * data source.
   */
  private static DataSource createLoggingDatasource(final DataSource dataSource,
      boolean includeMethodCalls) {
    PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
    creator.setMultiline(true);
    SystemOutQueryLoggingListener listener = new SystemOutQueryLoggingListener();
    listener.setQueryLogEntryCreator(creator);

    ProxyDataSourceBuilder builder = ProxyDataSourceBuilder.create(dataSource)
      .name("okera-datasource-proxy")
      .listener(listener)
      .proxyResultSet();

    if (includeMethodCalls) {
      builder = builder.afterMethod(executionContext -> {
          // print out JDBC API calls to console
          Method method = executionContext.getMethod();
          Class<?> targetClass = executionContext.getTarget().getClass();
          System.out.println(
              "JDBC: " + targetClass.getSimpleName() + "#" + method.getName());
          });
    }
    builder = builder.afterQuery((execInfo, queryInfoList) -> {
        System.out.println("Query took " + execInfo.getElapsedTime() + "msec");
        });
    return builder.build();
  }

  private static DataSource createQueryRewriter(final DataSource dataSource) {
    OkeraQueryTransformer transformer = new OkeraQueryTransformer();
    // Hard coded as an example, these values can be configured any way, including
    // from the environment variables.
    transformer.setHost("localhost");
    transformer.setPort(12050);
    transformer.setSystemUser("root");
    transformer.setDefaultDb("springbootjpa");

    // User to run the request as. This is the user logged into the application.
    //transformer.setUser("root");
    transformer.setUser("springbootjpa_user");

    return ProxyDataSourceBuilder.create(dataSource)
      .name("okera-datasource-proxy")
      .proxyResultSet()
      .queryTransformer(transformer)
      .build();
  }
}
