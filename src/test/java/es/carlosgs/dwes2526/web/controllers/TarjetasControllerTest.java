package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import es.carlosgs.dwes2526.rest.users.models.User;
import es.carlosgs.dwes2526.rest.users.services.UsersService;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class TarjetasControllerTest {

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private TarjetasService tarjetasService;

  @MockitoBean
  private UsersService usersService;

  private final Tarjeta tarjeta1 = Tarjeta.builder()
    .id(1L)
    .numero("1234-5678-1234-5678")
    .cvc("555")
    .fechaCaducidad(LocalDate.of(2025, 12, 31))
    .saldo(100.0)
    .build();

  private final Tarjeta tarjeta2 = Tarjeta.builder()
    .id(2L)
      .numero("4321-5678-1234-5678")
      .cvc("999")
      .fechaCaducidad(LocalDate.of(2025, 12, 31))
    .saldo(200.0)
      .build();

  @WithUserDetails("jose")
  @Test
  @DisplayName("GET /app/mistarjetas/{id} - Devuelve detalle de la tarjeta con {id}")
  void getById() {
    // Arrange
    Long id = 1L;
    when(tarjetasService.buscarPorId(id)).thenReturn(Optional.of(tarjeta1));

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
      .containsEntry("tarjeta", tarjeta1);
    mvcAssert.bodyText()
      .contains("Detalle de la tarjeta " + id);

    // Verify
    verify(tarjetasService, only()).buscarPorId(anyLong());
  }

  @WithUserDetails("jose")
  @Test
  @DisplayName("GET /app/mistarjetas - Devuelve lista de tarjetas del usuario activo")
  void misTarjetas() {
    // Arrange
    Long usuarioId = 2L;
    User usuario = User.builder().id(usuarioId).username("jose").build();
    List<Tarjeta> tarjetas = List.of(tarjeta1, tarjeta2);
    when(usersService.findByUsername(anyString())).thenReturn(Optional.of(usuario));
    when(tarjetasService.buscarPorUsuarioId(anyLong())).thenReturn(tarjetas);

    // Act
    var result = mockMvcTester.get()
      .uri(  "/app/mistarjetas")
      .contentType(MediaType.TEXT_HTML)
      .exchange();

    // Assert
    assertThat(result)
      .hasStatusOk()
      .hasViewName("app/tarjetas/lista")
      .model()
        .containsKeys("tarjetas")
        //.containsEntry("tarjetas", tarjetas);
        .hasEntrySatisfying("tarjetas", lista -> assertThat((List<?>) lista)
          .isInstanceOf(List.class)
          .hasSize(2));

    // Verify
    verify(usersService, times(1)).findByUsername(anyString());
    verify(tarjetasService, times(1)).buscarPorUsuarioId(anyLong());

  }
}
