package com.qpang.common.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    MASTER(Authority.MASTER),
    HUB_MANAGER(Authority.HUB_MANAGER),
    DELIVERY_MANAGER(Authority.DELIVERY_MANAGER),
    SUPPLIER_MANAGER(Authority.SUPPLIER_MANAGER);

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String MASTER = "MASTER";
        public static final String HUB_MANAGER = "HUB_MANAGER";
        public static final String DELIVERY_MANAGER = "DELIVERY_MANAGER";
        public static final String SUPPLIER_MANAGER = "SUPPLIER_MANAGER";
    }
}
