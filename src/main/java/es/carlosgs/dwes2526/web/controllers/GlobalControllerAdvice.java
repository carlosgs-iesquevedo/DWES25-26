package es.carlosgs.dwes2526.web.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

// Aqui exponemos atributos globales para las vistas
@ControllerAdvice
public class GlobalControllerAdvice {

  @Value("${spring.application.name}")
  private String appName;

  @ModelAttribute("appName")
  public String getAppName() {
    return appName;
  }

  @Value("${application.title}")
  private String appDescription;

  @ModelAttribute("appDescription")
  public String getAppDescription() {
    return appDescription;
  }


  @ModelAttribute("currentYear")
  public int getCurrentYear() {
    return LocalDate.now().getYear();
  }


}
