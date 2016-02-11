package com.msfdroid.rpc;

public class LoginException extends RpcException {
    public static final String LOGIN_FAILED = "Login Failed";

    public LoginException() {
        super(LOGIN_FAILED);
    }
}
