package com._98point6.dt.dtbackend;

import com._98point6.dt.dtbackend.domain.Game;
import com._98point6.dt.dtbackend.domain.Move;
import com._98point6.dt.dtbackend.domain.Player;
import com._98point6.dt.dtbackend.dto.CreateGameRequest;
import com._98point6.dt.dtbackend.dto.CreateGameResponse;
import com._98point6.dt.dtbackend.dto.GameStatusResponse;
import com._98point6.dt.dtbackend.dto.GetMoveResponse;
import com._98point6.dt.dtbackend.dto.PostMoveRequest;
import com._98point6.dt.dtbackend.dto.PostMoveResponse;
import com._98point6.dt.dtbackend.repository.GameRepository;
import com._98point6.dt.dtbackend.repository.MovesRepository;
import com._98point6.dt.dtbackend.repository.PlayerRepository;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: Awdesh Sharma
 */
@RestController
public class DropTokenController {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenController.class);

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MovesRepository movesRepository;

    DropTokenGame dropTokenGame;

    @Autowired
    public DropTokenController(DropTokenGame dropTokenGame) {
        this.dropTokenGame = dropTokenGame;
    }

    @GetMapping
    public ResponseEntity<List<CreateGameResponse>> getAllInProgressGame() {
        List<Game> games = gameRepository.findAll().
                            stream().filter(g -> g.getState().equals(GameState.PROGRESS.toString()))
                            .collect(Collectors.toList());

        List<CreateGameResponse> createGameResponseList = new ArrayList<>();
        for (Game game: games) {
            game.setGameId("game-".concat(game.getId().toString()));

            CreateGameResponse createGameResponse = new CreateGameResponse();
            createGameResponse.setGameId(game.getGameId());
            createGameResponseList.add(createGameResponse);
        }
        return ResponseEntity.ok(createGameResponseList);
    }

    @PostMapping
    public ResponseEntity<CreateGameResponse> createNewGame(@RequestBody CreateGameRequest newGame) {
        logger.info("request={}", newGame);

        Preconditions.checkNotNull(newGame);

        if(newGame.getColumns() < 4 || newGame.getRows() < 4 || newGame.getPlayers().size() < 2) {
            return ResponseEntity.badRequest().build();
        }

        Game game = new Game(newGame.getRows(), newGame.getColumns());
        gameRepository.save(game);

        List<String> players = newGame.getPlayers();
        for (String playerName: players) {
            Player player = new Player(playerName, game);
            playerRepository.save(player);
        }

        CreateGameResponse createGameResponse = new CreateGameResponse();
        game.setGameId("game-".concat(game.getId().toString()));
        gameRepository.save(game);
        createGameResponse.setGameId(game.getGameId());

        dropTokenGame.setUp(game.getGameId(), newGame.getRows(), newGame.getColumns());

        return ResponseEntity.status(HttpStatus.CREATED).body(createGameResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameStatusResponse> getGameStatus(@PathVariable("id") String gameId) {
        logger.info("gameId = {}", gameId);
        Game game = gameRepository.findByGameId(gameId);
        if(game == null) {
            return ResponseEntity.notFound().build();
        }
        List<String> playerNames = playerRepository.findByGame(game).stream()
                                                    .flatMap(p -> Stream.of(p.getPlayerId()))
                                                    .collect(Collectors.toList());

        GameStatusResponse gameStatusResponse = new GameStatusResponse();
        if(game.getState().equals(GameState.DRAW.toString())) {
            gameStatusResponse.setWinner(null);
        }
        if(game.getState().equals(GameState.WIN.toString())) {
            String winningPlayerName = playerRepository.findByGame(game)
                                                        .stream().filter(Player::isWinner)
                                                        .collect(Collectors.toList()).get(0).getPlayerId();
            gameStatusResponse.setWinner(winningPlayerName);
        }
        gameStatusResponse.setState(game.getState());
        gameStatusResponse.setPlayers(playerNames);
        return ResponseEntity.status(HttpStatus.OK).body(gameStatusResponse);
    }

    @PostMapping("/{id}/{playerId}")
    public ResponseEntity<PostMoveResponse> postMove(@PathVariable("id")String gameId,
                                                     @PathVariable("playerId") String playerId,
                                                     @RequestBody PostMoveRequest request) {

        logger.info("gameId={}, playerId={}, move={}", gameId, playerId, request);
        Preconditions.checkNotNull(request);

        if(!dropTokenGame.isPlayerTurn(playerId)) {
             return ResponseEntity.status(HttpStatus.CONFLICT).build();
         }

        Game game = gameRepository.findByGameId(gameId);
        if(game == null) {
            return ResponseEntity.notFound().build();
        }

        GameState gameState = dropTokenGame.put(request.getColumn(), gameId, playerId);
        switch (gameState) {
            case FULL:
                return ResponseEntity.badRequest().build();
            case WIN:
                List<Player> players = playerRepository.findByGame(game).stream().filter(p -> p.getPlayerId().equals(playerId)).collect(Collectors.toList());
                // set the current player as winner.
                players.get(0).setWinner(true);
                game.setState(GameState.WIN.toString());
            case DRAW:
                game.setState(GameState.DONE.toString());
        }

        Optional<Player> player = playerRepository.findById(Integer.valueOf(playerId));
        if(player.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Move move = new Move("MOVE", request.getColumn(), game, player.get());
        movesRepository.save(move);
        PostMoveResponse postMoveResponse = new PostMoveResponse();
        postMoveResponse.setMoveLink(game.getId().toString().concat("/moves").concat("/" + move.getId().toString()));

        return ResponseEntity.status(HttpStatus.OK).body(postMoveResponse);
    }

    @DeleteMapping("/{id}/{playerId}")
    public ResponseEntity playerQuit(@PathVariable("id")String gameId, @PathVariable("playerId") String playerId) {
        logger.info("gameId={}, playerId={}", gameId, playerId);

        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        if(game.getState().equals("DONE")) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
        List<Player> players = playerRepository.findByGame(game).stream()
                                                 .filter(player -> player.getPlayerId().equals(playerId))
                                                 .collect(Collectors.toList());

        if(players.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        playerRepository.delete(players.get(0));

        List<Player> playerList =  playerRepository.findByGame(game);
        for (Player player : playerList) {
            if(player.getPlayerId().equals(playerId)){
                continue;
            }
            player.setWinner(true);
        }
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}/moves")
    public ResponseEntity<List<GetMoveResponse>> getMoves(@PathVariable("id") String gameId,
                                                          @RequestParam (required = false) Integer start,
                                                          @RequestParam(required = false) Integer until) {

        logger.info("gameId={}, start={}, until={}", gameId, start, until);
        Game game = gameRepository.findByGameId(gameId);
        if(game == null){
            return ResponseEntity.notFound().build();
        }
        List<Move> moves = movesRepository.findByGame(game);
        if(moves.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<GetMoveResponse> movesResponseList = new ArrayList<>();

        if(start != null && until != null) {
            if(moves.size() < until || moves.size() < start) {
                return ResponseEntity.badRequest().build();
            }

            for (int i = start; i < until; i++) {
                GetMoveResponse getMoveResponse = new GetMoveResponse(moves.get(i).getType(),
                        moves.get(i).getColumn(),
                        moves.get(i).getPlayer().getPlayerId());
                movesResponseList.add(getMoveResponse);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(movesResponseList);
    }

    @GetMapping("/{id}/moves/{moveId}")
    public ResponseEntity<List<GetMoveResponse>> getMove(@PathVariable("id") String gameId, @PathVariable("moveId") Integer moveId) {
        logger.info("gameId={}, moveId={}", gameId, moveId);

        Game game = gameRepository.findByGameId(gameId);
        if(game == null) {
            return ResponseEntity.notFound().build();
        }
        List<Move> moves = movesRepository.findByGame(game).stream()
                                          .filter(move -> move.getId().equals(moveId))
                                          .collect(Collectors.toList());
        if(moves.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<GetMoveResponse> getMoveResponseList = new ArrayList<>();

        for (Move move : moves) {
            getMoveResponseList.add(new GetMoveResponse(move.getType(), move.getColumn(), move.getPlayer().getPlayerId()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(getMoveResponseList);
    }
}
