package com.java.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
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
        if (isGuest(authentication)) {
            dto.put("ERROR", "not logged or not exist");
        } else {
            dto.put("player", playerDTO(playerRepository.findByEmail(authentication.getName())));
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
    public ResponseEntity<Object> gameDTOId(@PathVariable Long id, Authentication authentication) {
        GamePlayer current = gamePlayerRepository.getOne(id);
        Player logged = playerRepository.findByEmail(authentication.getName());

        if (current.getPlayer().getId().equals(logged.getId())) {
            Map<String, Object> dto = gameDTO(current.getGame());
            dto.put("gameplayers", current.getShipSet().stream().map(ship -> shipsDTO(ship))
                    .collect(Collectors.toList()));
            dto.put("Ships", current.getShipSet().stream().map(ship -> shipsDTO(ship))
                    .collect(Collectors.toList()));
            Set<GamePlayer> gamePlayerSet = current.getGame().getGamePlayerSet();
            dto.put("Salvos", createSalvosDTO(gamePlayerSet));
            return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(makeMap("ERROR", "Return back CHEATER!"), HttpStatus.UNAUTHORIZED);
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

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication) {
        Player currentPlayer = playerRepository.findByEmail(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("ERROR", "SignUp first for play"), HttpStatus.UNAUTHORIZED);
        } else {
            Game currentGame = gameRepository.save(new Game());
            GamePlayer currentGp = gamePlayerRepository.save(new GamePlayer(currentPlayer, currentGame));
            return new ResponseEntity<>(makeMap("gpID", currentGp.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/game/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@PathVariable Long id, Authentication authentication) {
        Player currentPlayer = playerRepository.findByEmail(authentication.getName());
        Game currentGame = gameRepository.getOne(id);

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("ERROR", "Login first"), HttpStatus.UNAUTHORIZED);
        } else if (currentGame == null) {
            return new ResponseEntity<>(makeMap("ERROR", "This game not exist")
                    , HttpStatus.FORBIDDEN);
        } else if (currentGame.getGamePlayerSet().size() >1) {
            return new ResponseEntity<>(makeMap("ERROR", "This game is full")
                    , HttpStatus.NOT_ACCEPTABLE);
        } else {
            GamePlayer currentGp = gamePlayerRepository.save(new GamePlayer(currentPlayer, currentGame));
            return new ResponseEntity<>(makeMap("gpID", currentGp.getId()), HttpStatus.CREATED);
        }
    }

        @RequestMapping(path="/games/players/{gamePlayerId}/ships", method=RequestMethod.POST)
        public ResponseEntity<Object> createShips(@PathVariable Long gamePlayerId,
        Authentication authentication, @RequestBody Set<Ship> shipSet) {
            Player currentPlayer = playerRepository.findByEmail(authentication.getName());
            GamePlayer currentGp = gamePlayerRepository.getOne(gamePlayerId);

            if (isGuest(authentication)) {
                return new ResponseEntity<>(makeMap("ERROR", "Login first")
                        , HttpStatus.FORBIDDEN);
            } else if (currentGp == null) {
                return new ResponseEntity<>(makeMap("ERROR", "The gamePlayer does not exist")
                        , HttpStatus.NOT_ACCEPTABLE);
            } else if (!currentGp.getPlayer().equals(currentPlayer)) {
                return new ResponseEntity<>(makeMap("ERROR", "Cheater, return to your game!")
                        , HttpStatus.UNAUTHORIZED);
            } else if (currentGp.getShipSet().equals(null)){
                return new ResponseEntity<>(makeMap("ERROR", "The ships are placed")
                        , HttpStatus.FOUND);
            } else {
                for (Ship ship : shipSet) {
                    ship.setGamePlayer(currentGp);
                    shipRepository.save(ship);
                }
                return new ResponseEntity<>(makeMap("ok", "Created"), HttpStatus.CREATED);
            }
        }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("ERROR", "Empty input"), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>(makeMap("ERROR", "Name already in use"), HttpStatus.UNAUTHORIZED);
        }
        playerRepository.save(new Player(email, password));
        return new ResponseEntity<>(makeMap("ERROR", "created"), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}