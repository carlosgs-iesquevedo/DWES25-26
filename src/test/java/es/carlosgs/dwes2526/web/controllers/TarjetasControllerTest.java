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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TarjetasControllerTest {

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private TarjetasService tarjetasService;

  @MockitoBean
  private UsersService usersService;

  //@WithMockUser(username = "jose", roles = {"USER"} )
  @Test
  @DisplayName("GET /mistarjetas/{id} - Devuelve detalle de la tarjeta con {id}")
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
    // Mockeamos la parte de autenticación
    when(usersService.findByUsername("jose")).thenReturn(Optional.of(mock(User.class)));
    var auth = new UsernamePasswordAuthenticationToken(
      "jose", "xxxx", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    // Act
    var result = mockMvcTester.get()
      .uri(  "/app/mistarjetas/" + id)
      .contentType(MediaType.TEXT_HTML)
      .exchange();

    MvcResult mvsResult = result.getMvcResult();
    ModelAndView mav = mvsResult.getModelAndView();

    // Assert
    assertThat(result)
      .hasStatusOk()
      .viewName().isEqualTo("app/tarjetas/detalle");

    assertThat(mav).isNotNull();
    assertThat(mav.getModel()).containsEntry("tarjeta", tarjeta);

  }
}
