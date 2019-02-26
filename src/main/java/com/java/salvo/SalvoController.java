package com.java.salvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static java.util.stream.Collectors.toList;

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

    /*---------------------GET REQUEST MAPPINGS-----------------------*/
    @RequestMapping("/games")
    private Map<String, Object> getMapGamesDTO(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("ERROR", "not logged or not exist");
        } else {
            dto.put("player", makeMapPlayerDTO(playerRepository.findByEmail(authentication.getName())));
        }
        dto.put("games", getAll());
        return dto;
    }

    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Object> responseGameDTOId(@PathVariable Long id, Authentication authentication) {

        GamePlayer currentGp = gamePlayerRepository.getOne(id);
        Player logged = playerRepository.findByEmail(authentication.getName());
        Set<GamePlayer> gamePlayerSet = currentGp.getGame().getGamePlayerSet();

        if (currentGp.getPlayer().getId().equals(logged.getId())) {
            Map<String, Object> dto = makeMapGameDTO(currentGp.getGame());
            dto.put("Ships", currentGp.getShipSet()
                    .stream()
                    .map(ship -> makeMapShipsDTO(ship))
                    .collect(toList()));
            dto.put("Salvos", makeMapSalvosDTO(gamePlayerSet));
            dto.put("ShipSize", makeMapShipHitsDto());
            dto.put("Hits", makeMapHittedDto(currentGp));

            return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(makeMap("ERROR", "Return back CHEATER!"), HttpStatus.UNAUTHORIZED);
    }



    /*----------------------POST REQUEST MAPPING, RESPONSE ENTITY-----------------------------*/
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> responseGame(Authentication authentication) {
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
    public ResponseEntity<Object> responeJoinGame(@PathVariable Long id, Authentication authentication) {
        Player currentPlayer = playerRepository.findByEmail(authentication.getName());
        Game currentGame = gameRepository.getOne(id);

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("ERROR", "Login first"), HttpStatus.UNAUTHORIZED);
        }
        if (currentGame == null) {
            return new ResponseEntity<>(makeMap("ERROR", "This game not exist")
                    , HttpStatus.FORBIDDEN);
        }
        if (currentGame.getGamePlayerSet().size() >1) {
            return new ResponseEntity<>(makeMap("ERROR", "This game is full")
                    , HttpStatus.NOT_ACCEPTABLE);
        } else {
            GamePlayer currentGp = gamePlayerRepository.save(new GamePlayer(currentPlayer, currentGame));
            return new ResponseEntity<>(makeMap("gpID", currentGp.getId()), HttpStatus.CREATED);
        }
    }

        @RequestMapping(path="/games/players/{gamePlayerId}/ships", method=RequestMethod.POST)
        public ResponseEntity<Object> responseCreateShipset(@PathVariable Long gamePlayerId,
        Authentication authentication, @RequestBody Set<Ship> shipSet) {
            Player currentPlayer = playerRepository.findByEmail(authentication.getName());
            GamePlayer currentGp = gamePlayerRepository.getOne(gamePlayerId);

            if (isGuest(authentication)) {
                return new ResponseEntity<>(makeMap("ERROR", "Login first")
                        , HttpStatus.FORBIDDEN);
            }
            if (currentGp == null) {
                return new ResponseEntity<>(makeMap("ERROR", "The gamePlayer does not exist")
                        , HttpStatus.NOT_ACCEPTABLE);
            }
            if (!currentGp.getPlayer().equals(currentPlayer)) {
                return new ResponseEntity<>(makeMap("ERROR", "Cheater, return to your game!")
                        , HttpStatus.UNAUTHORIZED);
            }
            if (currentGp.getShipSet().equals(null)) {
                return new ResponseEntity<>(makeMap("ERROR", "The ships are not placed")
                        , HttpStatus.NOT_FOUND);
            }
            if (currentGp.getShipSet().size() != 0) {
                return new ResponseEntity<>(makeMap("ERROR", "Ships already has been placed")
                        , HttpStatus.FOUND);
            } else {
                for (Ship ship : shipSet) {
                    ship.setGamePlayer(currentGp);
                    shipRepository.save(ship);
                }
                return new ResponseEntity<>(makeMap("ok", "Created"), HttpStatus.CREATED);
            }
        }

    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes", method=RequestMethod.POST)
    public ResponseEntity<Object> responseCreateSalvoes(@PathVariable Long gamePlayerId,
                                              Authentication authentication, @RequestBody Salvo salvo) {

        Player currentPlayer = playerRepository.findByEmail(authentication.getName());
        GamePlayer currentGp = gamePlayerRepository.getOne(gamePlayerId);
        GamePlayer oppGamePlayer = opponentGpDTO(currentGp);

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("ERROR", "Login first")
                    , HttpStatus.FORBIDDEN);
        }
        if (currentGp == null) {
            return new ResponseEntity<>(makeMap("ERROR", "The gamePlayer does not exist")
                    , HttpStatus.NOT_ACCEPTABLE);
        }
        if (!currentGp.getPlayer().equals(currentPlayer)) {
            return new ResponseEntity<>(makeMap("ERROR", "Cheater, return to your game!")
                    , HttpStatus.UNAUTHORIZED);
        } else if(oppGamePlayer.getShipSet().size() == 0){
            return new ResponseEntity<>(makeMap("error", "Wait your Opponent to place his ships"), HttpStatus.FORBIDDEN);
        }
        if (currentGp.getSalvoSet().equals(null)){
            return new ResponseEntity<>(makeMap("ERROR", "The salvoes are placed, max 5")
                    , HttpStatus.FOUND);
        }
        if (currentGp.getSalvoSet().size() != (oppGamePlayer.getSalvoSet().size())) {
            return new ResponseEntity<>(makeMap("ERROR", "Wait for an opponent Salvoes")
                    , HttpStatus.NOT_ACCEPTABLE);
        } else {
            salvo.setTurn(currentGp.getSalvoSet().size() + 1);
            currentGp.addSalvo(salvo);
            salvoRepository.save(salvo);

            Game currentGame = currentGp.getGame();
            Player opponentPlayer = oppGamePlayer.getPlayer();

          /*  if (getGameState(currentGp) == GameState.GameOver_Won) {
                Score currentScoreWin = new Score(1.0, currentGame, currentPlayer);
                Score opponScoreLost = new Score(0.0, currentGame, opponentPlayer);
                ScoreRepository.save(currentScoreWin);
                ScoreRepository.save(opponScoreLost);

            } else if (getGameState(currentGp) == GameState.GameOver_Lost) {
                Score currentScoreLost = new Score(0.0, currentGame, currentPlayer);
                Score oppoScoreWin = new Score(1.0, currentGame, opponentPlayer);
                ScoreRepository.save(currentScoreLost);
                ScoreRepository.save(oppoScoreWin);

            } else if (getGameState(currentGp) == GameState.GameOver_Tied) {
                Score currentScoreTied = new Score(0.5, currentGame, currentPlayer);
                Score oppoScoreTied = new Score(0.5, currentGame, opponentPlayer);
                ScoreRepository.save(currentScoreTied);
                ScoreRepository.save(oppoScoreTied);
            }*/
        }
            return new ResponseEntity<>(makeMap("ok", "Created"), HttpStatus.CREATED);
        }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> responseRegister(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("ERROR", "Empty input"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>(makeMap("ERROR", "Name already in use"), HttpStatus.UNAUTHORIZED);
        } else {
            playerRepository.save(new Player(email, password));
            return new ResponseEntity<>(makeMap("ERROR", "created"), HttpStatus.CREATED);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /*----------------------MAKE MAP DTO-----------------------------*/
    public List<Object> getAll() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> makeMapGameDTO(game))
                .collect(toList());
    }

    private Map<String, Object> makeMapGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("date", game.getDate());
        dto.put("gamePlayers", game.getGamePlayerSet()
                .stream().map(gamePlayer -> makeMapGamePlayerDTO(gamePlayer))
                .collect(toList()));
        dto.put("leaderboard", playerRepository
                .findAll()
                .stream()
                .map(player -> makeMapPlayersDTO(player))
                .collect(toList()));
        return dto;
    }

    private Map<String, Object> makeMapGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("date", gamePlayer.getDate());
        dto.put("players", makeMapPlayerDTO(gamePlayer.getPlayer()));
        if (gamePlayer.getScore() == null) {
            dto.put("scores", gamePlayer.getScore());
        } else {
            dto.put("scores", gamePlayer.getScore().getScore());
        }
        return dto;
    }

    private Map<String, Object> makeMapPlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());
        return dto;
    }

    private Map<String,Integer> makeMapShipHitsDto(){
        Map<String,Integer> dto = new HashMap<String,Integer>();
        dto.put("PatrolBoat", 2);
        dto.put("Destroyer", 3);
        dto.put("Submarine", 3);
        dto.put("Battleship", 4);
        dto.put("AircraftCarrier", 5);
        dto.put("AllShips", 5);

        return dto;
    }

    private Map<String, Object> makeMapHittedDto (GamePlayer currentGp) {
        Map<String, Object> dto = new HashMap<String, Object>();

        GamePlayer oppGamePlayer = opponentGpDTO(currentGp);
        Set<Ship> oppGpShipSet = oppGamePlayer.getShipSet();
        Set<Salvo> currentGpSalvoSet = currentGp.getSalvoSet();

        List<String> salvoCurrLoc = currentGpSalvoSet
                .stream()
                .map(salvo -> salvo.getLocations())
                .flatMap(s -> s.stream())
                .collect(toList());
        System.out.println("salvo"+salvoCurrLoc);

        List<String> OppShips = oppGpShipSet
                .stream()
                .map(ship -> ship.getLocations()).flatMap(sh -> sh.stream())
                .collect(toList());
        System.out.println(("oppShips"+OppShips));

        List<String> hits = OppShips.stream().filter(s -> salvoCurrLoc.contains(s)).collect(toList());
        System.out.println("hitlist"+hits);

        for (Ship ship : oppGpShipSet) {
            for (String hit: hits) {
                if (ship.getLocations().contains(hit)) {
                    dto.put(hit, ship.getShipName());
                }
            }
        }

        return dto;
    }

    private Map<String, Object> makeMapShipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", ship.getId());
        dto.put("shipName", ship.getShipName());
        dto.put("location", ship.getLocations());

        return dto;
    }

    private Map<String, Object> makeMapSalvosDTO(Set<GamePlayer> gameplayerSet) {
        Map<String, Object> dto = new LinkedHashMap<>();
        for (GamePlayer gamePlayer : gameplayerSet) {
            dto.put(gamePlayer.getId().toString(), makeMapTurnsDTO(gamePlayer.getSalvoSet()));
        }
        return dto;
    }

    private Map<String, Object> makeMapTurnsDTO(Set<Salvo> salvoSet) {
        Map<String, Object> dto = new LinkedHashMap<>();
        for (Salvo salvo : salvoSet) {
            dto.put(salvo.getTurn().toString(), salvo.getLocations());
        }
        return dto;
    }

    private Map<String, Object> makeMapPlayersDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());
        dto.put("gamePlayers", player.getGamePlayerSet()
                .stream().map(gamePlayer -> makeMapGpScoresDTO(gamePlayer))
                .collect(toList()));
        return dto;
    }

    private Map<String, Object> makeMapGpScoresDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (gamePlayer.getScore() == null) {
            dto.put("scores", gamePlayer.getScore());
        } else {
            dto.put("scores", gamePlayer.getScore().getScore());
        }
        return dto;
    }

    /*-----------------GET AUTHENTICATED-----------------*/
    //return true = no logged
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    /*----------------GET OPPONENT GAMEPLAYER-------------*/
    private GamePlayer opponentGpDTO(GamePlayer gamePlayer) {
        return  gamePlayer.getGame().getGamePlayerSet()
                .stream()
                .filter(gp -> !gp.getId().equals(gamePlayer))
                .findFirst().orElse(null);
    }
}
