package com.anqigou.logistics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WechatSubscribeConfig {
    
    @Value("${wechat.miniapp.appid}")
    private String appId;
    
    @Value("${wechat.miniapp.secret}")
    private String secret;
    
    @Value("${wechat.subscribe.template-id}")
    private String templateId;
    
    public String getAppId() {
        return appId;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public String getTemplateId() {
        return templateId;
    }
}
