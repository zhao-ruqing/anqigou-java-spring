package com.anqigou.logistics.service.impl;

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
            log.info("========== 开始获取 access_token ==========");
            log.info("检查Redis缓存中的access_token");
            String cachedToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
            if (cachedToken != null) {
                log.info("使用缓存的 access_token: {}", cachedToken);
                log.info("========== 获取 access_token 完成（使用缓存） ==========");
                return cachedToken;
            }
            
            log.info("缓存中没有access_token，开始调用微信API获取");
            log.info("微信小程序AppId: {}", wechatSubscribeConfig.getAppId());
            
            String url = String.format(ACCESS_TOKEN_URL, wechatSubscribeConfig.getAppId(), wechatSubscribeConfig.getSecret());
            log.info("微信access_token请求URL: {}", url);
            
            log.info("执行HTTP GET请求");
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            log.info("HTTP响应状态: {}", response.getStatusCode());
            log.info("HTTP响应体: {}", response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject result = JSON.parseObject(response.getBody());
                String accessToken = result.getString("access_token");
                Integer errcode = result.getInteger("errcode");
                String errmsg = result.getString("errmsg");
                
                if (errcode != null && errcode != 0) {
                    log.error("微信API返回错误: errcode={}, errmsg={}", errcode, errmsg);
                    log.info("========== 获取 access_token 失败 ==========");
                    return null;
                }
                
                if (accessToken != null) {
                    log.info("成功获取access_token: {}", accessToken);
                    
                    // 缓存access_token
                    log.info("开始缓存access_token，过期时间: {}秒", ACCESS_TOKEN_EXPIRE_SECONDS);
                    redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
                    log.info("access_token缓存成功");
                    log.info("========== 获取 access_token 完成（新获取） ==========");
                    return accessToken;
                } else {
                    log.error("获取access_token失败，返回的access_token为空");
                    log.info("========== 获取 access_token 失败 ==========");
                }
            } else {
                log.error("HTTP请求失败，状态码: {}", response.getStatusCode());
                log.info("========== 获取 access_token 失败 ==========");
            }
        } catch (Exception e) {
            log.error("获取access_token异常", e);
            log.info("========== 获取 access_token 失败 ==========");
        }
        return null;
    }
    
    @Override
    public boolean sendSubscribeMessage(String openId, String templateId, Map<String, Object> data) {
        try {
            log.info("开始获取 access_token");
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("access_token 为空，无法发送订阅消息");
                return false;
            }
            log.info("成功获取 access_token: {}", accessToken);
            
            String url = String.format(SUBSCRIBE_MESSAGE_URL, accessToken);
            log.info("订阅消息发送地址: {}", url);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("touser", openId);
            requestBody.put("template_id", templateId);
            requestBody.put("data", data);
            requestBody.put("miniprogram", Map.of("appid", wechatSubscribeConfig.getAppId()));
            
            String requestBodyStr = JSON.toJSONString(requestBody);
            log.info("准备发送订阅消息请求: {}", requestBodyStr);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(requestBodyStr, headers);
            log.info("执行HTTP POST请求");
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            log.info("HTTP响应状态: {}", response.getStatusCode());
            log.info("HTTP响应体: {}", response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject result = JSON.parseObject(response.getBody());
                Integer errcode = result.getInteger("errcode");
                String errmsg = result.getString("errmsg");
                
                log.info("微信API返回: errcode={}, errmsg={}", errcode, errmsg);
                
                if (errcode != null && errcode == 0) {
                    log.info("订阅消息发送成功: openId={}", openId);
                    return true;
                } else {
                    log.error("订阅消息发送失败: errcode={}, errmsg={}", errcode, errmsg);
                    // 特殊处理常见错误
                    if (errcode == 40001) {
                        log.error("access_token 无效或已过期，清除缓存");
                        redisTemplate.delete(ACCESS_TOKEN_KEY);
                    } else if (errcode == 43101) {
                        log.error("用户拒绝接收该类型消息");
                    }
                }
            } else {
                log.error("HTTP请求失败，状态码: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("发送订阅消息异常", e);
        }
        return false;
    }
    
    @Override
    public boolean sendShippingNotification(String openId, String receiverName, String orderNo, String courierCompany, String trackingNo, String status) {
        try {
            Map<String, Object> data = new HashMap<>();
            
            log.info("========== 开始发送订阅消息 ==========");
            log.info("openId: {}", openId);
            log.info("收件人: {}", receiverName);
            log.info("订单号: {}", orderNo);
            log.info("快递公司: {}", courierCompany);
            log.info("运单号: {}", trackingNo);
            log.info("物流状态: {}", status);
            
            // 收件人
            Map<String, Object> receiverNameData = new HashMap<>();
            receiverNameData.put("value", receiverName);
            data.put("name15", receiverNameData);
            
            // 订单号
            Map<String, Object> orderNoData = new HashMap<>();
            orderNoData.put("value", orderNo);
            data.put("character_string1", orderNoData);
            
            // 快递公司
            Map<String, Object> courierCompanyData = new HashMap<>();
            courierCompanyData.put("value", courierCompany);
            data.put("name9", courierCompanyData);
            
            // 运单号
            Map<String, Object> trackingNoData = new HashMap<>();
            trackingNoData.put("value", trackingNo);
            data.put("character_string10", trackingNoData);
            
            // 状态
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("value", status);
            data.put("phrase3", statusData);
            
            log.info("模板参数构建完成: {}", JSON.toJSONString(data));
            log.info("模板ID: {}", wechatSubscribeConfig.getTemplateId());
            
            boolean success = sendSubscribeMessage(openId, wechatSubscribeConfig.getTemplateId(), data);
            
            if (success) {
                log.info("========== 订阅消息发送成功 ==========");
            } else {
                log.error("========== 订阅消息发送失败 ==========");
            }
            
            return success;
        } catch (Exception e) {
            log.error("========== 发送订阅消息异常 ==========", e);
            return false;
        }
    }
}
