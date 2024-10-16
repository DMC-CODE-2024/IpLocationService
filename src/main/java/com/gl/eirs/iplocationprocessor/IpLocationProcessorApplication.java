package com.gl.eirs.iplocationprocessor;

import com.gl.eirs.iplocationprocessor.service.MainService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableEncryptableProperties
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.gl.eirs")
@SpringBootApplication
@EnableConfigurationProperties
public class IpLocationProcessorApplication {

    public static void main(String[] args) {

        var ctx = SpringApplication.run(IpLocationProcessorApplication.class, args);

        MainService mainService = ctx.getBean(MainService.class);
        mainService.processDeltaFiles(args[0]);
    }

}
