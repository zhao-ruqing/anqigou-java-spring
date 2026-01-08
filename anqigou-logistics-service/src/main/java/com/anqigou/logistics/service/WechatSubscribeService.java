package com.anqigou.logistics.service;

import java.util.Map;

public interface WechatSubscribeService {
    
    String getAccessToken();
    
    boolean sendSubscribeMessage(String openId, String templateId, Map<String, Object> data);
    
    boolean sendShippingNotification(String openId, String receiverName, String orderNo, String courierCompany, String trackingNo, String status);
}
