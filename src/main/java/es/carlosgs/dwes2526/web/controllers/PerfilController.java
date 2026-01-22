package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.users.models.User;
import es.carlosgs.dwes2526.rest.users.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/app/perfil")
public class PerfilController {

    private UsersService userService;

    @GetMapping
    public String showProfile(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(email).orElse(null);
        model.addAttribute("usuario", user);
        return "app/perfil";
    }

    /*

    @PostMapping("/editar")
    public String updateProfile(@Valid @ModelAttribute("usuario") User updatedUser,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "app/perfil";
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User existingUser = userService.findByUsername(email).orElse(null);

        // Update only allowed fields
        if (existingUser != null) {
            existingUser.setNombre(updatedUser.getNombre());
            existingUser.setApellidos(updatedUser.getApellidos());
        }

        userService.save(existingUser);
        model.addAttribute("mensaje", "Perfil actualizado correctamente");
        model.addAttribute("usuario", existingUser);

        return "app/perfil";
    }

     */

    /**
     * Helper method to check if a path is an external URL
     */
    private boolean isExternalUrl(String path) {
        return path != null && (path.startsWith("http://") || path.startsWith("https://"));
    }
}