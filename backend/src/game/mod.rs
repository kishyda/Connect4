use rand::Rng;

use crate::Result;

enum Player {
    Player1,
    Player2,
    Bot,
}

impl Player {
    pub fn piece(&self) -> char {
        match *self {
            Player::Player1 => 'x',
            Player::Player2 => 'o',
            Player::Bot => 'o',
        }
    }
    pub fn bot_opponent(&self) -> Player {
        match *self {
            Player::Player1 => Player::Bot,
            Player::Bot => Player::Player1,
            Player::Player2 => Player::Bot,
        }
    }
    pub fn opponent(&self) -> Player {
        match *self {
            Player::Player1 => Player::Player2,
            Player::Bot => Player::Player1,
            Player::Player2 => Player::Player1,
        }
    }
    pub fn alternate_opponent_bot(&mut self) -> Player {
        match *self {
            Player::Player1 => {
                *self = Player::Bot;
                return Player::Bot;
            }
            Player::Bot => {
                *self = Player::Player1;
                return Player::Player1;
            }
            Player::Player2 => {
                *self = Player::Bot;
                return Player::Bot;
            }
        }
    }
}

impl Game {
    pub fn print_board(&self) {
        for i in self.board.iter().rev() {
            for j in i.iter() {
                print!("{j} ");
            }
            println!();
        }
    }

    fn check_horizontal(&self, row: usize, col: usize) -> bool {
        let (mut left, mut right) = (col, col);
        let token = self.board[row][col];
        let mut length = 1;

        while left > 0 && self.board[row][left - 1] == token {
            length += 1;
            left -= 1;
        }

        while right + 1 < 7 && self.board[row][right + 1] == token {
            length += 1;
            right += 1;
        }
        length >= 4
    }

    fn check_diagonal(&self, row: usize, col: usize) -> bool {
        let (mut rt, mut lt, mut rb, mut lb) = ((row, col), (row, col), (row, col), (row, col));
        let token = self.board[row][col];
        let mut posxlength = 1;
        let mut negxlength = 1;
        while rt.0 + 1 < 6 && rt.1 + 1 < 7 && self.board[rt.0 + 1][rt.1 + 1] == token {
            rt = (rt.0 + 1, rt.1 + 1);
            posxlength += 1;
        }
        while lb.0 as i8 - 1 >= 0 && lb.1 as i8 - 1 >= 0 && self.board[lb.0 - 1][lb.1 - 1] == token
        {
            lb = (lb.0 - 1, lb.1 - 1);
            posxlength += 1;
        }
        while lt.0 + 1 < 6 && lt.1 as i8 - 1 >= 0 && self.board[lt.0 + 1][lt.1 - 1] == token {
            lt = (lt.0 + 1, lt.1 - 1);
            negxlength += 1;
        }
        while rb.0 as i8 - 1 >= 0 && rb.1 + 1 < 7 && self.board[rb.0 - 1][rb.1 + 1] == token {
            rb = (rb.0 - 1, rb.1 + 1);
            negxlength += 1;
        }
        posxlength >= 4 || negxlength >= 4
    }

    fn check_vertical(&self, row: usize, col: usize) -> bool {
        let (mut top, mut bottom) = (row, row);
        let mut length = 1;
        let token = self.board[row][col];

        while bottom as i8 - 1 >= 0 && self.board[bottom - 1][col] == token {
            length += 1;
            bottom -= 1;
        }

        while top + 1 < 6 && self.board[top + 1][col] == token {
            length += 1;
            top += 1;
        }
        length >= 4
    }

