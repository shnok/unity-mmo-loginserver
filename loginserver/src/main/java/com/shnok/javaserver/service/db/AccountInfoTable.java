package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBAccountInfo;
import com.shnok.javaserver.db.repository.AccountInfoRepository;

public class AccountInfoTable {
    private final AccountInfoRepository accountInfoRepository;
    private static AccountInfoTable instance;
    public static AccountInfoTable getInstance() {
        if (instance == null) {
            instance = new AccountInfoTable();
        }
        return instance;
    }

    public AccountInfoTable() {
        accountInfoRepository = new AccountInfoRepository();
    }

    public DBAccountInfo getAccountInfo(String login) {
        return accountInfoRepository.getAccountInfo(login);
    }

    public void createAccount(DBAccountInfo accountInfo) {
        accountInfoRepository.createAccount(accountInfo);
    }

    public void updateAccount(DBAccountInfo accountInfo) {
        accountInfoRepository.updateAccount(accountInfo);
    }
}
