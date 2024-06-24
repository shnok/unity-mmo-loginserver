package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBAccountInfo;

import java.net.InetAddress;

public interface AccountInfoDao {
    public DBAccountInfo getAccountInfo(String login);
    public void createAccount(DBAccountInfo accountInfo);
}
