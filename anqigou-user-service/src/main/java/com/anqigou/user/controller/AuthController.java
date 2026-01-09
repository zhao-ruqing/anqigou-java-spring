package com.anqigou.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.dto.LoginRequest;
import com.anqigou.user.dto.LoginResponse;
import com.anqigou.user.dto.RegisterRequest;
import com.anqigou.user.dto.UserInfoDTO;
import com.anqigou.user.dto.VerifyCodeLoginRequest;
import com.anqigou.user.service.AuthService;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 获取验证码
     */
    @PostMapping("/send-code")
    public ApiResponse<String> sendVerifyCode(@RequestParam String phone) {
        authService.sendVerifyCode(phone);
        return ApiResponse.success("验证码已发送");
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody @Validated RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ApiResponse.success("注册成功", response);
    }
    
    /**
     * 密码登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 验证码登录
     */
    @PostMapping("/login-with-code")
    public ApiResponse<LoginResponse> loginWithVerifyCode(@RequestBody @Validated VerifyCodeLoginRequest request) {
        LoginResponse response = authService.loginWithVerifyCode(request);
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 微信登录
     */
    @PostMapping("/wechat-login")
    public ApiResponse<LoginResponse> wechatLogin(@RequestBody WechatLoginRequest request) {
        try {
            log.info("微信登录请求: code={}, userInfo={}", request.getCode(), request.getUserInfo() != null ? request.getUserInfo().getNickName() : "无");
            LoginResponse response = authService.wechatLogin(request.getCode());
            log.info("微信登录成功: userId={}", response.getUserId());
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            log.error("微信登录失败: {}", e.getMessage(), e);
            return ApiResponse.failure(500, "微信登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 微信登录请求参数
     */
    static class WechatLoginRequest {
        private String code;
        private UserInfo userInfo;
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public UserInfo getUserInfo() {
            return userInfo;
        }
        
        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }
        
        static class UserInfo {
            private String nickName;
            private String avatarUrl;
            private String gender;
            private String city;
            private String province;
            private String country;
            private String language;
            
            public String getNickName() {
                return nickName;
            }
            
            public void setNickName(String nickName) {
                this.nickName = nickName;
            }
            
            public String getAvatarUrl() {
                return avatarUrl;
            }
            
            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }
            
            public String getGender() {
                return gender;
            }
            
            public void setGender(String gender) {
                this.gender = gender;
            }
            
            public String getCity() {
                return city;
            }
            
            public void setCity(String city) {
                this.city = city;
            }
            
            public String getProvince() {
                return province;
            }
            
            public void setProvince(String province) {
                this.province = province;
            }
            
            public String getCountry() {
                return country;
            }
            
            public void setCountry(String country) {
                this.country = country;
            }
            
            public String getLanguage() {
                return language;
            }
            
            public void setLanguage(String language) {
                this.language = language;
            }
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/user-info")
    public ApiResponse<UserInfoDTO> getUserInfo(@RequestAttribute String userId) {
        UserInfoDTO userInfo = authService.getUserInfo(userId);
        return ApiResponse.success(userInfo);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/user-info")
    public ApiResponse<String> updateUserInfo(@RequestAttribute String userId, @RequestBody UserInfoDTO userInfo) {
        authService.updateUserInfo(userId, userInfo);
        return ApiResponse.success("更新成功");
    }
    
    /**
     * 获取用户openId（内部接口）
     */
    @GetMapping("/internal/{userId}/openid")
    public ApiResponse<String> getUserOpenId(@PathVariable("userId") String userId) {
        try {
            log.info("获取用户openId请求: userId={}", userId);
            String openId = authService.getUserOpenId(userId);
            log.info("获取用户openId成功: userId={}, openId={}", userId, openId);
            return ApiResponse.success(openId);
        } catch (Exception e) {
            log.error("获取用户openId失败: {}", e.getMessage(), e);
            return ApiResponse.failure(500, "获取用户openId失败: " + e.getMessage());
        }
    }
}
