package es.carlosgs.dwes2526.rest.auth.services.authentication;

import es.carlosgs.dwes2526.rest.auth.dto.JwtAuthResponse;
import es.carlosgs.dwes2526.rest.auth.dto.UserSignInRequest;
import es.carlosgs.dwes2526.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
  JwtAuthResponse signUp(UserSignUpRequest request);

  JwtAuthResponse signIn(UserSignInRequest request);
}
