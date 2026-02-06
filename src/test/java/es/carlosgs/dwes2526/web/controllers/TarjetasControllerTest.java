package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TarjetasControllerTest {

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private TarjetasService tarjetasService;

  @WithUserDetails("jose")
  @Test
  @DisplayName("GET /app/mistarjetas/{id} - Devuelve detalle de la tarjeta con {id}")
  void getById() {
    // Arrange
    Long id = 1L;
    Tarjeta tarjeta =  Tarjeta.builder()
      .id(id)
      .numero("1234-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025, 12, 31))
      .saldo(100.0)
      .build();

    when(tarjetasService.buscarPorId(id)).thenReturn(Optional.of(tarjeta));

    // Act
    var result = mockMvcTester.get()
      .uri(  "/app/mistarjetas/" + id)
      .contentType(MediaType.TEXT_HTML)
      .exchange();

    // Assert
    var mvcAssert = assertThat(result)
      .hasStatusOk()
      .hasViewName("app/tarjetas/detalle");

    mvcAssert.model()
      .containsKeys("tarjeta")
      .containsEntry("tarjeta", tarjeta);
    mvcAssert.bodyText()
      .contains("Detalle de la tarjeta " + id);

  }
}
