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
                    "  PRIMARY KEY (`nickname`)" +
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
        AiMinePlayerMP player = new AiMinePlayerMP();
        player.uuid = event.player.getUniqueID();
        if (player.loadPlayerAuth())
        {
            player.entity = (EntityPlayerMP) event.player;
            players.put(player.playerId, player);
            return player;
        }
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

    public static AiMinePlayerMP getOffline(String name)
    {
        AiMinePlayerMP player = get(name);
        if (player!=null)
            return player;
        player = new AiMinePlayerMP();
        player.name = name;
        if (player.loadPlayerAuth())
            return player;
        return null;
    }

    public static AiMinePlayerMP getOffline(long playerId)
    {
        AiMinePlayerMP player = get(playerId);
        if (player!=null)
            return player;
        player = new AiMinePlayerMP();
        player.playerId = playerId;
        if (player.loadPlayerAuth())
            return player;
        return null;
    }

    public static UUID getUUID(long uuid)
    {
        try
        {
            List<Row> rows = new ArrayList<>();
            if (uuid != 0)
                rows = MySQL.getGlobalDataSQL().querySQL("SELECT `uuid` FROM `users` WHERE `id`=?", uuid);
            if (rows.size() == 0)
                throw new IllegalArgumentException("Игрок #" + uuid + " не найден.");
            return UUID.fromString(rows.get(0).getString(1));
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Мускуль сломался пока получали uuid для игрока #"+uuid, e);
            Discord.instance.sendErrorLog("iMineCore", "Мускуль сломался пока получали ууид для игрока #"+uuid, e);
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

    protected boolean loadPlayerAuth()
    {
        List<Row> rows = new ArrayList<>();
        try
        {
            if (playerId != 0)
                rows = MySQL.getGlobalDataSQL().querySQL("SELECT * FROM `users` WHERE `id`=?", playerId);
            else if (uuid != null)
                rows = MySQL.getGlobalDataSQL().querySQL("SELECT * FROM `users` WHERE `uuid`=? AND DATE_FORMAT(`updated_at`, '%Y-%m-%d %H:%i:%s') <> '0000-00-00 00:00:00'", uuid.toString());
            else if (name != null && !name.isEmpty())
                rows = MySQL.getGlobalDataSQL().querySQL("SELECT * FROM `users` WHERE `username`=?", name);
            if (rows.size() == 0);
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
        }
        catch (Exception e)
        {
            Discord.instance.sendErrorLog("1", "НЕ УДАЛОСЬ ЗАГРУЗИТЬ ПОЛЬЗОВАТЕЛЯ НАХУЙ СУКА", e);
            return false;
        }
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
