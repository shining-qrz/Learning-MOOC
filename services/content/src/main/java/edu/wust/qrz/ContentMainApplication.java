package edu.wust.qrz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("edu.wust.qrz.mapper") // 扫描Mapper接口所在的包
@EnableDiscoveryClient
@SpringBootApplication
public class ContentMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentMainApplication.class, args);
    }
}
