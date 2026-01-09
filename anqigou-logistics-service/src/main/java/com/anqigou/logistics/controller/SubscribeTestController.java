package com.anqigou.logistics.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.logistics.service.WechatSubscribeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 订阅消息测试控制器
 */
@RestController
@RequestMapping("/test/subscribe")
@Slf4j
public class SubscribeTestController {
    
    @Autowired
    private WechatSubscribeService wechatSubscribeService;
    
    /**
     * 测试获取access_token
     */
    @GetMapping("/access-token")
    public ApiResponse<String> testGetAccessToken() {
        String accessToken = wechatSubscribeService.getAccessToken();
        if (accessToken != null) {
            return ApiResponse.success("获取access_token成功", accessToken);
        } else {
            return ApiResponse.failure(500, "获取access_token失败");
        }
    }
    
    /**
     * 测试发送订阅消息
     */
    @GetMapping("/send")
    public ApiResponse<Boolean> testSendSubscribeMessage(
            @RequestParam String openId,
            @RequestParam String receiverName,
            @RequestParam String orderNo,
            @RequestParam String courierCompany,
            @RequestParam String trackingNo,
            @RequestParam String status) {
        
        boolean success = wechatSubscribeService.sendShippingNotification(
            openId, receiverName, orderNo, courierCompany, trackingNo, status
        );
        
        if (success) {
            return ApiResponse.success("发送订阅消息成功", success);
        } else {
            return ApiResponse.failure(500, "发送订阅消息失败");
        }
    }
    
    /**
     * 测试模板参数构建
     */
    @GetMapping("/template-params")
    public ApiResponse<Map<String, Object>> testTemplateParams(
            @RequestParam String receiverName,
            @RequestParam String orderNo,
            @RequestParam String courierCompany,
            @RequestParam String trackingNo,
            @RequestParam String status) {
        
        Map<String, Object> data = new HashMap<>();
        
        // 收件人
        Map<String, Object> receiverNameData = new HashMap<>();
        receiverNameData.put("value", receiverName);
        data.put("thing1", receiverNameData);
        
        // 订单号
        Map<String, Object> orderNoData = new HashMap<>();
        orderNoData.put("value", orderNo);
        data.put("character_string2", orderNoData);
        
        // 快递公司
        Map<String, Object> courierCompanyData = new HashMap<>();
        courierCompanyData.put("value", courierCompany);
        data.put("thing3", courierCompanyData);
        
        // 运单号
        Map<String, Object> trackingNoData = new HashMap<>();
        trackingNoData.put("value", trackingNo);
        data.put("character_string4", trackingNoData);
        
        // 状态
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("value", status);
        data.put("thing5", statusData);
        
        return ApiResponse.success("模板参数构建成功", data);
    }
}
