package br.com.moveacess.controller;

import br.com.moveacess.dto.Dtos.RouteResponse;
import br.com.moveacess.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/routes") @RequiredArgsConstructor
public class RouteController {
    private final RouteService service;

    @GetMapping
    public RouteResponse route(@RequestParam double fromLat, @RequestParam double fromLng,
                               @RequestParam double toLat, @RequestParam double toLng) {
        return service.route(fromLat, fromLng, toLat, toLng);
    }
}
