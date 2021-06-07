package com._98point6.dt.dtbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class GameStatusResponse {
    private List<String> players;
    private String winner;
    private String state;

    public GameStatusResponse() {}

    public List<String> getPlayers() {
        return players;
    }

    public String getWinner() {
        return winner;
    }

    public String getState() {
        return state;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setState(String state) {
        this.state = state;
    }
}
