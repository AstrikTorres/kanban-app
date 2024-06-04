package com.astrik.kanban.security;

public class Constants {

	// Spring Security

	public static final String LOGIN_URL = "/login";
	public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
	public static final String TOKEN_BEARER_PREFIX = "Bearer ";

	// JWT

	public static final String SUPER_SECRET_KEY = "flavin refuel nurse piceous flogging silique";
	public static final long TOKEN_EXPIRATION_TIME = 1_728_000_000; // 20 day

}
