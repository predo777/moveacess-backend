package br.com.moveacess.controller;

import br.com.moveacess.dto.Dtos.*;
import br.com.moveacess.service.ObstacleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/obstacles") @RequiredArgsConstructor
public class ObstacleController {
    private final ObstacleService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ObstacleResponse report(@Valid @RequestBody ObstacleRequest req, Authentication auth) {
        return service.report(req, auth.getName());
    }

    @GetMapping("/nearby")
    public List<ObstacleResponse> nearby(@RequestParam double lat, @RequestParam double lng,
                                         @RequestParam(defaultValue = "500") double radius) {
        return service.nearby(lat, lng, radius);
    }

    @PostMapping("/{id}/confirm")
    public ObstacleResponse confirm(@PathVariable Long id) { return service.confirm(id); }

    @PostMapping("/{id}/resolved")
    public ObstacleResponse resolved(@PathVariable Long id) { return service.voteResolved(id); }
}
