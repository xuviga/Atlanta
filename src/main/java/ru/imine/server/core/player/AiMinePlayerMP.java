package ru.imine.server.core.player;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import ru.imine.server.core.AiMineCore;
import ru.imine.server.core.database.MySQL;
import ru.imine.server.core.database.Row;
import ru.imine.server.util.PermsUtil;
import ru.imine.shared.AiMine;
import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.core.player.PlayerRank;
import ru.imine.shared.core.player.PlayerRole;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.FancyLine;
import ru.imine.shared.fancychat.packet.FCPacket0ChatMessage;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.Location;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AiMinePlayerMP
{
    private static String tablename;
    private static final Map<Long, AiMinePlayerMP> players = new ConcurrentHashMap<>();

    protected long playerId;
    protected UUID uuid;
    protected String name;
    protected UUID hwUuid;
    protected UUID clientSeed;

    protected PlayerRank playerRank;
    protected PlayerRole playerRole;
    int nickColor;
    protected int chatColor;

    protected EntityPlayerMP entity;
    protected PlayerStorageProxy storage;

    static
    {
        try
        {
            tablename = AiMineCore.getServerName();
            MySQL.getGameDataSQL().updateSQL("CREATE TABLE IF NOT EXISTS `"+tablename+"` (" +
                    "  `username` bigint(20) unsigned NOT NULL," +
                    "  `balance_real` bigint(20) NOT NULL DEFAULT '0'," +
                    "  `NickColor` varchar(11) NULL," +
                    "  `ChatColor` varchar(11) NULL," +
                    "  PRIMARY KEY (`username`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Failed to create table to store playerdata. TableName=`" + tablename + "`. Shutdown!", e);
            Discord.instance.sendErrorLog("iMineCore", "Failed to create table to store playerdata. TableName=`" + tablename + "`. Shutdown!", e);

            FMLCommonHandler.instance().exitJava(1, false);
        }
    }

    @Deprecated
    public static AiMinePlayerMP addFromHere(PlayerEvent.PlayerLoggedInEvent event)
    {
        // Create a new AiMinePlayerMP object
        AiMinePlayerMP player = new AiMinePlayerMP();
        // Set the UUID of the player to the UUID of the logged-in player
        player.uuid = event.player.getUniqueID();
        // If player authentication is successfully loaded from the database
        if (player.loadPlayerAuth((Connection) player))
        {
            // Set the entity of the player to the logged-in player
            player.entity = (EntityPlayerMP) event.player;
            // Add the player to the thread-safe players map with their playerId as the key
            players.put(player.playerId, player);
            // Return the added player object
            return player;
        }
        // If player authentication could not be loaded, return null
        return null;
    }

    @Deprecated
    public static void removeFromHere(PlayerEvent.PlayerLoggedOutEvent event)
    {
        AiMinePlayerMP player = AiMinePlayerMP.get(event.player);
        if (player != null)
            players.remove(player.playerId);
    }

    public static AiMinePlayerMP get(ICommandSender sender)
    {
        return get((EntityPlayer) sender);
    }

    public static AiMinePlayerMP get(EntityPlayer player)
    {
        return players.values().stream().filter(it -> it.entity.equals(player)).findFirst().orElse(null);
    }

    public static AiMinePlayerMP get(long playerId)
    {
        return players.get(playerId);
    }

    public static AiMinePlayerMP get(UUID uuid)
    {
        return players.values().stream().filter(it -> it.uuid.equals(uuid)).findFirst().orElse(null);
    }

    public static AiMinePlayerMP get(String username)
    {
        return players.values().stream().filter(it -> it.name.equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    public static AiMinePlayerMP getOffline(String name) {
        AiMinePlayerMP player = get(name);
        if (player != null) {
            return player;
        }
        player = new AiMinePlayerMP();
        player.name = name;
        if (player.loadPlayerAuth((Connection) player)) {
            // Player data loaded successfully, return the new player object
            players.put(player.playerId, player); // Make sure to add the new player object to the thread-safe players map
            return player;
        }
        return null;
    }

    /**
     * Returns an offline player with the specified player ID. If the player is already loaded, returns the loaded player,
     * otherwise creates a new player object and loads the player data from the database.
     *
     * @param playerId the ID of the player to retrieve
     * @return an AiMinePlayerMP object representing the offline player, or null if the player was not found
     */
    public static AiMinePlayerMP getOffline(long playerId)
    {
        AiMinePlayerMP player = get(playerId);
        if (player != null) {
            // Player is already loaded, return the existing player object
            return player;
        }
        // Player is not loaded, create a new player object and load the player data from the database
        player = new AiMinePlayerMP();
        player.playerId = playerId;
        if (player.loadPlayerAuth((Connection) player)) {
            // Player data loaded successfully, return the new player object
            players.put(player.playerId, player); // Make sure to add the new player object to the thread-safe players map
            return player;
        }
        // Player data not found in the database, return null
        return null;
    }

    public static UUID getUUID(long playerId) {
        try {
            List<Row> rows = MySQL.getGlobalDataSQL().querySQL("SELECT `uuid` FROM `users` WHERE `id`=?", playerId);
            if (rows.isEmpty()) {
                throw new IllegalArgumentException("Player #" + playerId + " not found.");
            }
            return UUID.fromString(rows.get(0).getString(1));
        } catch (SQLException e) {
            String errorMessage = "Failed to get UUID for player #" + playerId;
            AiMine.LOGGER.error(errorMessage, e);
            Discord.instance.sendErrorLog("iMineCore", errorMessage, e);
            throw new RuntimeException(e);
        }
    }

    public static List<AiMinePlayerMP> getPlayers()
    {
        return new ArrayList<>(players.values());
    }

    public static List<AiMineUser> getUsers(AiMinePlayerMP viewer)
    {
        return getPlayers().stream().map(it->it.asUser(viewer)).collect(Collectors.toList());
    }

    private AiMinePlayerMP()
    {
    }

    protected boolean loadPlayerAuth(Connection connection) {
        try {
            // Create a list to store the rows returned by the query
            List<Row> rows = new ArrayList<>();

            // Execute the appropriate query based on the available information
            if (playerId != 0) {
                rows = MySQL.getGlobalDataSQL().querySQL(String.valueOf(connection), "SELECT * FROM `users` WHERE `id`=?", playerId);
            } else if (uuid != null) {
                rows = MySQL.getGlobalDataSQL().querySQL(String.valueOf(connection), "SELECT * FROM `users` WHERE `uuid`=?", uuid.toString());
            } else if (!name.isEmpty()) {
                rows = MySQL.getGlobalDataSQL().querySQL(String.valueOf(connection), "SELECT * FROM `users` WHERE `username`=?", name);
            }

            // If no rows were returned, the player could not be found
            if (rows.isEmpty()) {
                return false;
            }

            // Otherwise, populate the player object with the information from the row
            Row row = rows.get(0);
            playerId = row.getLong("id");
            uuid = UUID.fromString(row.getString("uuid"));
            name = row.getString("username");
            playerRank = PlayerRank.DEFAULT;
            playerRole = PlayerRole.DEFAULT;
            for (PlayerRank value : PlayerRank.values()) {
                if (PermsUtil.inheritsGroup(playerId, value.name)) {
                    playerRank = value;
                }
            }
            for (PlayerRole value : PlayerRole.values()) {
                if (PermsUtil.inheritsGroup(playerId, value.name)) {
                    playerRole = value;
                }
            }
            storage = new PlayerStorageProxy(playerId);
            return true;
        } catch (SQLException e) {
            AiMine.LOGGER.error("Ошибка при загрузке данных игрока", e);
            Discord.instance.sendErrorLog("iMineCore", "Ошибка при загрузке данных игрока", e);
        } catch (Exception e) {
            AiMine.LOGGER.error("Ошибка при загрузке данных игрока", e);
            Discord.instance.sendErrorLog("iMineCore", "Ошибка при загрузке данных игрока", e);
        }

        // If there was an exception, return false to indicate that the player could not be loaded
        return false;
    }


    public boolean hasRank(PlayerRank rank)
    {
        return playerRank.level >= rank.level;
    }

    public boolean hasRole(PlayerRole role)
    {
        return playerRole.level >= role.level;
    }

    public ITextComponent getDisplayName()
    {
        return entity!=null ? entity.getDisplayName() : new TextComponentString(getName());
    }

    public boolean isOnline()
    {
        return entity != null;
    }

    public void sendMessage(String line)
    {
        FancyChat.network.sendTo(new FCPacket0ChatMessage(FancyChat.stringToChatLine(line)), getEntity());
    }

    public void sendMessage(ITextComponent component)
    {
        FancyChat.network.sendTo(new FCPacket0ChatMessage(FancyChat.componentToChatLine(component)), getEntity());
    }

    public void sendMessage(FancyLine line)
    {
        FancyChat.network.sendTo(new FCPacket0ChatMessage(FancyChat.lineToChatLine(line)), getEntity());
    }

    public void sendMessage(FancyChatLine line)
    {
        FancyChat.network.sendTo(new FCPacket0ChatMessage(line), getEntity());
    }

    public long getPlayerId()
    {
        return playerId;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public PlayerRank getPlayerRank()
    {
        return playerRank;
    }

    public PlayerRole getPlayerRole()
    {
        return playerRole;
    }

    public EntityPlayerMP getEntity()
    {
        return entity;
    }

    public Location getLocation()
    {
        return new Location(entity);
    }

    public GameType getGameMode()
    {
        return entity.interactionManager.getGameType();
    }

    public UUID getClientSeed()
    {
        return clientSeed;
    }

    public PlayerStorageProxy getStorage()
    {
        return storage;
    }

    public long getMoney()
    {
        try
        {
            for (Row row : MySQL.getGameDataSQL().querySQL("SELECT `balance_real` FROM `users` WHERE username=?", name))
            {
                return row.getLong(1);
            }
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Failed to get " + name + "'s money from table. TableName " + tablename, e);
            Discord.instance.sendErrorLog("1", "Failed to get " + name + "'s money from table. TableName " + tablename, e);

            if (entity!=null)
                sendMessage("§cПроизошел сбой в работе системы монет. Повтори операцию позже.");
        }
        return 0;
    }

    public void changeMoney(long amount) throws SQLException
    {
        MySQL.getGameDataSQL().updateSQL("INSERT IGNORE INTO `users` (`username`, `balance_real`)" +
                "VALUES (?, ?) ON DUPLICATE KEY UPDATE `balance_real` = `balance_real` + ?;", name, amount, amount);
    }

    public int getChatColor()
    {
        try
        {
            for (Row row : MySQL.getGameDataSQL().querySQL("SELECT `ChatColor` FROM `users` WHERE username=?", name))
            {
                Integer value = row.getInteger(15);
                if (value==null)
                    return 0xFFFFFFFF;
            }
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Failed to get player's chat color from table. TableName=`users` username="+name, e);
            Discord.instance.sendErrorLog("2", "Failed to get player's chat color from table. TableName=`users` username="+name, e);
        }
        return 0xFFFFFFFF;
    }

    public int getNickColor()
    {
        try
        {
            for (Row row : MySQL.getGameDataSQL().querySQL("SELECT `NickColor` FROM `users` WHERE username=?", name))
            {
                Integer value = row.getInteger(16);
                if (value==null)
                    return 0xFFFFFFFF;
            }
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Failed to get player's nick color from table. TableName=`users` username="+name, e);
            Discord.instance.sendErrorLog("3", "Failed to get player's nick color from table. TableName=`users` username="+name, e);
        }
        return 0xFFFFFFFF;
    }

    public void setChatColor(int color) throws SQLException
    {
        MySQL.getGameDataSQL().updateSQL("INSERT IGNORE INTO `users` (`username`, `ChatColor`)" +
                "VALUES (?, ?) ON DUPLICATE KEY UPDATE `ChatColor` = `ChatColor` + ?;", name, color, color);
    }

    public void setNickColor(int color) throws SQLException
    {
        MySQL.getGameDataSQL().updateSQL("INSERT IGNORE INTO `users` (`username`, `NickColor`)" +
                "VALUES (?, ?) ON DUPLICATE KEY UPDATE `NickColor` = `NickColor` + ?;", name, color, color);
    }

    @Override
    public String toString()
    {
        return "AiMinePlayerMP={" + name + " #" + playerId + " "+uuid+" }";
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof AiMinePlayerMP && playerId == ((AiMinePlayerMP) other).playerId;
    }

    @Override
    public int hashCode()
    {
        return 42+Long.hashCode(playerId);
    }

    public AiMineUser asUser(AiMinePlayerMP viewer)
    {
        return new AiMineUser(viewer,this);
    }


}
