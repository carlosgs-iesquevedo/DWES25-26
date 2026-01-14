package es.carlosgs.dwes2526.web.controllers;

import es.carlosgs.dwes2526.rest.tarjetas.dto.TarjetaResponseDto;
import es.carlosgs.dwes2526.rest.tarjetas.services.TarjetasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("tarjetas")
public class TarjetasController {
    private final TarjetasService tarjetasService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        TarjetaResponseDto tarjeta = tarjetasService.findById(id);
        model.addAttribute("tarjeta", tarjeta);
        return "tarjetas/tarjetaDetalle";
    }

}
