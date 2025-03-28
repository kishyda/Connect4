package G3A.Server.Server;

import Logic.*;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS configuration class
@Configuration
class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all endpoints
                .allowedOrigins("http://localhost:3000") // Your frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}

@SpringBootApplication
@RestController
public class ServerApplication {
    static public HashMap<String, LocalPvPGame> localPvPGameMap = new HashMap<>();
    static public HashMap<String, PvAIGame> pvAIGameMap = new HashMap<>();
    static public HashMap<String, OnlinePvPGame> OnlinePvPGameMap = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @PostMapping(value = "/InitGame/LocalPvP", produces = "application/json", consumes = "application/json")
    public ResponseEntity<SessionID2> initLocalPvPGame(@RequestBody SessionID2 session) {
        System.out.println(session);
        localPvPGameMap.put(session.sessionID, new LocalPvPGame(session.sessionID));
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @PostMapping(value = "/game/LocalPvP", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ResponsePackage> LocalPvPResponse(@RequestBody MovePackage movePackage) {
        LocalPvPGame game = localPvPGameMap.get(movePackage.sessionID);
        try {
            game.outToIn.put(new Move(5 - movePackage.row, movePackage.col));
        } catch (InterruptedException e) {
            return new ResponseEntity<>(new ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(new ResponsePackage(true, game.inToOut.take()), HttpStatus.OK);
        } catch (InterruptedException e) {
        }
        return new ResponseEntity<>(new ResponsePackage(true, -1), HttpStatus.OK);
    }

    @PostMapping(value = "/InitGame/PvAI", produces = "application/json", consumes = "application/json")
    public ResponseEntity<SessionID> initPvAI(@RequestBody InitPvAIPackage initPvAIPackage) {
        pvAIGameMap.put(initPvAIPackage.sessionID, new PvAIGame(initPvAIPackage.sessionID, initPvAIPackage.difficulty));
        return new ResponseEntity<>(new SessionID(initPvAIPackage.sessionID), HttpStatus.OK);
    }

    @PostMapping(value = "/game/PvAI/PlayerMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ResponsePackage> PvAIPlayerResponse(@RequestBody MovePackage movePackage) {
        PvAIGame game = pvAIGameMap.get(movePackage.sessionID);
        try {
            System.out.printf("col: %d, row: %d", movePackage.col, 5 - movePackage.row);
            game.outToIn.put(new Move(5 - movePackage.row, movePackage.col));
            var response = game.inToOut.take();
            return new ResponseEntity<>(new ResponsePackage(true, response.winner), HttpStatus.OK);
        } catch (InterruptedException e) {
            System.out.println("it failed");
            return new ResponseEntity<>(new ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/game/PvAI/CPUMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MoveAndGameStatus> PvAICPUResponse(@RequestBody MovePackage movePackage) {

        PvAIGame game = pvAIGameMap.get(movePackage.sessionID);

        if (game == null) {
            return new ResponseEntity<>(new MoveAndGameStatus(-1, -1, -1), HttpStatus.OK);
        }

        try {
            MoveAndGameStatus moveAndGameStatus = game.inToOut.peek();
            if (moveAndGameStatus == null) {
                return new ResponseEntity<>(new MoveAndGameStatus(-1, -1, -1), HttpStatus.OK);
            }
            moveAndGameStatus = game.inToOut.take();
            return new ResponseEntity<>(
                    new MoveAndGameStatus(moveAndGameStatus.row, moveAndGameStatus.col, moveAndGameStatus.winner),
                    HttpStatus.OK);

        } catch (InterruptedException e) {
            System.out.println("it it failed");
            return new ResponseEntity<>(new MoveAndGameStatus(-1, -1, -1), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/InitGame/CreateParty", produces = "application/json", consumes = "application/json")
    public ResponseEntity createParty(@RequestBody SessionAndParty session) {
        OnlinePvPGameMap.put(session.partyCode, new OnlinePvPGame(session.sessionID, session.partyCode));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/InitGame/JoinParty", produces = "application/json", consumes = "application/json")
    public ResponseEntity joinParty(@RequestBody SessionAndParty session) {

        System.out.println(session.partyCode);
        var game = OnlinePvPGameMap.get(session.partyCode);

        try {
            game.waitForPlayer2.put(session.sessionID);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/game/OnlinePvP/PostMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ResponsePackage> playerMove(@RequestBody OnlinePvPMovePackage movePackage) {

        var game = OnlinePvPGameMap.get(movePackage.partyCode);

        if (game.Player1.equals(movePackage.sessionID)) {

            try {
                game.putPlayer1Move.put(new Move(movePackage.row, movePackage.col));
            } catch (InterruptedException e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            try {
                return new ResponseEntity<>(game.Player1MoveResponse.take(), HttpStatus.OK);
            } catch (InterruptedException e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

        } else if (game.Player2.equals(movePackage.sessionID)) {

            try {
                game.putPlayer2Move.put(new Move(movePackage.row, movePackage.col));
            } catch (InterruptedException e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            try {
                return new ResponseEntity<>(game.Player2MoveResponse.take(), HttpStatus.OK);
            } catch (InterruptedException e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(new ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
    }

    // GET THE OPPONENT'S MOVE
    @PostMapping(value = "/game/OnlinePvP/GetMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MoveAndGameStatus> getPlayerMove(@RequestBody SessionAndParty sessionAndParty) {

        var game = OnlinePvPGameMap.get(sessionAndParty.partyCode);

        if (game.Player2 == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        if (game.Player1.equals(sessionAndParty.sessionID)) {
            try {
                return new ResponseEntity<>(game.getPlayer2Move.take(), HttpStatus.OK);
            } catch (InterruptedException e) {
            }

        } else if (game.Player2.equals(sessionAndParty.sessionID)) {
            try {
                return new ResponseEntity<>(game.getPlayer1Move.take(), HttpStatus.OK);
            } catch (InterruptedException e) {
            }
        }
        return new ResponseEntity<>(new MoveAndGameStatus(-1, -1, -1), HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/game/OnlinePvP/CheckWin", produces = "application/json", consumes = "application/json")
    public ResponseEntity<GameStatus> checkWin(@RequestBody SessionAndParty sessionAndParty) {
        var onlineGame = OnlinePvPGameMap.get(sessionAndParty.partyCode);
        if (onlineGame.game.getGameOver() == true) {
            return new ResponseEntity<>(new GameStatus(onlineGame.game.getWinner()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new GameStatus(-1), HttpStatus.OK);
    }

    public static class GameStatus {
        public int winner;

        public GameStatus(int winner) {
            this.winner = winner;
        }
    }

    public static class OnlinePvPMovePackage {
        public int col;
        public int row;
        public String sessionID;
        public String partyCode;
    }

    public static class SessionID2 {
        public String sessionID;
    }

    public static class SessionAndParty {
        public String sessionID;
        public String partyCode;
    }

    public static class SessionID {
        public String sessionID;

        public SessionID(String sessionID) {
            this.sessionID = sessionID;
        }
    }

    public static class InitPvAIPackage {
        public String sessionID;
        public int difficulty;
    }

    public static class ResponsePackage {
        public boolean successful;
        public int winner;

        public ResponsePackage(boolean status, int winner) {
            this.successful = status;
            this.winner = winner;
        }
    }

    public static class MovePackage {
        public int col;
        public int row;
        public String sessionID;
    }

    public static class MoveAndGameStatus {
        public int row;
        public int col;
        public int winner;

        MoveAndGameStatus(int x, int y, int winner) {
            this.col = x;
            this.row = y;
            this.winner = winner;
        }
    }

    public static class LocalPvPGame {
        public String sessionID;
        public Thread gameThread;
        public BlockingQueue<Move> outToIn;
        public BlockingQueue<Integer> inToOut;

        public LocalPvPGame(String sessionID) {
            this.sessionID = sessionID;
            this.outToIn = new ArrayBlockingQueue<>(1);
            this.inToOut = new ArrayBlockingQueue<>(1);
            this.gameThread = Thread.ofVirtual().start(() -> {
                boolean p2p = false;
                int cpuLevel = 1;
                Game game = new Game(p2p, cpuLevel);
                int activePlayer = 0;
                while (!game.getGameOver()) {
                    Move move = new Move(0, 0);
                    if (p2p || activePlayer == 0) // HUMAN PLAYER STEP
                    {
                        try {
                            move = this.outToIn.take();
                        } catch (InterruptedException e) {
                        }
                        game.step(move, game.activePlayer);
                        game.board.printBoard();
                        game.activePlayer = (game.activePlayer + 1) % 2;
                    } else if (!p2p && game.activePlayer == 1) {
                        game.step(game.activePlayer);
                        game.board.printBoard();
                        game.activePlayer = (game.activePlayer + 1) % 2;
                    }

                    if (game.getGameOver() == false) {
                        try {
                            this.inToOut.put(-1);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                ;
                try {
                    this.inToOut.put(game.getWinner());
                } catch (InterruptedException e) {
                }
            });
        }
    }

    public static class PvAIGame {
        public String sessionID;
        public Thread gameThread;
        public BlockingQueue<Move> outToIn;
        public BlockingQueue<MoveAndGameStatus> inToOut;

        public PvAIGame(String sessionID, int difficulty) {
            this.sessionID = sessionID;
            this.outToIn = new ArrayBlockingQueue<>(2);
            this.inToOut = new ArrayBlockingQueue<>(2);
            this.gameThread = Thread.startVirtualThread(() -> {
                boolean p2p = false;
                int cpuLevel = difficulty;
                if (cpuLevel == 2) {
                    cpuLevel = 0;
                }
                Game game = new Game(p2p, cpuLevel);
                while (!game.getGameOver()) {
                    Move move = new Move(0, 0);
                    if (p2p || game.activePlayer == 0) // HUMAN PLAYER STEP
                    {
                        try {
                            move = this.outToIn.take();
                        } catch (InterruptedException e) {
                        }
                        while (!game.checkLegalMove(game.board, move)) {
                            move = new Move(move.row, move.col);
                        }
                        game.step(move, game.activePlayer);
                        game.activePlayer = (game.activePlayer + 1) % 2;
                        if (game.getGameOver() == true) {
                            try {
                                this.inToOut.put(new MoveAndGameStatus(-1, -1, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                        }
                        if (game.getGameOver() == false) {
                            try {
                                this.inToOut.put(new MoveAndGameStatus(-1, -1, -1));
                            } catch (InterruptedException e) {
                            }
                        }
                    } else if (!p2p && game.activePlayer == 1) { // COMPUTER PLAYER STEP, it will decide move
                                                                 // automatically
                        Move computerMove = game.step(game.activePlayer);
                        game.activePlayer = (game.activePlayer + 1) % 2;
                        if (game.getGameOver() == false) {
                            try {
                                this.inToOut.put(new MoveAndGameStatus(computerMove.row, computerMove.col, -1));
                            } catch (InterruptedException e) {
                            }
                        }
                        if (game.getGameOver()) {
                            try {
                                this.inToOut.put(
                                        new MoveAndGameStatus(computerMove.row, computerMove.col, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    game.board.printBoard();

                }
                ;
            });
        }
    }

    public static class OnlinePvPGame {
        public String partyCode;
        public String Player1;
        public String Player2 = null;
        public int currentTurn = 1;
        public Game game;
        public Thread gameThread;
        public BlockingQueue<Move> putPlayer1Move;
        public BlockingQueue<ResponsePackage> Player1MoveResponse;
        public BlockingQueue<MoveAndGameStatus> getPlayer1Move;
        public BlockingQueue<Move> putPlayer2Move;
        public BlockingQueue<ResponsePackage> Player2MoveResponse;
        public BlockingQueue<MoveAndGameStatus> getPlayer2Move;
        public BlockingQueue<String> waitForPlayer2;

        public OnlinePvPGame(String Player1, String partyCode) {
            this.Player1 = Player1;
            this.partyCode = partyCode;
            this.putPlayer1Move = new ArrayBlockingQueue<>(42);
            this.Player1MoveResponse = new ArrayBlockingQueue<>(42);
            this.getPlayer1Move = new ArrayBlockingQueue<>(42);
            this.putPlayer2Move = new ArrayBlockingQueue<>(42);
            this.Player2MoveResponse = new ArrayBlockingQueue<>(42);
            this.getPlayer2Move = new ArrayBlockingQueue<>(42);
            this.waitForPlayer2 = new ArrayBlockingQueue<>(42);
            this.gameThread = Thread.startVirtualThread(() -> {

                try {
                    String Player2 = this.waitForPlayer2.take();
                    this.Player2 = Player2;
                } catch (InterruptedException e) {
                }

                game = new Game(true, 0);
                while (!game.getGameOver()) {

                    if (this.currentTurn == 1) {
                        System.out.println("Getting player1Move");
                        Move player1Move = new Move(-1, -1);
                        try {
                            player1Move = this.putPlayer1Move.take();
                        } catch (InterruptedException e) {
                        }
                        System.out.println("GOT PLAYER 1 MOVE");

                        game.step(player1Move, game.activePlayer);
                        game.board.printBoard();
                        game.activePlayer = (game.activePlayer + 1) % 2;
                        this.currentTurn = 2;

                        if (game.getGameOver() == false) {
                            try {
                                this.getPlayer1Move.put(new MoveAndGameStatus(player1Move.col, player1Move.row, -1));
                            } catch (InterruptedException e) {
                            }
                            try {
                                this.Player1MoveResponse.put(new ResponsePackage(true, -1));
                            } catch (InterruptedException e) {
                            }
                            ;
                        } else if (game.getGameOver() == true) {
                            try {
                                this.getPlayer1Move
                                        .put(new MoveAndGameStatus(player1Move.col, player1Move.row, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            try {
                                this.Player1MoveResponse.put(new ResponsePackage(true, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            ;
                        }
                        try {
                            this.Player1MoveResponse.put(new ResponsePackage(true, game.getWinner()));
                        } catch (InterruptedException e) {
                        }
                        ;
                        System.out.println("finished turn of player 1");
                    }

                    if (this.currentTurn == 2) {
                        System.out.println("Getting player2Move");
                        Move player2Move = new Move(-1, -1);
                        try {
                            player2Move = this.putPlayer2Move.take();
                        } catch (InterruptedException e) {
                        }
                        System.out.println("GOT PLAYER 2 MOVE");

                        game.step(player2Move, game.activePlayer);
                        game.board.printBoard();
                        game.activePlayer = (game.activePlayer + 1) % 2;
                        this.currentTurn = 1;

                        if (game.getGameOver() == false) {
                            try {
                                this.getPlayer2Move.put(new MoveAndGameStatus(player2Move.col, player2Move.row, -1));
                            } catch (InterruptedException e) {
                            }
                            try {
                                this.Player2MoveResponse.put(new ResponsePackage(true, -1));
                            } catch (InterruptedException e) {
                            }
                            ;
                        } else if (game.getGameOver() == true) {
                            try {
                                this.getPlayer2Move
                                        .put(new MoveAndGameStatus(player2Move.col, player2Move.row, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            ;
                            try {
                                this.Player2MoveResponse.put(new ResponsePackage(true, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            ;
                        }
                        try {
                            this.Player2MoveResponse.put(new ResponsePackage(true, game.getWinner()));
                        } catch (InterruptedException e) {
                        }
                        ;
                        System.out.println("finished turn of player 2");
                    }
                }
                ;
                // try {this.inToOut.put(game.getWinner());}
                // catch(InterruptedException e) {}
            });
        }
    }
}
