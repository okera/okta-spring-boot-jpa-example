package com.okta.springbootjpa;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .anyRequest().authenticated()
        .and().requestCache().requestCache(new NullRequestCache())
        .and().httpBasic()
        .and().csrf().disable();
  }

  @Autowired
  void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    // Add some test users
    auth.inMemoryAuthentication()
        .withUser("user").password("{noop}password").authorities("ROLE_USER")
        .and()
        .withUser("springbootjpa_user").password("{noop}password")
            .authorities("ROLE_USER")
        .and()
        .withUser("root").password("{noop}password")
            .authorities("ROLE_USER", "ROLE_ADMIN");
  }
}
