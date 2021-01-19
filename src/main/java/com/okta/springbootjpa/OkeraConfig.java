package com.okta.springbootjpa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="okera")
public class OkeraConfig {
  // Planner host/port to connect to okera
  private String plannerHost;
  private int plannerPort;

  // Default database, this is almost always the same as the database for the
  // datasource
  private String defaultDb;

  // System credentials to connect to the planner. This is typically not the
  // end user, but the system account that the spring application is connecting
  // as.
  private String systemUser;
  private String systemToken;

  public void setPlannerPort(int port) { this.plannerPort = port; }
  public int getPlannerPort() { return plannerPort; }
  public void setPlannerHost(String host) { this.plannerHost = host; }
  public String getPlannerHost() { return plannerHost; }
  public void setDefaultDb(String db) { this.defaultDb = db; }
  public String getDefaultDb() { return defaultDb; }
  public void setSystemUser(String user) { this.systemUser = user; }
  public String getSystemUser() { return systemUser; }
  public void setSystemToken(String token) { this.systemToken = token; }
  public String getSystemToken() { return systemToken; }

}
