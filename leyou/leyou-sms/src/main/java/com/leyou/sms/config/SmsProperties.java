package com.leyou.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leyou.sms")
public class SmsProperties {

    String AccessKeyID;
    String AccessKeySecret;
    String SignName;
    String VerifyCodeTemplate;

    public String getAccessKeyID() {
        return AccessKeyID;
    }

    public void setAccessKeyID(String accessKeyID) {
        AccessKeyID = accessKeyID;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getSignName() {
        return SignName;
    }

    public void setSignName(String signName) {
        SignName = signName;
    }

    public String getVerifyCodeTemplate() {
        return VerifyCodeTemplate;
    }

    public void setVerifyCodeTemplate(String verifyCodeTemplate) {
        VerifyCodeTemplate = verifyCodeTemplate;
    }
}
