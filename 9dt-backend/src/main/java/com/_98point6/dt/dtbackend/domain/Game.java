package com._98point6.dt.dtbackend.domain;

import com._98point6.dt.dtbackend.GameState;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Integer id;

    private String gameId;

    @JsonIgnore
    private Integer rows;

    @JsonIgnore
    private Integer columns;

    @JsonIgnore
    private String state;

    public Game(Integer rows, Integer columns) {
        this.rows = rows;
        this.columns = columns;
        this.state = GameState.PROGRESS.toString();
    }

    protected Game() {
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Integer getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
