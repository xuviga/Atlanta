package ru.imine.server.util;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import ru.imine.server.core.AiMineCore;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.shared.util.RobertoGarbagio;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermsUtil
{
    private static final Server server = Bukkit.getServer();

    public static boolean hasPermission(ICommandSender sender, String permission)
    {
        if (sender instanceof EntityPlayer)
            return hasPermission((EntityPlayer) sender, permission);
        else
            return true;
    }

    public static boolean hasPermission(EntityPlayer player, String permission)
    {
        return hasPermission(player.getUniqueID(), permission);
    }

    public static boolean hasPermission(long playerId, String permission)
    {
        return hasPermission(AiMinePlayerMP.getUUID(playerId), permission);
    }

    public static boolean hasPermission(UUID uuid, String permission)
    {
        try
        {
            RobertoGarbagio.LOGGER.info("see="+server.getPlayer(uuid).hasPermission(permission));
            return server.getPlayer(uuid).hasPermission(permission);
        }
        catch (Exception e)
        {
            return AiMineCore.getServerName().equalsIgnoreCase("Test Server");
        }
    }

    public static boolean inheritsGroup(long playerId, String groupName)
    {
        return inheritsGroup(AiMinePlayerMP.getUUID(playerId), groupName);
    }

    public static boolean inheritsGroup(EntityPlayer player, String groupName)
    {
        return inheritsGroup(player.getUniqueID(), groupName);
    }

    public static boolean inheritsGroup(UUID uuid, String groupName)
    {
        try
        {
            return server.getPlayer(uuid).hasPermission("group."+groupName);
        }
        catch (IllegalStateException e)
        {
            return AiMineCore.getServerName().equalsIgnoreCase("iPingas");
        }
    }

    public static Set<String> getGroups(EntityPlayer player)
    {
        return getGroups(player.getUniqueID());
    }

    public static Set<String> getGroups(long playerId)
    {
        return getGroups(AiMinePlayerMP.getUUID(playerId));
    }
//
    public static Set<String> getGroups(UUID uuid)
    {
        try
        {
            User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
            Set<String> groups = user.getNodes().stream()
                    .filter(NodeType.INHERITANCE::matches)
                    .map(NodeType.INHERITANCE::cast)
                    .map(InheritanceNode::getGroupName)
                    .collect(Collectors.toSet());

            RobertoGarbagio.LOGGER.info("getGroups="+groups);
            return groups;


        }
        catch (IllegalStateException e)
        {
            return AiMineCore.isTest() ? new HashSet<>() : new HashSet<>();
        }
    }

    public static void provideGroup(long playerId, String group, long time)
    {
        provideGroup(AiMinePlayerMP.getUUID(playerId), group, time);
    }

    public static void provideGroup(UUID uuid, String group, long time)
    {
        try
        {
            InheritanceNode node = InheritanceNode.builder(group).expiry(time).build();
            User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
            if (user.data().add(node).wasSuccessful()) {
                LuckPermsProvider.get().getUserManager().saveUser(user);
            }
           RobertoGarbagio.LOGGER.info("providing group "+group+" for "+time+" проверяй");
        }
        catch (IllegalStateException ignored)
        {
        }
    }
}