package com.ijpay.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource( "classpath:alipay.yml")
@ConfigurationProperties(prefix = "alipay")
public class AliPayBean {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String serverUrl;
    private String notify_domain;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getNotify_domain() {
        return notify_domain;
    }

    public void setNotify_domain(String notify_domain) {
        this.notify_domain = notify_domain;
    }

    @Override
    public String toString() {
        return "AliPayBean{" +
                "appId='" + appId + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", notify_domain='" + notify_domain + '\'' +
                '}';
    }
}
