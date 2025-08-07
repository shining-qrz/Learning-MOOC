package edu.wust.qrz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("edu.wust.qrz.mapper")
@EnableDiscoveryClient
public class MediaMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaMainApplication.class, args);
    }
}
