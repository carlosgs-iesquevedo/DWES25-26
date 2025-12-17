package es.carlosgs.dwes2526.auth.services.users;

import es.carlosgs.dwes2526.auth.repositories.AuthUsersRepository;
import es.carlosgs.dwes2526.users.exceptions.UserNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// Indicamos que es un servicio de detalles de usuario
// Es muy importante esta línea para decir que vamos a usar el servicio de usuarios Spring
// Otra forma de hacerlo es declarar la interfaz así
// public interface AuthUsersService {
//   UserDetailsService userDetailsService();
// }
// y luego usarla aquí con implements AuthUsersService

@RequiredArgsConstructor
@Service("userDetailsService")
public class AuthUsersServiceImpl implements AuthUsersService {

  private final AuthUsersRepository authUsersRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UserNotFound {
    return authUsersRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFound("Usuario con username " + username + " no encontrado"));
  }
}
