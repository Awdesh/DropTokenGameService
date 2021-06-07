package com._98point6.dt.dtbackend.dto;

public class GetMoveResponse {
    private String type;
    private String player;
    private Integer column;

    public GetMoveResponse(String type, Integer column, String player) {
        this.type = type;
        this.column = column;
        this.player = player;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }
}
