# MoveAcess – Backend

API REST de mobilidade urbana acessível para pessoas cadeirantes.

**Stack:** Java 17 · Spring Boot 3.3 · Spring Security + JWT · Spring Data JPA · MySQL · Swagger (springdoc) · OpenRouteService

## Como rodar
1. `docker compose up -d`  (sobe o MySQL)
2. Crie uma chave grátis em https://openrouteservice.org e exporte: `export ORS_API_KEY=sua_chave`
3. `mvn spring-boot:run`
4. Swagger: http://localhost:8080/swagger-ui.html

## Endpoints
| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | /api/auth/register | – | Cadastro |
| POST | /api/auth/login | – | Login (retorna JWT) |
| POST | /api/places | ✔ | Cadastra local + recursos de acessibilidade |
| GET | /api/places/{id} | – | Detalhes, recursos e nota média |
| GET | /api/places/nearby?lat&lng&radius | – | Locais num raio (m) |
| POST | /api/places/{id}/features | ✔ | Adiciona rampa, elevador, vaga etc. |
| POST | /api/places/features/{id}/confirm | ✔ | Confirma um recurso (validação colaborativa) |
| POST/GET | /api/places/{id}/reviews | ✔ / – | Avaliações (1–5) |
| POST | /api/obstacles | ✔ | Reporta obstáculo |
| GET | /api/obstacles/nearby | – | Obstáculos ativos num raio |
| POST | /api/obstacles/{id}/confirm | ✔ | Confirma que o obstáculo existe |
| POST | /api/obstacles/{id}/resolved | ✔ | Voto "resolvido" (3 votos desativam) |
| GET | /api/routes?fromLat&fromLng&toLat&toLng | – | Rota para cadeirante + obstáculos no trajeto |

## Evolução sugerida
- Trocar lat/lng por PostGIS (`geography(Point)`) e `ST_DWithin`
- Upload de fotos como evidência
- Perfil MODERATOR para moderar denúncias
- Migrations com Flyway; testes com Testcontainers
