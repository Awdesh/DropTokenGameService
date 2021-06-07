package com._98point6.dt.dtbackend.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PostMoveResponse {
    private String moveLink;

    public PostMoveResponse() {}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("moveLink", moveLink)
                .toString();
    }

    public String getMoveLink() {
        return moveLink;
    }

    public void setMoveLink(String moveLink) {
        this.moveLink = moveLink;
    }
}
