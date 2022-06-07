package pers.learn.common.constant;

public class Auth {

    // 管理后台用户realm 名称
    public static final String BACKEND_USER = "admin";
    // 前台用户realm 名称
    public static final String USER = "api";

    // Jwt中claim存储realm名称的键
    public static final String JWT_ENDPOINT = "endpoint";
    public static final String JWT_USERNAME = "username";
    public static final String JWT_HEADER = "Authorization";
}
