package es.carlosgs.dwes2526.titulares.controllers;

import es.carlosgs.dwes2526.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.titulares.exceptions.TitularConflictException;
import es.carlosgs.dwes2526.titulares.exceptions.TitularNotFoundException;
import es.carlosgs.dwes2526.titulares.models.Titular;
import es.carlosgs.dwes2526.titulares.services.TitularesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class TitularesRestControllerTest {
    private final String ENDPOINT = "/api/v1/titulares";

    private final Titular titular1 = Titular.builder().id(1L).nombre("Jose").build();
    private final Titular titular2 = Titular.builder().id(2L).nombre("Paco").build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private TitularesService titularesService;

    @Test
    void getAll() {
        // Arrange
        var titulares = List.of(titular1, titular2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(titulares);
        when(titularesService.findAll(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(titulares.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Titular.class).usingRecursiveComparison().isEqualTo(titular1);
                    assertThat(json).extractingPath("$.content[1]")
                            .convertTo(Titular.class).usingRecursiveComparison().isEqualTo(titular2);
                });

        // Verify
        verify(titularesService, times(1))
            .findAll(Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByNombre() {
        // Arrange
        var titulares = List.of(titular2);
        String queryString = "?nombre=" + titular2.getNombre();
        Optional<String> nombre = Optional.of(titular2.getNombre());
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(titulares);
        when(titularesService.findAll(nombre, Optional.empty(), pageable))
            .thenReturn(page);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(titulares.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Titular.class).usingRecursiveComparison().isEqualTo(titular2);
                });

        // Verify
        verify(titularesService, times(1))
            .findAll(nombre, Optional.empty(), pageable);
    }

    @Test
    void getById() {
        // Arrange
        Long id = titular1.getId();
        when(titularesService.findById(id)).thenReturn(titular1);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Titular.class).usingRecursiveComparison().isEqualTo(titular1);

        // Verify
        verify(titularesService, only()).findById(anyLong());

    }

    @Test
    void getById_shouldThrowTitularNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        when(titularesService.findById(anyLong())).thenThrow(new TitularNotFoundException(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus4xxClientError()
                // throws TarjetaNotFoundException
                .hasFailed().failure()
                .isInstanceOf(TitularNotFoundException.class)
                .hasMessageContaining("no encontrado");

        // Verify
        verify(titularesService, only()).findById(anyLong());

    }

    @Test
    void create() {
        // Arrange
        String requestBody = """
           {
              "nombre": "Manuela"
           }
           """;

        var titularSaved = Titular.builder()
                .id(1L)
                .nombre("manuela")
                .build();

        when(titularesService.save(any(TitularRequestDto.class))).thenReturn(titularSaved);

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
                .convertTo(Titular.class)
                .usingRecursiveComparison()
                .isEqualTo(titularSaved);

        verify(titularesService, only()).save(any(TitularRequestDto.class));


    }


    @Test
    void create_whenBadRequest() {
        // Arrange
        String requestBody = """
           {
              "nombre": null
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
                .hasPathSatisfying("$.errores", path ->
                    assertThat(path).hasFieldOrProperty("nombre"));


        verify(titularesService, never()).save(any(TitularRequestDto.class));

    }

    @Test
    void create_whenNombreExists() {
      // Arrange
      String requestBody = """
           {
              "nombre": "Jose"
           }
           """;

      when(titularesService.save(any(TitularRequestDto.class)))
          .thenThrow(new TitularConflictException("Ya existe un titular con el nombre Jose"));


      // Act
      var result = mockMvcTester.post()
          .uri(ENDPOINT)
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody)
          .exchange();

      // Assert
      assertThat(result)
          .hasStatus(HttpStatus.CONFLICT)
          // throws TitularesConflictEsception
          .hasFailed().failure()
          .isInstanceOf(TitularConflictException.class)
          .hasMessageContaining("Ya existe un titular");


      verify(titularesService, only()).save(any(TitularRequestDto.class));
    }

    @Test
    void update() {
      // Arrange
      long id = 1L;
      String requestBody = """
           {
              "nombre": "JOSE"
           }
           """;

      var titularSaved = Titular.builder()
          .id(1L)
          .nombre("JOSE")
          .build();

      when(titularesService.update(anyLong(), any(TitularRequestDto.class))).thenReturn(titularSaved);

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
          .convertTo(Titular.class)
          .usingRecursiveComparison()
          .isEqualTo(titularSaved);

      verify(titularesService, only()).update(anyLong(), any(TitularRequestDto.class));
    }

    @Test
    void update_shouldThrowTitularNotFound() {
      // Arrange
      Long id = 3L;
      String requestBody = """
           {
              "nombre": "JOSE"
           }
           """;
      when(titularesService.update(anyLong(), any(TitularRequestDto.class))).thenThrow(new TitularNotFoundException(id));

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
          .isInstanceOf(TitularNotFoundException.class)
          .hasMessageContaining("no encontrado");

      // Verify
      verify(titularesService, only()).update(anyLong(), any());
    }

    @Test
    void update_shouldThrowBadRequest() {
      // Arrange
      long id = 3L;
      String requestBody = """
           {
              "nombre": null
           }
           """;

      // Act
      var result = mockMvcTester.put()
          .uri(ENDPOINT + "/" + id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody)
          .exchange();

      // Assert
      assertThat(result)
          .hasStatus(HttpStatus.BAD_REQUEST)
          .bodyJson()
          .hasPathSatisfying("$.errores", path ->
              assertThat(path).hasFieldOrProperty("nombre"));


      verify(titularesService, never()).update(anyLong(), any(TitularRequestDto.class));
    }

    @Test
    void update_whenNombreExists() {
      // Arrange
      long id = 1L;
      String requestBody = """
           {
              "nombre": "Jose"
           }
           """;

      when(titularesService.update(anyLong(), any(TitularRequestDto.class)))
          .thenThrow(new TitularConflictException("Ya existe un titular con el nombre Jose"));


      // Act
      var result = mockMvcTester.put()
          .uri(ENDPOINT + "/" + id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody)
          .exchange();

      // Assert
      assertThat(result)
          .hasStatus(HttpStatus.CONFLICT)
          // throws TitularesConflictEsception
          .hasFailed().failure()
          .isInstanceOf(TitularConflictException.class)
          .hasMessageContaining("Ya existe un titular");

      verify(titularesService, only()).update(anyLong(), any(TitularRequestDto.class));
    }

    @Test
    void delete() {
      // Arrange
      long id = 1L;
      doNothing().when(titularesService).deleteById(anyLong());
      // Act
      var result = mockMvcTester.delete()
          .uri(ENDPOINT+ "/" + id)
          .exchange();
      // Assert
      assertThat(result)
          .hasStatus(HttpStatus.NO_CONTENT);

      verify(titularesService, only()).deleteById(anyLong());
    }

  @Test
  void delete_shouldThrowTitularaNotFound() {
    // Arrange
    Long id = 1L;
    doThrow(new TitularNotFoundException(id)).when(titularesService).deleteById(anyLong());
    // Act
    var result = mockMvcTester.delete()
        .uri(ENDPOINT+ "/" + id)
        .exchange();
    // Assert
    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND);

    verify(titularesService, only()).deleteById(anyLong());
  }



}