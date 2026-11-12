package com.concord.paymentservice.config;

import com.cybersource.authsdk.core.ConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
public class CybersourceConfig  {

@Bean
    public com.cybersource.authsdk.core.MerchantConfig merchantConfig(CybersourceProperties props) throws ConfigException {
    java.util.Properties p = new java.util.Properties();
    p.setProperty("authenticationType", props.getAuthenticationType());
    p.setProperty("jwtKeyType", props.getJwtKeyType());
    p.setProperty("merchantID", props.getMerchantId());
    p.setProperty("merchantKeyId", props.getKeyId());
    p.setProperty("merchantsecretKey", props.getSharedSecret());
    p.setProperty("runEnvironment", props.getRunEnvironment());
    return new com.cybersource.authsdk.core.MerchantConfig(p);
    }
}
