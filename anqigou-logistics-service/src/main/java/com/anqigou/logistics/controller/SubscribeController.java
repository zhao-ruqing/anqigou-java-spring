package com.anqigou.logistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.logistics.service.WechatSubscribeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 订阅消息控制器
 */
@RestController
@RequestMapping("/subscribe")
@Slf4j
public class SubscribeController {
    
    @Autowired
    private WechatSubscribeService wechatSubscribeService;
    
    /**
     * 保存订阅状态
     */
    @PostMapping("/save")
    public ApiResponse<String> saveSubscribeStatus(@RequestBody SubscribeStatusRequest request) {
        try {
            log.info("保存订阅状态: orderId={}, templateId={}, status={}", 
                    request.getOrderId(), request.getTemplateId(), request.getStatus());
            // 这里可以添加保存订阅状态到数据库的逻辑
            // 目前我们只需要记录日志，因为微信订阅消息是一次性的，不需要持久化保存
            return ApiResponse.success("订阅状态保存成功");
        } catch (Exception e) {
            log.error("保存订阅状态失败", e);
            return ApiResponse.failure(500, "保存订阅状态失败");
        }
    }
    
    /**
     * 订阅状态请求类
     */
    static class SubscribeStatusRequest {
        private String orderId;
        private String templateId;
        private String status;
        
        public String getOrderId() {
            return orderId;
        }
        
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        
        public String getTemplateId() {
            return templateId;
        }
        
        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