    pub fn place_piece(&mut self, player: &Player, col: usize) -> i32 {
        let mut score = 0;
        let mut row = 99;

        let mut index: usize = 99;
        for i in 0..6 {
            if self.board[i][col] == '_' {
                index = i;
                row = i;
                break;
            }
        }
        if index == 99 || row == 99 {
            return 0;
        }
        self.board[index][col] = player.piece();
        if self.check_diagonal(index, col)
            || self.check_horizontal(index, col)
            || self.check_vertical(index, col)
        {
            return 1000;
        }

        for left in (0..4).rev() {
            if col as i8 - left as i8 >= 0 && self.board[row][col - left] == player.piece() {
                score += 1;
            }
        }
        for right in 0..4 {
            if col + right < 7 && self.board[row][col + right] == player.piece() {
                score += 1;
            }
        }
        for top in 0..4 {
            if row + top < 6 && self.board[row + top][col] == player.piece() {
                score += 1;
            }
        }
        for bottom in 0..4 {
            if row as i8 - bottom as i8 >= 0 && self.board[row - bottom][col] == player.piece() {
                score += 1;
            }
        }
        for topright in 0..4 {
            if row + topright < 6
                && col + topright < 6
                && self.board[row + topright][col + topright] == player.piece()
            {
                score += 1;
            }
        }
        for bottomright in 0..4 {
            if row as i8 - bottomright as i8 >= 0
                && col + bottomright < 6
                && self.board[row - bottomright][col + bottomright] == player.piece()
            {
                score += 1;
            }
        }
        for topleft in 0..4 {
            if row + topleft < 6
                && col as i8 - topleft as i8 >= 0
                && self.board[row + topleft][col - topleft] == player.piece()
            {
                score += 1;
            }
        }
        for bottomleft in 0..4 {
            if row as i8 - bottomleft as i8 >= 0
                && col as i8 - bottomleft as i8 >= 0
                && self.board[row - bottomleft][col - bottomleft] == player.piece()
            {
                score += 1;
            }
        }
        score
    }
    fn easy_bot_move(&mut self, col: usize) {
        let rand = rand::thread_rng().gen_range(0..7);
        self.place_piece(&Player::Bot, col);
    }

    pub fn hard_bot_move(&mut self) -> i32 {
        let mut best_move = 0;
        let mut best_value = i32::MIN;

        for i in 0..6 {
            let mut board_copy = self.copy();
            if board_copy.place_piece(&Player::Player1, i) >= 1000 {
                println!("blockign player win");
                return self.place_piece(&Player::Bot, i);
            }
        }

        'outer: for i in 0..6 {
            let mut score: i32 = 0;
            for j in 0..6 {
                for k in 0..6 {
                    for l in 0..6 {
                        for m in 0..6 {
                            let mut board_copy = self.copy();
                            let result = board_copy.place_piece(&Player::Bot, i);
                            if result >= 1000 {
                                println!("detected win");
                                return self.place_piece(&Player::Bot, i)
                            }
                            score += result;
                            let result = board_copy.place_piece(&Player::Player1, j);
                            if result >= 1000 { 
                                println!("detected incoming loss if played");
                                continue 'outer;
                            }
                            score -= result;
                            score += board_copy.place_piece(&Player::Bot, k);
                            score -= board_copy.place_piece(&Player::Player1, l);
                            score += board_copy.place_piece(&Player::Bot, m);
                            if best_value < score.into() {
                                best_move = i;
                                best_value = score.into();
                            }
                        }
                    }
                }
            }
        }
        self.place_piece(&Player::Bot, best_move)
    }

    fn bot_move() {}

    pub fn copy(&self) -> Game {
        Game {
            board: self.board,
            sessionid: String::new(),
            gamemode: String::new(),
            userid1: String::new(),
            userid2: String::new(),
        }
    }

    pub fn new(gamemode: String, sessionid: String, userid1: String, userid2: String) -> Game {
        Game {
            board: [['_'; 7]; 6],
            gamemode,
            sessionid,
            userid1,
            userid2,
        }
    }

    pub fn testing() {
        let mut game = Game::new(
            "something".to_string(),
            "something".to_string(),
            "somethign".to_string(),
            "something".to_string(),
        );
        let player = Player::Player1;
        loop {
            let mut row;
            let mut col;
            let mut input = String::new();

            print!("Enter row: ");
            std::io::stdin().read_line(&mut input).unwrap();
            row = input.trim().parse::<usize>().unwrap();

            print!("Enter col: ");
            input = String::new();
            std::io::stdin().read_line(&mut input).unwrap();
            col = input.trim().parse::<usize>().unwrap();

            let score = game.place_piece(&player, col);
            if score >= 1000 {
                game.print_board();
                println!("you win");
                break;
            } else {
                println!("payer score: {score}");
            };

            game.print_board();

            let score = game.hard_bot_move();
            if score >= 1000 {
                game.print_board();
                println!("bot wins");
                break;
            } else {
                println!("bot score: {score}");
            };

            game.print_board();
        }
    }
}

pub struct Game {
    board: [[char; 7]; 6],
    sessionid: String,
    gamemode: String,
    userid1: String,
    userid2: String,
}
