package com.baml.ocst.eureka.security;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest; 
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails; 
import org.springframework.web.filter.GenericFilterBean;

@Configuration
public class CustomIpFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomIpFilter.class);
   // @Value("${csw.eureka.enable.ip.filter}") 
    private String enableIpFilter;
   // @Value("${csw.eureka.allowed.url}")
    private String eurekaAllowedUrl;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    List<String> allowedUrlList;

    @PostConstruct
    public void postconstruct () {
        allowedUrlList = Stream.of(eurekaAllowedUrl).filter(StringUtils::isNotBlank).flatMap(Pattern.compile(",")::splitAsStream).collect(Collectors.toList());
        if(!StringUtils.equalsIgnoreCase("Y", enableIpFilter)) {
             LOGGER.info("IP filter is disabled");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.debug("csw.eureka.enable.ip.filter: {}",enableIpFilter);
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(StringUtils.equalsIgnoreCase("Y", enableIpFilter)) { 
            LOGGER.debug("Logging Request {}, {}, {}",
                request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
            boolean urlExcluded = false;

            for(String regex:allowedUrlList) {
                if(request.getRequestURI().matches(regex)) {
                    urlExcluded = true; 
                    LOGGER.debug("URL: {} is excluded from IP FIlter", request.getRequestURI());                    
                    break;
                }
            }

            if(!urlExcluded) {
                try {
                    LOGGER.debug("1P filter starting for {}, {} {}",
                        request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
                }
                catch (BadCredentialsException e) {
                    SecurityContextHolder.clearContext();
                    LOGGER.warn("{},{} URL: {}", e.getMessage(), request.getMethod(), request.getRequestURI());
                    return;
                    //TODO: handle exception
                }
                catch (AuthenticationException e){
                    SecurityContextHolder.clearContext();
                    LOGGER.warn("IP filter failed. {}, {} URL: {}", e.getMessage(), request.getMethod(), request.getRequestURI());
                    return;
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
