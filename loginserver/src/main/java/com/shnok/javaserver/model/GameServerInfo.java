package com.shnok.javaserver.model;

import com.shnok.javaserver.enums.ServerStatus;
import com.shnok.javaserver.thread.GameServerThread;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

@Getter
@Setter
public class GameServerInfo {
    // auth
    private int id;
    private final byte[] hexId;
    private boolean isAuthed;
    // status
    private GameServerThread gameServerThread;
    private int status;
    // network
    private final ArrayList<GameServerAddress> addresses = new ArrayList<>(5);
    private int port;
    // config
    private final boolean isPvp = true;
    private int serverType;
    private int ageLimit;
    private boolean isShowingBrackets;
    private int maxPlayers;

    /**
     * Instantiates a new game server info.
     * @param id the id
     * @param hexId the hex id
     * @param gameServerThread the gst
     */
    public GameServerInfo(int id, byte[] hexId, GameServerThread gameServerThread) {
        this.id = id;
        this.hexId = hexId;
        this.gameServerThread = gameServerThread;
        status = ServerStatus.STATUS_DOWN.getCode();
    }

    /**
     * Instantiates a new game server info.
     * @param id the id
     * @param hexId the hex id
     */
    public GameServerInfo(int id, byte[] hexId) {
        this(id, hexId, null);
    }

    public String getName() {
        // TODO: Custom name
        return "Teon";
        //return ServerNameDAO.getServer(id);
    }

    public String getStatusName() {
         switch (status) {
            case 0: return "Auto";
            case 1: return "Good";
            case 2: return "Normal";
            case 3: return "Full";
            case 4: return "Down";
            case 5: return "GM Only";
            default: return "Unknown";
        }
    }

    /**
     * Gets the current player count.
     * @return the current player count
     */
    public int getCurrentPlayerCount() {
        if (gameServerThread == null) {
            return 0;
        }
        return gameServerThread.getPlayerCount();
    }

    /**
     * Gets the external host.
     * @return the external host
     */
    public String getExternalHost() {
        try {
            return getServerAddress(InetAddress.getByName("0.0.0.0"));
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Sets the down.
     */
    public void setDown() {
        setAuthed(false);
        setPort(0);
        setGameServerThread(null);
        setStatus(ServerStatus.STATUS_DOWN.getCode());
    }

    /**
     * Adds the server address.
     * @param subnet the subnet
     * @param addr the addr
     * @throws UnknownHostException the unknown host exception
     */
    public void addServerAddress(String subnet, String addr) throws UnknownHostException {
        addresses.add(new GameServerAddress(subnet, addr));
    }

    /**
     * Gets the server address.
     * @param addr the addr
     * @return the server address
     */
    public String getServerAddress(InetAddress addr) {
        for (GameServerAddress a : addresses) {
            if (a.equals(addr)) {
                return a.getServerAddress();
            }
        }
        return null; // should not happen
    }

    /**
     * Gets the server addresses.
     * @return the server addresses
     */
    public String[] getServerAddresses() {
        String[] result = new String[addresses.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = addresses.get(i).toString();
        }

        return result;
    }

    /**
     * Clear server addresses.
     */
    public void clearServerAddresses() {
        addresses.clear();
    }
}
