package es.carlosgs.dwes2526.auth.services.authentication;

import es.carlosgs.dwes2526.auth.dto.JwtAuthResponse;
import es.carlosgs.dwes2526.auth.dto.UserSignInRequest;
import es.carlosgs.dwes2526.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
  JwtAuthResponse signUp(UserSignUpRequest request);

  JwtAuthResponse signIn(UserSignInRequest request);
}
