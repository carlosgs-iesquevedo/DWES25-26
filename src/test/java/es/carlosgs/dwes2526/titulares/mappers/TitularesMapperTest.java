package es.carlosgs.dwes2526.titulares.mappers;

import es.carlosgs.dwes2526.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.titulares.models.Titular;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TitularesMapperTest {
  private final Titular titular = Titular.builder().id(1L).nombre("Jose").build();

  // Inyectamos el mapper
  private final TitularesMapper titularesMapper = new TitularesMapper();

  private final TitularRequestDto titularDto = TitularRequestDto.builder().nombre("JOSE").build();

  @Test
  public void whenToTitular_thenReturnTitular() {
    Titular mappedTitular = titularesMapper.toTitular(titularDto);

    assertAll("whenToTitular_thenReturnTitular",
        () -> assertEquals(titularDto.getNombre(), mappedTitular.getNombre())
    );
  }

  @Test
  public void whenToTitularWithExistingTitular_thenReturnUpdatedTitular() {

    Titular updatedTitular = titularesMapper.toTitular(titularDto, titular);

    assertAll("whenToTitularWithExistingTitular_thenReturnUpdatedTitular",
        () -> assertEquals(titularDto.getNombre(), updatedTitular.getNombre())
    );
  }
}