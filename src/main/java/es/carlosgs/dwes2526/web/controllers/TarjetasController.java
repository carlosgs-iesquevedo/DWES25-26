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

  // Inyectamos en el modelo automáticamente la lista de mis tarjetas
  @ModelAttribute("tarjetas")
  public List<Tarjeta> misTarjetas() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> usuario = usersService.findByUsername(username);
    if (usuario.isEmpty()) {
      return List.of();
    }
    return tarjetasService.buscarPorUsuarioId(usuario.get().getId());
  }

  // Enviamos mis tarjetas a la vista lista
  @GetMapping("/mistarjetas")
  public String list() {
    return "app/tarjetas/lista";
  }


  @GetMapping("/mistarjetas/{id}")
  public String getById(@PathVariable Long id, Model model) {
    Tarjeta tarjeta = tarjetasService.buscarPorId(id).orElse(null);
    model.addAttribute("tarjeta", tarjeta);
    return "app/tarjetas/detalle";
  }

}
