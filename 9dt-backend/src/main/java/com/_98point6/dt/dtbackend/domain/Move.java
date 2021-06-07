package com._98point6.dt.dtbackend.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Move {

    @Id
    @GeneratedValue
    private Integer id;

    private String type;

    private Integer column;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Player player;

    public Move(String type, Integer column, Game game, Player player) {
        this.type = type;
        this.column = column;
        this.game = game;
        this.player = player;
    }

    protected  Move() {
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Integer getColumn() {
        return column;
    }

    public Player getPlayer() {
        return player;
    }
}
