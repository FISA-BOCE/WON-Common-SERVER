package com.woorifisa.won_common_server.domain.mapping.model.enums;

public enum LinkStatus {
    NONE, LINKED, UNLINKED;

    public boolean isConnected() {
        return this == LINKED;
    }
}
