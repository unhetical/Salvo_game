package com.java.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @RequestMapping("/games")
    private Map<String, Object> gamesDTO(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (!isGuest(authentication)) {
            dto.put("player", playerDTO(playerRepository.findByEmail(authentication.getName())));
        } else {
            dto.put("error", "not logged or not exist");
        }
        dto.put("games", getAll());
        return dto;
    }

    //return true = no logged
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    public List<Object> getAll() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> gameDTO(game))
                .collect(Collectors.toList());
    }

    private Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("date", game.getDate());
        dto.put("gamePlayers", game.getGamePlayerSet()
                .stream().map(gamePlayer -> gamePlayerDTO(gamePlayer))
                .collect(Collectors.toList()));
        dto.put("leaderboard", playerRepository
                .findAll()
                .stream()
                .map(player -> playersDTO(player))
                .collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> gamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("date", gamePlayer.getDate());
        dto.put("players", playerDTO(gamePlayer.getPlayer()));
        if (gamePlayer.getScore() == null) {
            dto.put("scores", gamePlayer.getScore());
        } else {
            dto.put("scores", gamePlayer.getScore().getScore());
        }
        return dto;
    }

    private Map<String, Object> playerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());
        return dto;
    }

    @RequestMapping("/game_view/{id}")
    public Map<String, Object> gameDTOId(@PathVariable Long id) {
        GamePlayer current = gamePlayerRepository.getOne(id);
        Map<String, Object> dto = gameDTO(current.getGame());
        dto.put("Ships", current.getShipSet().stream().map(ship -> shipsDTO(ship))
                .collect(Collectors.toList()));
        Set<GamePlayer> gamePlayerSet = current.getGame().getGamePlayerSet();
        dto.put("Salvos", createSalvosDTO(gamePlayerSet));
        return dto;
    }

    private Map<String, Object> shipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", ship.getId());
        dto.put("ship Type", ship.getShipName());
        dto.put("location", ship.getLocations());
        return dto;
    }

    private Map<String, Object> createSalvosDTO(Set<GamePlayer> gameplayerSet) {
        Map<String, Object> dto = new LinkedHashMap<>();
        for (GamePlayer gamePlayer : gameplayerSet) {
            dto.put(gamePlayer.getId().toString(), createTurnsDTO(gamePlayer.getSalvoSet()));
        }
        return dto;
    }

    private Map<String, Object> createTurnsDTO(Set<Salvo> salvoSet) {
        Map<String, Object> dto = new LinkedHashMap<>();
        for (Salvo salvo : salvoSet) {
            dto.put(salvo.getTurn().toString(), salvo.getLocations());
        }
        return dto;
    }

  /*  @RequestMapping("/leaderboard")
    public List<Object> findAll() {
        return playerRepository
                .findAll()
                .stream()
                .map(player -> playersDTO(player))
                .collect(Collectors.toList());
    }*/

    private Map<String, Object> playersDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());
        dto.put("gamePlayers", player.getGamePlayerSet()
                .stream().map(gamePlayer -> gamePlayersDTO(gamePlayer))
                .collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> gamePlayersDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (gamePlayer.getScore() == null) {
            dto.put("scores", gamePlayer.getScore());
        } else {
            dto.put("scores", gamePlayer.getScore().getScore());
        }
        return dto;
    }

/*
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createPlayer(@RequestParam String email, String password) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No email", HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByEmail(email);
        if (player != null) {
            return new ResponseEntity<>("Name already used", HttpStatus.CONFLICT);
        }
        playerRepository.save(new Player(email, password));
        return new ResponseEntity<>("Named added", HttpStatus.CREATED);
    }
*/

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.UNAUTHORIZED);
        }
        playerRepository.save(new Player(email, password));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}