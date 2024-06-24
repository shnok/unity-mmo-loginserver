package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBAccountInfo;
import com.shnok.javaserver.db.repository.AccountInfoRepository;

public class AccountInfoTableService {
    private final AccountInfoRepository accountInfoRepository;
    private static AccountInfoTableService instance;
    public static AccountInfoTableService getInstance() {
        if (instance == null) {
            instance = new AccountInfoTableService();
        }
        return instance;
    }

    public AccountInfoTableService() {
        accountInfoRepository = new AccountInfoRepository();
    }

    public DBAccountInfo getAccountInfo(String login) {
        return accountInfoRepository.getAccountInfo(login);
    }

    public void createAccount(DBAccountInfo accountInfo) {
        accountInfoRepository.createAccount(accountInfo);
    }
}
