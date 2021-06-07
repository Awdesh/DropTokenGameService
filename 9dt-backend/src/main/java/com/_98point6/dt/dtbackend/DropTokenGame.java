package com._98point6.dt.dtbackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
A HashMap is maintained to keep track of board for each gameId.
gameId -> board

Another HashMap is maintained to keep track of player's turn in the game.
playerId -> false/true
 */

@Component
public class DropTokenGame {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenGame.class);

    boolean won;
    int[][] board;
    List<Integer> columns;
    boolean playerOne;
    Map<String, int[][]> map;
    Map<String, Boolean> playerMap;

    public DropTokenGame(){
        this.map = new HashMap<>();
        this.playerMap = new HashMap<>();
        columns = new ArrayList<>();
        playerOne = false;
        won = false;
    }

    public void setUp(String gameId, int rows, int columns) {
        board = new int[rows][columns];
        map.put(gameId, board);
    }

    public boolean isPlayerTurn(String playerId) {
        if(!playerMap.containsKey(playerId)) {
            playerMap.put(playerId, playerOne);
        } else if (playerMap.get(playerId)) {
            logger.info("Not the player's turn");
            return false;
        }
        return true;
    }

    public GameState put(int column, String gameId, String playerId) {
        if(!map.containsKey(gameId)) {
            map.put(gameId, board);
        } else {
            board = map.get(gameId);
        }

        int len = board.length;

        if (len < column - 1 || board[column-1][0] != 0 || isBoardFull()) {
            logger.error("Invalid input");
            return GameState.FULL;
        } else {
            int[] columnToPut = board[column - 1];
            // playerOne puts token '1' and playerTwo puts token '2'.
            int valueToPut = playerOne ? 1 : 2;
            // We put tokens at the bottom first. Hence, start the iteration from last index.
            for (int i = columnToPut.length - 1; i >= 0; i--) {
                if (columnToPut[i] == 0) {
                    columnToPut[i] = valueToPut;
                    columns.add(column);
                    break;
                }
            }
            // Determine the turn of next player.
            playerOne = !playerOne;
            playerMap.put(playerId, true); // 3 -> true, 4-> true
            for (Map.Entry<String, Boolean> entry : playerMap.entrySet()) {
                if(entry.getKey().equals(playerId)) {
                    continue;
                }
                // set other players to false for next turn.
                playerMap.put(entry.getKey(), false);
            }
            if (checkForWin()) {
                logger.info(GameState.WIN.toString());
                return GameState.WIN;
            }
            if (isBoardFull()) {
                logger.info(GameState.DRAW.toString());
                return GameState.FULL;
            }
            logger.info("OK");
        }

        return GameState.PROGRESS;
    }

    /**
     *
     * @return
     */
    public boolean isBoardFull() {
        for (int i = 0; i < board.length ; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks the board to see if a player has won
     *
     * @return true if a player has won, false otherwise
     */
    public boolean checkForWin() {
        boolean rows = checkRows(true, true);
        boolean columns = checkColumns(true, true);
        boolean diagonals = checkDiagonals(true, true);
        return rows || columns || diagonals;
    }

    /**
     * Checks the rows of the board to see if a player has one
     *
     * @param  ones  a tracker for whether the first player has won
     * @param  twos  a tracker for whether the second player has won
     * @return  true if a player has won, false otherwise
     */
    private boolean checkRows(boolean ones, boolean twos) {
        for (int i = 0; i < board.length; i++) {
            int[] column = board[i];
            for (int j = 0; j < column.length; j++) {
                if (column[j] != 1) {
                    ones = false;
                }
                if (column[j] != 2) {
                    twos = false;
                }
            }
            if (ones || twos) {
                won = true;
                return true;
            } else {
                ones = true;
                twos = true;
            }
        }
        return false;
    }

    /**
     * Checks the columns of the board to see if a player has one
     *
     * @param  ones  a tracker for whether the first player has won
     * @param  twos  a tracker for whether the second player has won
     * @return  true if a player has won, false otherwise
     */
    private boolean checkColumns(boolean ones, boolean twos) {
        for (int i = 0; i < board.length; i++) {

            for (int j = 0; j < board.length; j++) {
                if (board[j][i] != 1) {
                    ones = false;
                }
                if (board[j][i] != 2) {
                    twos = false;
                }
            }

            if (ones || twos) {
                won = true;
                return true;
            } else {
                ones = true;
                twos = true;
            }
        }
        return false;
    }

    /**
     * Checks the diagonals on the board to see if a user has won
     *
     * @param  ones  a tracker for whether the first player has won
     * @param  twos  a tracker for whether the second player has won
     * @return  true if a player has won, false otherwise
     */
    private boolean checkDiagonals(boolean ones, boolean twos) {
        for (int i = 0; i < board.length; i++) {
            if (board[i][i] != 1) {
                ones = false;
            }
            if (board[i][i] != 2) {
                twos = false;
            }
        }
        if (ones || twos) {
            won = true;
            return true;
        }
        ones = true;
        twos = true;
        for (int i = 0; i < board.length; i++) {
            if (board[board.length - 1 - i][i] != 1) {
                ones = false;
            }
            if (board[board.length - 1 - i][i] != 2) {
                twos = false;
            }
        }
        won = ones || twos;
        return ones || twos;
    }
}
