package com._98point6.dt.dtbackend.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PostMoveRequest {
    private Integer column;

    public PostMoveRequest() {}

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("column", column)
                .toString();
    }
}
