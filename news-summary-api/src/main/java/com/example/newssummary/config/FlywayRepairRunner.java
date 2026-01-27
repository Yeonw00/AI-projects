//package com.example.newssummary.config;
//
//import org.flywaydb.core.Flyway;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FlywayRepairRunner {
//    @Bean
//    CommandLineRunner runFlywayRepair(Flyway flyway) {
//        return args -> {
//            System.out.println("🛠 Running Flyway.repair()...");
//            flyway.repair();
//        };
//    }
//}