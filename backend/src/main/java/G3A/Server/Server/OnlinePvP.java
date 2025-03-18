package G3A.Server.Server;

import Logic.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class OnlinePvP {
    static public HashMap<String, OnlinePvPGame> OnlinePvPGameMap = new HashMap<>();

    @PostMapping(value = "/InitGame/CreateParty", produces = "application/json", consumes = "application/json")
    public ResponseEntity createParty(@RequestBody Types.PartyCode partyCode, HttpServletResponse response) {
        var sessionID = UUID.randomUUID().toString();
        OnlinePvPGameMap.put(partyCode.partyCode, new OnlinePvPGame(sessionID, partyCode.partyCode));
        response.setHeader("Set-Cookie", String.format("sessionID=%s; Path=/", sessionID));
        System.out.println("Started party successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/InitGame/JoinParty", produces = "application/json", consumes = "application/json")
    public ResponseEntity joinParty(@RequestBody Types.PartyCode partyCode, HttpServletResponse response) {

        var sessionID = UUID.randomUUID().toString();

        System.out.println(partyCode.partyCode);
        var game = OnlinePvPGameMap.get(partyCode.partyCode);

        response.setHeader("Set-Cookie", String.format("sessionID=%s; Path=/", sessionID));

        try {
            game.waitForPlayer2.put(sessionID);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        System.out.println("Joined party successfully");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/game/OnlinePvP/PostMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Types.ResponsePackage> playerMove(@RequestBody Types.OnlinePvPMovePackage movePackage, @CookieValue(value="sessionID", defaultValue="") String sessionID) {

        var game = OnlinePvPGameMap.get(movePackage.partyCode);

        if (game.Player1.equals(sessionID)) {

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

        } else if (game.Player2.equals(sessionID)) {

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
        return new ResponseEntity<>(new Types.ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
    }

    // GET THE OPPONENT'S MOVE
    @PostMapping(value = "/game/OnlinePvP/GetMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Types.MoveAndGameStatus> getPlayerMove(@RequestBody Types.PartyCode partyCode, @CookieValue(value="sessionID", defaultValue="") String sessionID) {

        var game = OnlinePvPGameMap.get(partyCode.partyCode);

        if (game.Player2 == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        if (game.Player1.equals(sessionID)) {
            try {
                return new ResponseEntity<>(game.getPlayer2Move.take(), HttpStatus.OK);
            } catch (InterruptedException e) {
            }

        } else if (game.Player2.equals(sessionID)) {
            try {
                return new ResponseEntity<>(game.getPlayer1Move.take(), HttpStatus.OK);
            } catch (InterruptedException e) {
            }
        }
        return new ResponseEntity<>(new Types.MoveAndGameStatus(-1, -1, -1), HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/game/OnlinePvP/CheckWin", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Types.GameStatus> checkWin(@RequestBody Types.PartyCode partyCode) {
        var onlineGame = OnlinePvPGameMap.get(partyCode.partyCode);
        if (onlineGame.game.getGameOver() == true) {
            return new ResponseEntity<>(new Types.GameStatus(onlineGame.game.getWinner()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Types.GameStatus(-1), HttpStatus.OK);
    }




    public static class OnlinePvPGame {
        public String partyCode;
        public String Player1;
        public String Player2 = null;
        public int currentTurn = 1;
        public Game game;
        public Thread gameThread;
        public BlockingQueue<Move> putPlayer1Move;
        public BlockingQueue<Types.ResponsePackage> Player1MoveResponse;
        public BlockingQueue<Types.MoveAndGameStatus> getPlayer1Move;
        public BlockingQueue<Move> putPlayer2Move;
        public BlockingQueue<Types.ResponsePackage> Player2MoveResponse;
        public BlockingQueue<Types.MoveAndGameStatus> getPlayer2Move;
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
                                this.getPlayer1Move.put(new Types.MoveAndGameStatus(player1Move.col, player1Move.row, -1));
                            } catch (InterruptedException e) {
                            }
                            try {
                                this.Player1MoveResponse.put(new Types.ResponsePackage(true, -1));
                            } catch (InterruptedException e) {
                            }
                            ;
                        } else if (game.getGameOver() == true) {
                            try {
                                this.getPlayer1Move
                                        .put(new Types.MoveAndGameStatus(player1Move.col, player1Move.row, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            try {
                                this.Player1MoveResponse.put(new Types.ResponsePackage(true, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            ;
                        }
                        try {
                            this.Player1MoveResponse.put(new Types.ResponsePackage(true, game.getWinner()));
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
                                this.getPlayer2Move.put(new Types.MoveAndGameStatus(player2Move.col, player2Move.row, -1));
                            } catch (InterruptedException e) {
                            }
                            try {
                                this.Player2MoveResponse.put(new Types.ResponsePackage(true, -1));
                            } catch (InterruptedException e) {
                            }
                            ;
                        } else if (game.getGameOver() == true) {
                            try {
                                this.getPlayer2Move
                                        .put(new Types.MoveAndGameStatus(player2Move.col, player2Move.row, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            ;
                            try {
                                this.Player2MoveResponse.put(new Types.ResponsePackage(true, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                            ;
                        }
                        try {
                            this.Player2MoveResponse.put(new Types.ResponsePackage(true, game.getWinner()));
                        } catch (InterruptedException e) {
                        }
                        ;
                        System.out.println("finished turn of player 2");
                    }
                }
            });
        }
    }
}
