package com.project.bloggiator.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "your-cloud-name",
                "api_key", "your-api-key",
                "api_secret", "your-api-secret",
                "secure", true
                                                      );
        return new Cloudinary(config);
    }
}