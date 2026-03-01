package com.note_core.security;

public final class PreAuth {

    private PreAuth() {
    }

    // Permissions
    public static final String USER_READ = "hasAuthority('USER_READ')";
    public static final String USER_WRITE = "hasAuthority('USER_WRITE')";
    public static final String ADMIN_IMPERSONATE = "hasAuthority('ADMIN_IMPERSONATE')";
    public static final String PLAN_READ = "hasAuthority('PLAN_READ')";
    public static final String PLAN_WRITE = "hasAuthority('PLAN_WRITE')";
    public static final String PLAN_ASSIGN = "hasAuthority('PLAN_ASSIGN')";

    // Roles
    public static final String IS_ADMIN = "hasAnyRole('ADMIN', 'SUPER_ADMIN')";
    public static final String IS_SUPER_ADMIN = "hasRole('SUPER_ADMIN')";
}
