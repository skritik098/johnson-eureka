package com.baml.eops.csw_eureka_server.security;

import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith; 
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException; 
import org.springframework.test.util.ReflectionTestUtils;
import com.baml.eops.csw_eureka_server.security.CustomIpFilter;

@RunWith(PowerMockRunner.class)

public class CustomIpFilterTest {
    @Autowired
    @InjectMocks 

    CustomIpFilter customIpfilter;

    @Mock
    HttpServletRequest servletRequest;

    @Mock
    HttpServletResponse servletResponse;

    @Mock
    FilterChain filterChain;

    @Mock
    AuthenticationManager authenticationManager;

    @Before
    public void setUp() throws Exception{
        System.setProperty("SERVICE_ENV_VAR", "DEV");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDoFilter_disableIpFilter() throws Exception{
        try {
            ReflectionTestUtils.setField(customIpfilter, "enableIpFilter", "N");
            customIpfilter.doFilter(servletRequest,servletResponse,filterChain);
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
            //TODO: handle exception
        }
    }

    @Test
    public void testDoFilter_urlExcluded() throws Exception{
        try {
            ReflectionTestUtils.setField(customIpfilter,"enableIpFilter","Y");
            ReflectionTestUtils.setField(customIpfilter,"eurekaAllowedUrl","/,/test");

            List<String> allowedUrlList = Arrays.asList("/,/test".split(","));
            ReflectionTestUtils.setField(customIpfilter,"allowedUrlList",allowedUrlList);

            PowerMockito.when(servletRequest.getRequestURI()).thenReturn("/testing");
            customIpfilter.doFilter(servletRequest,servletResponse,filterChain);
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
            //TODO: handle exception
        }
    }

    @Test
    public void testDoFilter_auth() throws Exception {
        try {
            ReflectionTestUtils.setField(customIpfilter,"enableIpFilter","Y");
            ReflectionTestUtils.setField(customIpfilter,"eurekaAllowedUrl","/,/test");

            List<String> allowedUrlList = Arrays.asList("/,/test".split(","));
            ReflectionTestUtils.setField(customIpfilter,"allowedUrlList",allowedUrlList);

            PowerMockito.when(servletRequest.getRequestURI()).thenReturn("/testing");
            customIpfilter.doFilter(servletRequest,servletResponse,filterChain);

            Mockito.verify(servletRequest,Mockito.atLeast(1)).getRequestURI();
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
            //TODO: handle exception
        }
    }

    @Test
    public void testDoFilter_auth_badCred_exception() throws Exception{
        try {
            ReflectionTestUtils.setField(customIpfilter,"enableIpFilter","Y");
            ReflectionTestUtils.setField(customIpfilter,"eurekaAllowedUrl","/,/test");

            List<String> allowedUrlList = Arrays.asList("/,/test".split(","));
            ReflectionTestUtils.setField(customIpfilter,"allowedUrlList",allowedUrlList);

            PowerMockito.when(servletRequest.getRequestURI()).thenReturn("/testing");
	    //PowerMockito.when(authenticationManager.authenticate(Matchers.any(Authentication.class))).thenThrow(new BadCredentialsException("Bad Credentials"));

            customIpfilter.doFilter(servletRequest,servletResponse,filterChain);

            Mockito.verify(servletRequest,Mockito.atLeast(1)).getRequestURI();
	    //Mockito.verify(authenticationManager, Mockito.atLeast(1)).authenticate(Matchers.any(Authentication.class));
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
            //TODO: handle exception
        }
    }
    @Test
    public void testDoFilter_auth_exception() throws Exception {
        try {
            ReflectionTestUtils.setField(customIpfilter,"enableIpFilter","Y");
            ReflectionTestUtils.setField(customIpfilter,"eurekaAllowedUrl","/,/test");

            List<String> allowedUrlList = Arrays.asList("/,/test".split(","));
            ReflectionTestUtils.setField(customIpfilter,"allowedUrlList",allowedUrlList);

            PowerMockito.when(servletRequest.getRequestURI()).thenReturn("/testing");
           // PowerMockito.when(authenticationManager.authenticate(Matchers.any(Authentication.class))).thenThrow(new SessionAuthenticationException(""));

            customIpfilter.doFilter(servletRequest,servletResponse,filterChain);

            Mockito.verify(servletRequest,Mockito.atLeast(1)).getRequestURI();

            //Mockito.verify(authenticationManager, Mockito.atLeast(1)).authenticate(Matchers.any(Authentication.class));
            
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
            //TODO: handle exception
        }
    }
}
