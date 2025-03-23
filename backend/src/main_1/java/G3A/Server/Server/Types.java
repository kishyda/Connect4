package G3A.Server.Server;

public class Types {
    public static class GameStatus {
        public int winner;

        public GameStatus(int winner) {
            this.winner = winner;
        }
    }

    public static class OnlinePvPMovePackage {
        public int col;
        public int row;
        public String partyCode;
    }

    public static class SessionID2 {
        public String sessionID;
    }

    public static class PartyCode {
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
}
