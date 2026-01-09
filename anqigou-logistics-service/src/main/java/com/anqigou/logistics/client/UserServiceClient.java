package com.anqigou.logistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.anqigou.common.response.ApiResponse;

@FeignClient(name = "anqigou-user-service", path = "/auth")
public interface UserServiceClient {
    
    @GetMapping("/internal/{userId}/openid")
    ApiResponse<String> getUserOpenId(@PathVariable("userId") String userId);
}
