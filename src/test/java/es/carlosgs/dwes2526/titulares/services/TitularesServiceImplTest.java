package es.carlosgs.dwes2526.titulares.services;

import es.carlosgs.dwes2526.titulares.dto.TitularRequestDto;
import es.carlosgs.dwes2526.titulares.exceptions.TitularConflictException;
import es.carlosgs.dwes2526.titulares.mappers.TitularesMapper;
import es.carlosgs.dwes2526.titulares.models.Titular;
import es.carlosgs.dwes2526.titulares.repositories.TitularesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TitularesServiceImplTest {
  private final Titular titular = Titular.builder().id(1L).nombre("Jose").build();
  private final TitularRequestDto titularDto = TitularRequestDto.builder().nombre("Jose").build();

  @Mock
  private TitularesRepository titularesRepository;
  // usamos el mapper real aunque en modo espía que nos permite simular algunas partes del mismo
  @Spy
  private TitularesMapper titularesMapper;
  // Es la clase que se testea y a la que se inyectan los mocks y espías automáticamente
  @InjectMocks
  private TitularesServiceImpl titularesService;

  @Test
  public void testFindAll() {
    // Arrange
    when(titularesRepository.findAll()).thenReturn(List.of(titular));

    // Act
    var res = titularesService.findAll(null);

    // Assert
    assertAll("findAll",
        () -> assertNotNull(res),
        () -> assertFalse(res.isEmpty())
    );

    // Verify
    verify(titularesRepository, times(1)).findAll();
  }

  @Test
  public void testFindByNombre() {
    // Arrange
    when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(titular));

    // Act
    var res = titularesService.findByNombre("Jose");

    // Assert
    assertAll("findByNombre",
        () -> assertNotNull(res),
        () -> assertEquals("Jose", res.getNombre())
    );

    // Verify
    verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
  }

  @Test
  public void testFindById() {
    // Arrange
    when(titularesRepository.findById(anyLong())).thenReturn(Optional.of(titular));

    // Act
    var res = titularesService.findById(1L);

    // Assert
    assertAll("findById",
        () -> assertNotNull(res),
        () -> assertEquals("Jose", res.getNombre())
    );

    // Verify
    verify(titularesRepository, times(1)).findById(anyLong());
  }

  @Test
  public void testSave() {
    // Arrange
    when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
    when(titularesRepository.save(any(Titular.class))).thenReturn(titular);

    // Act
    titularesService.save(titularDto);

    // Assert
    assertAll("save",
        () -> assertNotNull(titular),
        () -> assertEquals("Jose", titular.getNombre())
    );

    // Verify
    verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    verify(titularesRepository, times(1)).save(any(Titular.class));
  }

  @Test
  public void testSaveConflict() {
    // Arrange
    when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(titular));

    // Act
    var res = assertThrows(TitularConflictException.class,
        () -> titularesService.save(titularDto));

    // Assert
    assertAll("saveConflict",
        () -> assertNotNull(res),
        () -> assertEquals("Ya existe un titular con el nombre Jose", res.getMessage())
    );

    // Verify
    verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    verify(titularesRepository, times(0)).save(any(Titular.class));
  }

  @Test
  public void testUpdate() {
    // Arrange
    when(titularesRepository.findById(anyLong())).thenReturn(Optional.of(titular));
    when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(titular));
    when(titularesRepository.save(any(Titular.class))).thenReturn(titular);

    // Act
    titularesService.update(1L, titularDto);

    // Assert
    assertAll("update",
        () -> assertNotNull(titular),
        () -> assertEquals("Jose", titular.getNombre())
    );


    // Verify
    verify(titularesRepository, times(1)).findById(anyLong());
    verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    verify(titularesRepository, times(1)).save(any(Titular.class));
  }

  @Test
  public void testUpdateConflict() {
    // Arrange
    when(titularesRepository.findById(anyLong())).thenReturn(Optional.of(titular));
    when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(titular));

    // Act, el id no debe ser igual, no se puede actualizar, porqe ya existe
    var res = assertThrows(TitularConflictException.class,
        () -> titularesService.update(2L, titularDto));

    // Assert
    assertAll("updateConflict",
        () -> assertNotNull(res),
        () -> assertEquals("Ya existe un titular con el nombre Jose", res.getMessage())
    );

    // Verify
    verify(titularesRepository, times(1)).findById(anyLong());
    verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    verify(titularesRepository, times(0)).save(any(Titular.class));
  }

  @Test
  public void testDeleteById() {
    // Arrange
    when(titularesRepository.findById(anyLong())).thenReturn(Optional.of(titular));
    when(titularesRepository.existsTarjetaById(anyLong())).thenReturn(false);

    // Act
    titularesService.deleteById(1L);

    // Assert
    assertAll("deleteById",
        () -> assertNotNull(titular),
        () -> assertEquals("Jose", titular.getNombre())
    );

    // Verify
    verify(titularesRepository, times(1)).findById(anyLong());
    verify(titularesRepository, times(1)).existsTarjetaById(anyLong());
    verify(titularesRepository, times(1)).deleteById(anyLong());
  }

}