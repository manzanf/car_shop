package com.playtika.carshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.PublicMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;

@EnableAutoConfiguration(exclude = {AuditAutoConfiguration.class, MetricRepositoryAutoConfiguration.class, WebSocketAutoConfiguration.class,
        MultipartAutoConfiguration.class, JmxAutoConfiguration.class, PublicMetricsAutoConfiguration.class})
@SpringBootApplication
public class CarShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarShopApplication.class, args);
    }
}
