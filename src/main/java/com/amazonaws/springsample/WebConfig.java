// UNCOMMENT FOR MANUAL INSTRUMENTATION INSTEAD OF AUTO-INSTRUMENTATION
// The two forms of instrumentation ARE mutually exclusive

//package com.amazonaws.springsample;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Bean;
//import javax.servlet.Filter;
//import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public Filter TracingFilter() {
//        return new AWSXRayServletFilter("Test app");
//    }
//}