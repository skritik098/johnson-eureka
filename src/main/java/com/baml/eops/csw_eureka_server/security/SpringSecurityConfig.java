package com.baml.ocst.eureka.security;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.BeanIds; 
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityConfig.class);
    @Value("${csw.eureka.enable.ip.filter}")
    private String enableIpFilter;
    @Autowired
    private CustomIpAuthenticationProvider ipAuthenticationProvider;
    @Autowired
    private CustomIpFilter customIpFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOGGER.info("csw.eureka.enable.ip.filter: {}",enableIpFilter);

        if(StringUtils.equalsIgnoreCase("Y",enableIpFilter)) {
            http.addFilterAfter(customIpFilter,BasicAuthenticationFilter.class)
                .httpBasic()
                .and().authorizeRequests()
                .antMatchers("/","/eureka/js/**","/eureka/css/**","/eureka/fonts/**","/eureka/images/**","/**/favicon.ico","/error").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
        }
        else{
            http.httpBasic().and().authorizeRequests().antMatchers("/","/**").permitAll()
            .and().csrf().disable();
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ipAuthenticationProvider);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    // @Override
    public AuthenticationManager AuthenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

