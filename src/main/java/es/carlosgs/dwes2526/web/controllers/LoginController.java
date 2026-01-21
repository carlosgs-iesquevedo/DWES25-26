package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.users.models.User;
import es.carlosgs.dwes2526.rest.users.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UsersService usuarioServicio;


    @GetMapping("/")
    public String welcome() {
        return "redirect:/public/";
    }

    @GetMapping("/auth/login")
    public String login(Model model) {
        // CSRF token is handled by GlobalControllerAdvice
        // Para el formulario de registro
        model.addAttribute("usuario", new User());
        return "login";
    }

}
