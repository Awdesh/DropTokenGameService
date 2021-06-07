package com._98point6.dt.dtbackend.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Integer id;

    private String playerId;

    private boolean winner;

    @ManyToOne
    private Game game;

    public Player(String playerId, Game game) {
        this.playerId = playerId;
        this.game = game;
    }

    protected Player() {
    }

    public Integer getId() {
        return id;
    }

    public String getPlayerId() {
        return playerId;
    }


    public void setPlayerId(String playerName) {
        this.playerId = playerName;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean isWinner() {
        return winner;
    }
}
