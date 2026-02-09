package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.models.Tarjeta;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import es.carlosgs.dwes2526.rest.users.models.User;
import es.carlosgs.dwes2526.rest.users.services.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class TarjetasController {
  private final TarjetasService tarjetasService;
  private final UsersService usersService;

  // Enviamos mis tarjetas a la vista lista
  @GetMapping("/mistarjetas")
  public String misTarjetas(Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> usuario = usersService.findByUsername(username);
    List<Tarjeta> tarjetas = List.of();
    if (usuario.isPresent()) {
      tarjetas = tarjetasService.buscarPorUsuarioId(usuario.get().getId());
    }
    model.addAttribute("tarjetas", tarjetas);
    return "app/tarjetas/lista";
  }

  @GetMapping("/mistarjetas/{id}")
  public String getById(@PathVariable Long id, Model model) {
    Tarjeta tarjeta = tarjetasService.buscarPorId(id).orElse(null);
    model.addAttribute("tarjeta", tarjeta);
    return "app/tarjetas/detalle";
  }

}
