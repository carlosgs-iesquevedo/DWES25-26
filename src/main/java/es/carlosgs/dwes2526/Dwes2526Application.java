package es.carlosgs.dwes2526;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Dwes2526Application {

  static void main(String[] args) {
    SpringApplication.run(Dwes2526Application.class, args);
  }

}
