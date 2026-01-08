package com.anqigou.logistics.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anqigou.logistics.config.WechatSubscribeConfig;
import com.anqigou.logistics.service.WechatSubscribeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WechatSubscribeServiceImpl implements WechatSubscribeService {
    
    @Autowired
    private WechatSubscribeConfig wechatSubscribeConfig;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private static final String SUBSCRIBE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=%s";
    private static final String ACCESS_TOKEN_KEY = "wechat:access_token";
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 7000;
    
    @Override
    public String getAccessToken() {
        try {
            String cachedToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
            if (cachedToken != null) {
                return cachedToken;
            }
            
            String url = String.format(ACCESS_TOKEN_URL, wechatSubscribeConfig.getAppId(), wechatSubscribeConfig.getSecret());
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject result = JSON.parseObject(response.getBody());
                String accessToken = result.getString("access_token");
                if (accessToken != null) {
                    redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
                    log.info("获取 access_token 成功");
                    return accessToken;
                } else {
                    log.error("获取 access_token 失败: {}", result.getString("errmsg"));
                }
            }
        } catch (Exception e) {
            log.error("获取 access_token 异常", e);
        }
        return null;
    }
    
    @Override
    public boolean sendSubscribeMessage(String openId, String templateId, Map<String, Object> data) {
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("access_token 为空，无法发送订阅消息");
                return false;
            }
            
            String url = String.format(SUBSCRIBE_MESSAGE_URL, accessToken);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("touser", openId);
            requestBody.put("template_id", templateId);
            requestBody.put("data", data);
            requestBody.put("miniprogram", Map.of("appid", wechatSubscribeConfig.getAppId()));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject result = JSON.parseObject(response.getBody());
                Integer errcode = result.getInteger("errcode");
                if (errcode != null && errcode == 0) {
                    log.info("订阅消息发送成功: openId={}", openId);
                    return true;
                } else {
                    log.error("订阅消息发送失败: errcode={}, errmsg={}", errcode, result.getString("errmsg"));
                }
            }
        } catch (Exception e) {
            log.error("发送订阅消息异常", e);
        }
        return false;
    }
    
    @Override
    public boolean sendShippingNotification(String openId, String orderNo, String courierCompany, String trackingNo, String shippedTime) {
        try {
            Map<String, Object> data = new HashMap<>();
            
            Map<String, Object> orderNoData = new HashMap<>();
            orderNoData.put("value", orderNo);
            data.put("thing1", orderNoData);
            
            Map<String, Object> courierCompanyData = new HashMap<>();
            courierCompanyData.put("value", courierCompany);
            data.put("thing2", courierCompanyData);
            
            Map<String, Object> trackingNoData = new HashMap<>();
            trackingNoData.put("value", trackingNo);
            data.put("character_string3", trackingNoData);
            
            Map<String, Object> shippedTimeData = new HashMap<>();
            shippedTimeData.put("value", shippedTime);
            data.put("time4", shippedTimeData);
            
            return sendSubscribeMessage(openId, wechatSubscribeConfig.getTemplateId(), data);
        } catch (Exception e) {
            log.error("发送发货通知异常", e);
            return false;
        }
    }
}
