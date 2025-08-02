package edu.wust.qrz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("edu.wust.qrz.mapper") // Scan Mapper interfaces
@EnableDiscoveryClient
@SpringBootApplication
public class SystemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemMainApplication.class, args);
    }
}
