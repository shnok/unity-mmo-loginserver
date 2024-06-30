package com.shnok.javaserver.model;

import static com.shnok.javaserver.config.Configuration.server;

public class SessionKey {
    public final int playOkID1;
    public final int playOkID2;
    public final int loginOkID1;
    public final int loginOkID2;

    public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2) {
        playOkID1 = playOK1;
        playOkID2 = playOK2;
        loginOkID1 = loginOK1;
        loginOkID2 = loginOK2;
    }

    @Override
    public String toString() {
        return "PlayOk: " + playOkID1 + " " + playOkID2 + " LoginOk:" + loginOkID1 + " " + loginOkID2;
    }

    public boolean checkLoginPair(int loginOk1, int loginOk2) {
        return (loginOkID1 == loginOk1) && (loginOkID2 == loginOk2);
    }

    /**
     * Only checks the PlayOk part of the session key if server doesn't show the license when player logs in.
     * @param other the other session key to validate
     * @return true if keys are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SessionKey)) {
            return false;
        }

        SessionKey key = (SessionKey) other;
        // when server doesn't show license it doesn't send the LoginOk packet, client doesn't have this part of the key then.
        if (server.showLicense()) {
            return ((playOkID1 == key.playOkID1) && (loginOkID1 == key.loginOkID1) && (playOkID2 == key.playOkID2) && (loginOkID2 == key.loginOkID2));
        }
        return ((playOkID1 == key.playOkID1) && (playOkID2 == key.playOkID2));
    }
}