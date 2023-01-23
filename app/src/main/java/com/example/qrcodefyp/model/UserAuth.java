package com.example.qrcodefyp.model;

public class UserAuth {
    private boolean isLogin=false;
    private boolean isRemember =false;

    public UserAuth(boolean isLogin, boolean isRemember) {
        this.isLogin = isLogin;
        this.isRemember = isRemember;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isRemember() {
        return isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }
}
