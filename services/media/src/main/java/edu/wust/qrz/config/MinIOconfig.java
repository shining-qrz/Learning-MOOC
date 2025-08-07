package edu.wust.qrz.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinIOconfig {
    @Value("${MINIO_ENDPOINT}")
    private String endpoint;

    @Value("${MINIO_ACCESS_KEY}")
    private String accessKey;

    @Value("${MINIO_SECRET_KEY}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
