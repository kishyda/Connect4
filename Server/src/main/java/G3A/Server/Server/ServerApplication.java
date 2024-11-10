package G3A.Server.Server;

import Logic.*;
import java.util.*;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Error;
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
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @PostMapping(value = "/LogIn", produces = "application/json", consumes="application/json")
    public ResponseEntity<User> logIn(@RequestBody User user) {
        String userId = UUID.randomUUID().toString();
        user.userId = userId;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/CreateAccount", produces = "application/json", consumes="application/json")
    public ResponseEntity<User> createAccount(@RequestBody User user) {
        String userId = UUID.randomUUID().toString();
        user.userId = userId;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/InitGame/LocalPvP", produces = "application/json", consumes="application/json")
    public ResponseEntity<SessionID> initLocalPvPGame(@RequestBody SessionID session) {
        System.out.println(session);
        localPvPGameMap.put(session.sessionID, new LocalPvPGame(session.sessionID));
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @PostMapping(value = "/game/LocalPvP", produces = "application/json", consumes="application/json")
    public ResponseEntity<ResponsePackage> LocalPvPResponse(@RequestBody MovePackage movePackage) {
        LocalPvPGame game = localPvPGameMap.get(movePackage.sessionID);
        try { game.outToIn.put(new Move(movePackage.y, movePackage.x)); } catch (InterruptedException e) {return new ResponseEntity<>(new ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);}
        try{return new ResponseEntity<>(new ResponsePackage(true, game.inToOut.take()), HttpStatus.OK);}
        catch(InterruptedException e) {}
        return new ResponseEntity<>(new ResponsePackage(true, -1), HttpStatus.OK);
    }

    @PostMapping(value = "/InitGame/PvAI", produces = "application/json", consumes="application/json")
    public ResponseEntity<SessionID> initPvAI(@RequestBody SessionID session) {
        System.out.println(session);
        pvAIGameMap.put(session.sessionID, new PvAIGame(session.sessionID));
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @PostMapping(value = "/game/PvAI/PlayerMove", produces = "application/json", consumes="application/json")
    public ResponseEntity<ResponsePackage> PvAIPlayerResponse(@RequestBody MovePackage movePackage) {
        PvAIGame game = pvAIGameMap.get(movePackage.sessionID);
        System.out.println(game);
        try {
            game.outToIn.put(new Move(5 - movePackage.y, movePackage.x));
            var response = game.inToOut.take();
            return new ResponseEntity<>(new ResponsePackage(true, response.winner), HttpStatus.OK);
        } catch (InterruptedException e) {
            System.out.println("it it failed");
            return new ResponseEntity<>(new ResponsePackage(false, -1), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/game/PvAI/CPUMove", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MoveAndGameStatus> PvAICPUResponse(@RequestBody MovePackage movePackage) {
        PvAIGame game = pvAIGameMap.get(movePackage.sessionID);
        System.out.println(game);
        try {
            MoveAndGameStatus moveAndGameStatus = game.inToOut.peek();
            if (moveAndGameStatus == null) {
                return new ResponseEntity<>(new MoveAndGameStatus(-1, -1, -1), HttpStatus.OK);
            }
            moveAndGameStatus = game.inToOut.take();
            return new ResponseEntity<>(new MoveAndGameStatus(moveAndGameStatus.y, moveAndGameStatus.x, moveAndGameStatus.winner), HttpStatus.OK);
        } catch(InterruptedException e) {
            System.out.println("it it failed");
            return new ResponseEntity<>(new MoveAndGameStatus(-1, -1, -1), HttpStatus.BAD_REQUEST);
        }
    }


    public static class SessionID {
        public String sessionID;
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
        public int x;
        public int y;
        public String sessionID;
    }

    public static class MoveAndGameStatus {
        public int x;
        public int y;
        public int winner;
        MoveAndGameStatus(int x, int y, int winner) {
            this.x = x;
            this.y = y;
            this.winner = winner;
        }
    }

    public static class User {
        public String userId;
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
                while(!game.getGameOver()) {
                    Move move = new Move(0, 0);
                    if(p2p || activePlayer==0) //HUMAN PLAYER STEP
                    {
                        try {
                            move =  this.outToIn.take();
                        } catch (InterruptedException e) {
                        }
                        game.step(move, game.activePlayer); 
                        game.board.printBoard();
                        game.activePlayer = (game.activePlayer+1) % 2;
                    }
                    else if(!p2p && game.activePlayer==1) { 
                        game.step(game.activePlayer);
                        game.board.printBoard();
                        game.activePlayer = (game.activePlayer+1) % 2;
                    }
                    
                    if (game.getGameOver() == false) {
                        try {this.inToOut.put(-1);}
                        catch(InterruptedException e) {
                        }
                    }
                };
                try {this.inToOut.put(game.getWinner());}
                catch(InterruptedException e) {}
            });
        }
    }

    public static class PvAIGame {
        public String sessionID;
        public Thread gameThread;
        public BlockingQueue<Move> outToIn;
        public BlockingQueue<MoveAndGameStatus> inToOut;
        public PvAIGame(String sessionID) {
            this.sessionID = sessionID;
            this.outToIn = new ArrayBlockingQueue<>(2);
            this.inToOut = new ArrayBlockingQueue<>(2);
            this.gameThread = Thread.startVirtualThread(() -> {
                boolean p2p = false;
                int cpuLevel = 1;
                Game game = new Game(p2p, cpuLevel); 
                while(!game.getGameOver()) {
                    Move move = new Move(0, 0);
                    if(p2p || game.activePlayer==0) //HUMAN PLAYER STEP
                    {
                        try {
                            move =  this.outToIn.take();
                        } catch (InterruptedException e) {
                        }
                        while(!game.checkLegalMove(game.board, move)) {
                            move = new Move(move.col - 1, move.row - 1); //YOUR MOVE ROW/COL GETTER METHOD GOES HERE 
                        }
                        game.step(move, game.activePlayer); 
                        game.activePlayer = (game.activePlayer+1) % 2;
                        if (game.getGameOver() == true) {
                            try {this.inToOut.put(new MoveAndGameStatus(-1, -1, game.getWinner()));}
                            catch(InterruptedException e) {
                            }
                        }
                        if (game.getGameOver() == false) {
                            try {this.inToOut.put(new MoveAndGameStatus(-1, -1, -1));}
                            catch(InterruptedException e) {
                            }
                        }
                    }
                    else if(!p2p && game.activePlayer==1) { //COMPUTER PLAYER STEP, it will decide move automatically
                        Move computerMove = game.step(game.activePlayer);
                        game.activePlayer = (game.activePlayer+1) % 2;
                        if (game.getGameOver() == false) {
                            try {this.inToOut.put(new MoveAndGameStatus(computerMove.row, computerMove.col, -1));}
                            catch(InterruptedException e) {
                            }
                        }
                        if (game.getGameOver()) {
                            System.out.println("returning col: " + computerMove.col + "recturning row: " + computerMove.row);
                            try {this.inToOut.put(new MoveAndGameStatus(computerMove.row, computerMove.col, game.getWinner()));}
                            catch(InterruptedException e) {
                            }
                        }
                        game.board.printBoard();
                    }
                    
                };
            });
        }
    }
}

