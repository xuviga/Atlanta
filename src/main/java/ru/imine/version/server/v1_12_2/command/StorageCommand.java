package ru.imine.version.server.v1_12_2.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import ru.imine.server.core.database.Storage;
import ru.imine.server.util.AiCommand;
import ru.imine.server.util.DiscordAPI;
import ru.imine.server.util.PermsUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageCommand extends AiCommand
{
    @Override
    public String getName()
    {
        return "storage";
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_)
    {
        return "nope";
    }

    @Override
    public List<String> getAliases()
    {
        return new ArrayList<>();
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender sender, String[] args)
    {
        if (sender instanceof EntityPlayerMP && !PermsUtil.hasPermission(sender,"imine.admin"))
        {
            DiscordAPI.getInstance().sendErrorLog("StorageCommand","Игрок попал в вызов /storage без прав!");
            return;
        }
        if (args[0].equalsIgnoreCase("help"))
        {
            sender.sendMessage(new TextComponentString("/storage <storage> get <path> - получает значение\n" +
                    "/storage <storage> mapget <path> <key> - получает мап-значение\n" +
                    "/storage <storage> set <path> <value> - задает значение\n" +
                    "/storage <storage> delete <path> - задает значение\n" +
                    "/storage <storage> mapkeydelete <path> <key> - удаляет мап-значение\n"+
                    "/storage <storage> mapdelete <path> - удаляет всю мапу\n"
            ));
        }
        Storage storage = Storage.getStorage(args[0]);
        if (storage==null)
        {
            sender.sendMessage(new TextComponentString("§cСтораж не найден"));
            return;
        }
        if (args[1].equalsIgnoreCase("get"))
            sender.sendMessage(new TextComponentString(storage.get(args[2],args[3])));
        else if (args[1].equalsIgnoreCase("set"))
            storage.set(args[2],args[3],args[4]);
        else if (args[1].equalsIgnoreCase("delete_path"))
            storage.deletePath(args[2]);
        else if (args[1].equalsIgnoreCase("delete_key"))
            storage.deleteKey(args[2],args[3]);
        sender.sendMessage(new TextComponentString("§aDone!"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender instanceof EntityPlayerMP && PermsUtil.hasPermission(sender,"imine.admin");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_)
    {
        return false;
    }
}
