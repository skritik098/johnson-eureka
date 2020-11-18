package com.baml.eops.csw_eureka_server.security;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith; 
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.web.authentication.WebAuthenticationDetails; 
import org.springframework.test.util.ReflectionTestUtils;
//import CustomIpAuthenticationProvider;
import com.baml.ocst.eureka.security.CustomIpAuthenticationProvider;

@RunWith(PowerMockRunner.class)

public class CustomIpAuthenticationProviderTest {
    @Autowired
    @InjectMocks 
    CustomIpAuthenticationProvider authProvider;

    @Mock
    HttpServletRequest servletRequest;

    @Before
    public void setUp() throws Exception{
        System.setProperty("SERVICE_ENV_VAR", "DEV");
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void supports() throws Exception {
        try {
            ReflectionTestUtils.setField(authProvider, "eurekaWhitelistIp", ".*");
            Assert.assertTrue(authProvider.supports(null));
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
            //TODO: handle exception
        }
    }

    @Test
    public void authenticate() throws Exception{
        ReflectionTestUtils.setField(authProvider,"eurekaWhitelistIp","127.0.0.1");
        List<String> whiteList = new ArrayList<>();
        whiteList.add("127.0.0.1");
        ReflectionTestUtils.setField(authProvider,"whiteList",whiteList);
        Authentication auth = PowerMockito.mock(Authentication.class);
        WebAuthenticationDetails details = PowerMockito.mock(WebAuthenticationDetails.class);
        Mockito.when(auth.getDetails()).thenReturn(details);
        Mockito.when(details.getRemoteAddress()).thenReturn("127.0.0.1");

        Authentication authentication = authProvider.authenticate(auth);

        Mockito.verify(auth, Mockito.times(1)).getDetails();
        Mockito.verify(details, Mockito.times(1)).getRemoteAddress();

        Assert.assertNotNull(authentication);
    }

    @Test(expected = BadCredentialsException.class)
    public void authenticate_exception() throws Exception{
        ReflectionTestUtils.setField(authProvider, "eurekaWhitelistIp", ".*");
        List<String> whiteList = Arrays.asList("/,/test".split(","));
        ReflectionTestUtils.setField(authProvider, "whiteList", whiteList);

        Authentication auth = PowerMockito.mock(Authentication.class);
        WebAuthenticationDetails details = PowerMockito.mock(WebAuthenticationDetails.class);
        Mockito.when(auth.getDetails()).thenReturn(details); 
        Mockito.when(details.getRemoteAddress()).thenReturn("127.0.0.1");
        authProvider.authenticate(auth); 
        Assert.fail("hadCredentialsException expected,");
    }
}