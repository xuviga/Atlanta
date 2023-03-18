package ru.imine.shared.core.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.server.core.AiMineCore;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.shared.AiMine;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.FancyLine;
import ru.imine.shared.fancychat.packet.FCPacket0ChatMessage;
import ru.imine.shared.util.Location;
import ru.imine.version.client.MinecraftMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Юзается для передачи данных о юзере сервером клиенту
 */
public class AiMineUser implements Serializable
{
    private static final long serialVersionUID = 322L;

    public static final Map<Long,AiMineUser> users = new ConcurrentHashMap<>();
    private static boolean isServer;

    public final long playerId;
    public final UUID uuid;
    public final String name;

    public final String displayName;
    public final PlayerRank playerRank;
    public final PlayerRole playerRole;
    public final int nickColor;
    public final int chatColor;

    public final long money;
    public final Location location;
    public final List<List<String>> propertyMap;
    public final GameType gameMode;
    public final boolean isOnline;

    static
    {
        isServer = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
        if (!isServer || AiMineCore.isTest())
            users.put(322L, new AiMineUser());
    }

    public static AiMineUser get(ICommandSender sender)
    {
        return get((EntityPlayer) sender);
    }

    public static AiMineUser get(EntityPlayer player)
    {
        if (isServer)
        {
            AiMinePlayerMP playerMP = AiMinePlayerMP.get(player);
            return playerMP.asUser(playerMP);
        }
        return null;
        //return users.values().stream().filter(it -> it.name.equalsIgnoreCase(player.getName())).findFirst().orElse(null);
    }

    public static AiMineUser get(long playerId)
    {
        if (isServer)
        {
            AiMinePlayerMP playerMP = AiMinePlayerMP.get(playerId);
            return playerMP.asUser(playerMP);
        }
        return users.get(playerId);
    }

    public static AiMineUser get(UUID uuid)
    {
        if (isServer)
        {
            AiMinePlayerMP playerMP = AiMinePlayerMP.get(uuid);
            return playerMP.asUser(playerMP);
        }
        return users.values().stream().filter(it -> it.uuid.equals(uuid)).findFirst().orElse(null);
    }

    public static AiMineUser get(String username)
    {
        if (isServer && FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
        {
            AiMinePlayerMP playerMP = AiMinePlayerMP.get(username);
            return playerMP.asUser(playerMP);
        }
        return users.values().stream().filter(it -> it.name.equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    @SideOnly(Side.SERVER)
    public static AiMineUser getOffline(String name)
    {
        AiMineUser user = get(name);
        if (user!=null)
            return user;

        return null;
    }

    @SideOnly(Side.SERVER)
    public static AiMineUser getOffline(UUID uuid)
    {
        AiMineUser user = get(uuid);
        if (user!=null)
            return user;
        return null;
    }

    @SideOnly(Side.SERVER)
    public static AiMineUser getOffline(long playerId)
    {
        AiMineUser user = get(playerId);
        if (user!=null)
            return user;

        return null;
    }

    public static AiMineUser getLocalPlayer()
    {
        if (MinecraftMapper.instance()==null)
            AiMine.LOGGER.info("MinecraftMapper.instance()="+MinecraftMapper.instance());
        else if (MinecraftMapper.instance().getPlayer()==null)
            AiMine.LOGGER.info("MinecraftMapper.instance().getPlayer()="+MinecraftMapper.instance().getPlayer());
        return get(MinecraftMapper.instance().getPlayer().getName());
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    private AiMineUser()
    {
        playerId = 322;
        EntityPlayer player = Minecraft.getMinecraft().player;
        uuid = player.getUniqueID();
        name = player.getName();
        playerRank = PlayerRank.DEFAULT;
        playerRole = PlayerRole.DEFAULT;
        nickColor = 0xFFFFFF;
        chatColor = 0xFFFFFF;
        displayName = player.getDisplayNameString();
        isOnline = true;
        money = 9000;
        location = new Location(player.world,player.posX,player.posY,player.posZ);
        gameMode = GameType.SURVIVAL;
        propertyMap = new ArrayList<>();
        for (Map.Entry<String, Property> entry : player.getGameProfile().getProperties().entries())
        {
            List<String> property = new ArrayList<>();
            property.add(entry.getValue().getName());
            property.add(entry.getValue().getSignature());
            property.add(entry.getValue().getValue());
            propertyMap.add(property);
        }
    }

    @Deprecated
    @SideOnly(Side.SERVER)
    public AiMineUser(AiMinePlayerMP viewer, AiMinePlayerMP player)
    {
        playerId = player.getPlayerId();
        uuid = player.getUUID();
        name = player.getName();
        playerRank = player.getPlayerRank();
        playerRole = player.getPlayerRole();
        nickColor = player.getNickColor();
        chatColor = player.getChatColor();
        displayName = player.getDisplayName().getFormattedText();
        isOnline = player.isOnline();

        if (viewer!=null && viewer.equals(player))
        {
            money = player.getMoney();
        }
        else
        {
            money = 0;
        }
        location = player.getLocation();
        gameMode = player.getGameMode();
        propertyMap = new ArrayList<>();
        for (Map.Entry<String, Property> entry : player.getEntity().getGameProfile().getProperties().entries())
        {
            List<String> property = new ArrayList<>();
            property.add(entry.getValue().getName());
            property.add(entry.getValue().getSignature());
            property.add(entry.getValue().getValue());
            propertyMap.add(property);
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

    public GameProfile getGameProfile()
    {
        GameProfile gameProfile = new GameProfile(uuid,name);
        for (List<String> entry : propertyMap)
        {
            Property property = new Property(entry.get(0),entry.get(1),entry.get(2));
            gameProfile.getProperties().put(property.getName(), property);
        }
        return gameProfile;
    }

    public void sendMessage(String line)
    {
        FCPacket0ChatMessage.onMessageClient(new FCPacket0ChatMessage(FancyChat.stringToChatLine(line))); //todo redo
    }

    public void sendMessage(FancyLine line)
    {
        FCPacket0ChatMessage.onMessageClient(new FCPacket0ChatMessage(FancyChat.lineToChatLine(line))); //todo redo
    }

    public void sendMessage(FancyChatLine line)
    {
        FCPacket0ChatMessage.onMessageClient(new FCPacket0ChatMessage(line)); //todo redo
    }

    @Override
    public String toString()
    {
        return "AiMineUser={" + name + " #" + playerId + " "+uuid+" "+playerRank+" "+playerRole+"}";
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof AiMineUser && playerId == ((AiMineUser) other).playerId;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(playerId);
    }

    public PlayerRank getPlayerRank()
    {
        return playerRank;
    }

    public PlayerRole getPlayerRole()
    {
        return playerRole;
    }

}