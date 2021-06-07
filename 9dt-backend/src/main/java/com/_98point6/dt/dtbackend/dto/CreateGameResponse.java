package com._98point6.dt.dtbackend.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CreateGameResponse {
    private String gameId;

    public CreateGameResponse() {}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("gameId", gameId)
                .toString();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
