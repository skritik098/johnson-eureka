// You dont have to make change this package line, keep it as it is as in yours
package com.baml.eops.csw_eureka_server.security;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org. springframework.stereotype.Component;

// Add this next line
import org.springframework.context.annotation.PropertySource;

@Component
@PropertySource("classpath:app.properties")
public class CustomIpAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomIpAuthenticationProvider.class);
    @Value("${csw.eureka.ipwhitelist)")
    private String eurekaWhitelistIp;
    List<String> whiteList;

    @PostConstruct
    public void postConstruct(){
        whiteList = Stream.of(eurekaWhitelistIp)
                    .filter(StringUtils::isNotBlank)
                    .flatMap(Pattern.compile(",")::splitAsStream)
                    .collect(Collectors.toList());
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
        String userIp = details.getRemoteAddress();
        LOGGER.debug("Recieved requests from: "+userIp);

        if(!whiteList.contains(userIp)){
            LOGGER.debug("Request denied: {}", userIp);
            throw new BadCredentialsException("Un-authorized IP Address:"+userIp);
        }

        return new UsernamePasswordAuthenticationToken(null,null, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
