package com.concord.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cybersource")
public class CybersourceProperties {

    private String authenticationType = "jwt";
    private String jwtKeyType = "SHARED_SECRET";
    private String merchantId;
    private String keyId;
    private String sharedSecret;
    private String runEnvironment;

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getJwtKeyType() {
        return jwtKeyType;
    }

    public void setJwtKeyType(String jwtKeyType) {
        this.jwtKeyType = jwtKeyType;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public String getRunEnvironment() {
        return runEnvironment;
    }

    public void setRunEnvironment(String runEnvironment) {
        this.runEnvironment = runEnvironment;
    }
}
