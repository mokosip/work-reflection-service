package de.sipgate.konschack.work_reflection_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @ComponentScan(
//    basePackages = {
//      "de.sipgate.konschack.work_reflection_service",
//      "de.sipgate.konschack.work_reflection_service.aiCore",
//      "de.sipgate.konschack.work_reflection_service.appCore"
//    })
public class WorkReflectionServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(WorkReflectionServiceApplication.class, args);
  }
}
