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

import G3A.Server.Server.Types.ResponsePackage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class LocalPvP {

    static public HashMap<String, LocalPvPGame> localPvPGameMap = new HashMap<>();

    @GetMapping(value = "/InitGame/LocalPvP")
    public void initLocalPvPGame(HttpServletResponse response) {
        System.out.println("got request");
        var sessionID = UUID.randomUUID().toString();
        localPvPGameMap.put(sessionID, new LocalPvPGame(sessionID));
        String cookieValue = String.format("sessionID=%s; Path=/", sessionID);
        response.setHeader("Set-Cookie", cookieValue);
    }

    @GetMapping(value="/test")
    public void test(@CookieValue(value="sessionID", defaultValue = "") String sessionID, HttpServletRequest request) {
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            System.out.println(headerName + ": " + request.getHeader(headerName));
        });
        System.out.println("id: " + sessionID);
    }

    @PostMapping(value = "/game/LocalPvP", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Types.ResponsePackage> LocalPvPResponse(@CookieValue(value="sessionID", defaultValue="") String sessionID, @RequestBody Types.MovePackage movePackage) {
        LocalPvPGame game = localPvPGameMap.get(sessionID);
        Integer gameResult; 
        try {
            game.outToIn.put(new Move(5 - movePackage.row, movePackage.col));
        } catch (InterruptedException e) {
            return new ResponseEntity<>(new ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
        }
        try {
            gameResult = game.inToOut.take();
        } catch (InterruptedException e) {
            return new ResponseEntity<>(new ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
        }
        if (gameResult != -1) {
            localPvPGameMap.remove(sessionID);
        }
        return new ResponseEntity<>(new ResponsePackage(true, gameResult),HttpStatus.OK);
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

}
