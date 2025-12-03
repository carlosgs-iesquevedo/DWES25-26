package es.carlosgs.dwes2526.tarjetas.controllers;

import es.carlosgs.dwes2526.tarjetas.dto.TarjetaCreateDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.tarjetas.dto.TarjetaUpdateDto;
import es.carlosgs.dwes2526.tarjetas.exceptions.TarjetaNotFoundException;
import es.carlosgs.dwes2526.tarjetas.services.TarjetasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class TarjetasRestControllerTest {

  private final String ENDPOINT = "/api/v1/tarjetas";

  private final TarjetaResponseDto tarjetaResponse1 = TarjetaResponseDto.builder()
      .id(1L)
      .numero("1234-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular("Jose")
      .saldo(100.0)
      .build();

  private final TarjetaResponseDto tarjetaResponse2 = TarjetaResponseDto.builder()
      .id(2L)
      .numero("4321-5678-1234-5678")
      .cvc("555")
      .fechaCaducidad(LocalDate.of(2025,12,31))
      .titular("Juan")
      .saldo(100.0)
      .build();

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private TarjetasService tarjetasService;

  @Test
  void getAll() {
    // Arrange
    var tarjetaResponses = List.of(tarjetaResponse1, tarjetaResponse2);
    var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    var page = new PageImpl<>(tarjetaResponses);
    when(tarjetasService.findAll(null, null, pageable)).thenReturn(page);

    // Act. Consultar el endpoint
    var result = mockMvcTester.get()
        .uri(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(tarjetaResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(TarjetaResponseDto.class).isEqualTo(tarjetaResponse1);
          assertThat(json).extractingPath("$.content[1]")
              .convertTo(TarjetaResponseDto.class).isEqualTo(tarjetaResponse2);
        });

    // Verify
    verify(tarjetasService, times(1)).findAll(null, null, pageable);
  }

  @Test
  void getAllByNumero() {
    // Arrange
    var tarjetaResponses = List.of(tarjetaResponse2);
    String queryString = "?numero=" + tarjetaResponse2.getNumero();
    var page = new PageImpl<>(tarjetaResponses);
    when(tarjetasService.findAll(anyString(), isNull(), any(Pageable.class))).thenReturn(page);

    // Act
    var result = mockMvcTester.get()
        .uri(ENDPOINT + queryString)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(tarjetaResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(TarjetaResponseDto.class).isEqualTo(tarjetaResponse2);
        });

    // Verify
    verify(tarjetasService, times(1)).findAll(anyString(), isNull(), any(Pageable.class));
  }

  @Test
  void getAllByTitular() {
    // Arrange
    var tarjetaResponses = List.of(tarjetaResponse2);
    String queryString = "?titular=" + tarjetaResponse2.getTitular();
    var page = new PageImpl<>(tarjetaResponses);
    when(tarjetasService.findAll(isNull(), anyString(), any(Pageable.class))).thenReturn(page);

    // Act
    var result = mockMvcTester.get()
        .uri(ENDPOINT + queryString)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(tarjetaResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(TarjetaResponseDto.class).isEqualTo(tarjetaResponse2);
        });

    // Verify
    verify(tarjetasService, only()).findAll(isNull(), anyString(), any(Pageable.class));
  }

  @Test
  void getAllByNumeroAndTitular() {
    // Arrange
    var tarjetaResponses = List.of(tarjetaResponse2);
    String queryString = "?numero=" + tarjetaResponse2.getNumero() + "&"
        + "titular=" + tarjetaResponse2.getTitular();
    var page = new PageImpl<>(tarjetaResponses);
    when(tarjetasService.findAll(anyString(), anyString(), any(Pageable.class))).thenReturn(page);

    // Act
    var result = mockMvcTester.get()
        .uri(ENDPOINT + queryString)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(tarjetaResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(TarjetaResponseDto.class).isEqualTo(tarjetaResponse2);
        });

    // Verify
    verify(tarjetasService, only()).findAll(anyString(), anyString(), any(Pageable.class));
  }


  @Test
  void getById_shouldReturnJsonWithTarjeta_whenValidIdProvided() {
    // Arrange
    Long id = tarjetaResponse1.getId();
    when(tarjetasService.findById(id)).thenReturn(tarjetaResponse1);

    // Act
    var result = mockMvcTester.get()
        .uri(ENDPOINT + "/" + id.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(TarjetaResponseDto.class)
        .isEqualTo(tarjetaResponse1);

    // Verify
    verify(tarjetasService, only()).findById(anyLong());

  }

  @Test
  void getById_shouldThrowTarjetaNotFound_whenInvalidIdProvided() {
    // Arrange
    Long id = 3L;
    when(tarjetasService.findById(anyLong())).thenThrow(new TarjetaNotFoundException(id));

    // Act
    var result = mockMvcTester.get()
        .uri(ENDPOINT + "/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .exchange();

    assertThat(result)
        .hasStatus4xxClientError()
        // throws TarjetaNotFoundException
        .hasFailed().failure()
        .isInstanceOf(TarjetaNotFoundException.class)
        .hasMessageContaining("no encontrada");

    // Verify
    verify(tarjetasService, only()).findById(anyLong());

  }

  @Test
  void create() {
    // Arrange
    String requestBody = """
           {
              "numero": "1111-2222-3333-4444",
              "cvc": "123",
              "fechaCaducidad": "2029-12-31",
              "titular": "Ana",
              "saldo": 123.0
           }
           """;

    var tarjetaSaved = TarjetaResponseDto.builder()
        .id(1L)
        .numero("1111-2222-3333-4444")
        .cvc("123")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular("Ana")
        .saldo(124.0)
        .build();

    when(tarjetasService.save(any(TarjetaCreateDto.class))).thenReturn(tarjetaSaved);

    // Act
    var result = mockMvcTester.post()
        .uri(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatus(HttpStatus.CREATED)
        .bodyJson()
        .convertTo(TarjetaResponseDto.class)
        .isEqualTo(tarjetaSaved);

    verify(tarjetasService, only()).save(any(TarjetaCreateDto.class));


  }

  @Test
  void create_whenBadRequest() {
    // Arrange
    String requestBody = """
           {
              "numero": "1111",
              "cvc": "1234",
              "fechaCaducidad": "2024-12-31",
              "titular": "Ana",
              "saldo": 123.0
           }
           """;

    // Act
    var result = mockMvcTester.post()
        .uri(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
            .hasPathSatisfying("$.errores", path -> {
              assertThat(path).hasFieldOrProperty("cvc");
              assertThat(path).hasFieldOrProperty("numero");
              assertThat(path).hasFieldOrProperty("fechaCaducidad");
            });

// Otra manera
//    assertThat(result)
//        .hasStatus(HttpStatus.BAD_REQUEST)
//        .bodyJson()
//        .satisfies(json -> {
//          assertThat(json).extractingPath("$.detail").matches(Matcher.containsString("validación"))
//          assertThat(json).extractingPath("$.errores").hasFieldOrProperty("cvc");
//          assertThat(json).extractingPath("$.errores").hasFieldOrProperty("numero");
//          assertThat(json).extractingPath("$.errores").hasFieldOrProperty("fechaCaducidad");
//        });

    verify(tarjetasService, never()).save(any(TarjetaCreateDto.class));

  }

  @Test
  void update() {
    // Arrange
    Long id = 1L;
    String requestBody = """
           {
              "saldo": 500.0
           }
           """;

    var tarjetaSaved = TarjetaResponseDto.builder()
        .id(1L)
        .numero("1111-2222-3333-4444")
        .cvc("123")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular("Ana")
        .saldo(500.0)
        .build();

    when(tarjetasService.update(anyLong(), any(TarjetaUpdateDto.class))).thenReturn(tarjetaSaved);

    // Act
    var result = mockMvcTester.put()
        .uri(ENDPOINT+ "/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(TarjetaResponseDto.class)
        .isEqualTo(tarjetaSaved);

    verify(tarjetasService, only()).update(anyLong(), any(TarjetaUpdateDto.class));

  }

  @Test
  void update_shouldThrowTarjetaNotFound_whenInvalidIdProvided() {
    // Arrange
    Long id = 3L;
    String requestBody = """
           {
              "saldo": 500.0
           }
           """;
    when(tarjetasService.update(anyLong(), any(TarjetaUpdateDto.class))).thenThrow(new TarjetaNotFoundException(id));

    // Act
    var result = mockMvcTester.put()
        .uri(ENDPOINT + "/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND)
        // throws TarjetaNotFoundException
        .hasFailed().failure()
        .isInstanceOf(TarjetaNotFoundException.class)
        .hasMessageContaining("no encontrada");

    // Verify
    verify(tarjetasService, only()).update(anyLong(), any());
  }

  @Test
  void updatePartial() {
    // Arrange
    Long id = 1L;
    String requestBody = """
           {
              "saldo": 500.0
           }
           """;

    var tarjetaSaved = TarjetaResponseDto.builder()
        .id(1L)
        .numero("1111-2222-3333-4444")
        .cvc("123")
        .fechaCaducidad(LocalDate.of(2025,12,31))
        .titular("Ana")
        .saldo(500.0)
        .build();

    when(tarjetasService.update(anyLong(), any(TarjetaUpdateDto.class))).thenReturn(tarjetaSaved);

    // Act
    var result = mockMvcTester.patch()
        .uri(ENDPOINT+ "/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(TarjetaResponseDto.class)
        .isEqualTo(tarjetaSaved);

    verify(tarjetasService, only()).update(anyLong(), any(TarjetaUpdateDto.class));
  }

  @Test
  void delete() {
    // Arrange
    Long id = 1L;
    doNothing().when(tarjetasService).deleteById(anyLong());
    // Act
    var result = mockMvcTester.delete()
        .uri(ENDPOINT+ "/" + id)
        .exchange();
    // Assert
    assertThat(result)
        .hasStatus(HttpStatus.NO_CONTENT);

    verify(tarjetasService, only()).deleteById(anyLong());

  }

  @Test
  void delete_shouldThrowTarjetaNotFound_whenInvalidIdProvided() {
    // Arrange
    Long id = 3L;
    doThrow(new TarjetaNotFoundException(id)).when(tarjetasService).deleteById(anyLong());

    // Act
    var result = mockMvcTester.delete()
        .uri(ENDPOINT + "/" + id)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND)
        // throws TarjetaNotFoundException
        .hasFailed().failure()
        .isInstanceOf(TarjetaNotFoundException.class)
        .hasMessageContaining("no encontrada");

    // Verify
    verify(tarjetasService, only()).deleteById(anyLong());

  }
}