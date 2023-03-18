package ru.imine.server.core.player;

import ru.imine.server.core.database.Storage;

public class PlayerStorageProxy
{
    private long playerId;
    public PlayerStorageProxy(long playerId)
    {
        this.playerId = playerId;
    }

    public long getLastLogin()
    {
        return Storage.getStorage("player").getLong(Long.toString(playerId),"last_login",0);
    }

    public long getLastLogout()
    {
        return Storage.getStorage("player").getLong(Long.toString(playerId),"last_logout",0);
    }
}
