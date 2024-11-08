export type move = {
    x: number,
    y: number,
}

export type LocalPvPGame = {
    gameId: string | null;
    playerId: string | null;
}

export type Game = {
  gameId: string | null;
  playerId: string | null;
  gameMode: string | null;
}

export function newGame(userId: string) {
  return {
    gameId: null,
    playerId: userId,
    gameMode: null,
  }
}

export function newLocalPvPGame(playerid: string): LocalPvPGame {
    return  {
        gameId: null,
        playerId: playerid,
    }
}
