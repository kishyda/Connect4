package G3A.Server.Server;
import Logic.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class PvAI {
    static public HashMap<String, PvAIGame> pvAIGameMap = new HashMap<>();

    @PostMapping(value = "/InitGame/PvAI", consumes = "application/json")
    public ResponseEntity initPvAI(@RequestBody Types.InitPvAIPackage initPvAIPackage, HttpServletResponse response) {
        var sessionID = UUID.randomUUID().toString();
        pvAIGameMap.put(sessionID, new PvAIGame(sessionID, initPvAIPackage.difficulty));
        response.setHeader("Set-Cookie", String.format("sessionID=%s; Path=/", sessionID));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/game/PvAI/PlayerMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Types.ResponsePackage> PvAIPlayerResponse(@CookieValue(value="sessionID", defaultValue="") String sessionID, @RequestBody Types.MovePackage movePackage) {
        PvAIGame game = pvAIGameMap.get(sessionID);
        try {
            System.out.printf("col: %d, row: %d", movePackage.col, 5 - movePackage.row);
            game.outToIn.put(new Move(5 - movePackage.row, movePackage.col));
            var response = game.inToOut.take();
            if (response.winner != -1) {
                pvAIGameMap.remove(sessionID);
            }
            return new ResponseEntity<>(new Types.ResponsePackage(true, response.winner), HttpStatus.OK);
        } catch (InterruptedException e) {
            System.out.println("it failed");
            return new ResponseEntity<>(new Types.ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/game/PvAI/CPUMove", produces = "application/json")
    public ResponseEntity<Types.MoveAndGameStatus> PvAICPUResponse(@CookieValue(value="sessionID", defaultValue="") String sessionID) {

        PvAIGame game = pvAIGameMap.get(sessionID);

        if (game == null) {
            return new ResponseEntity<>(new Types.MoveAndGameStatus(-1, -1, -1), HttpStatus.OK);
        }

        try {
            Types.MoveAndGameStatus moveAndGameStatus = game.inToOut.peek();
            if (moveAndGameStatus == null) {
                return new ResponseEntity<>(new Types.MoveAndGameStatus(-1, -1, -1), HttpStatus.OK);
            }
            moveAndGameStatus = game.inToOut.take();
            if (moveAndGameStatus.winner != -1) {
                pvAIGameMap.remove(sessionID);
            }
            return new ResponseEntity<>(
                    new Types.MoveAndGameStatus(moveAndGameStatus.row, moveAndGameStatus.col, moveAndGameStatus.winner),
                    HttpStatus.OK);

        } catch (InterruptedException e) {
            System.out.println("it it failed");
            return new ResponseEntity<>(new Types.MoveAndGameStatus(-1, -1, -1), HttpStatus.BAD_REQUEST);
        }
    }

    public static class PvAIGame {
        public String sessionID;
        public Thread gameThread;
        public BlockingQueue<Move> outToIn;
        public BlockingQueue<Types.MoveAndGameStatus> inToOut;

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
                    if (p2p || game.activePlayer == 0) {
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
                                this.inToOut.put(new Types.MoveAndGameStatus(-1, -1, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                        }
                        if (game.getGameOver() == false) {
                            try {
                                this.inToOut.put(new Types.MoveAndGameStatus(-1, -1, -1));
                            } catch (InterruptedException e) {
                            }
                        }
                    } else if (!p2p && game.activePlayer == 1) {
                        Move computerMove = game.step(game.activePlayer);
                        game.activePlayer = (game.activePlayer + 1) % 2;
                        if (game.getGameOver() == false) {
                            try {
                                this.inToOut.put(new Types.MoveAndGameStatus(computerMove.row, computerMove.col, -1));
                            } catch (InterruptedException e) {
                            }
                        }
                        if (game.getGameOver()) {
                            try {
                                this.inToOut.put(
                                        new Types.MoveAndGameStatus(computerMove.row, computerMove.col, game.getWinner()));
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    game.board.printBoard();

                };
            });
        }
    }
}
