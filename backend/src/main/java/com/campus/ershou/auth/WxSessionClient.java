package com.campus.ershou.auth;

public interface WxSessionClient {

    String exchange(String code);
}
