package ru.imine.version.server.v1_12_2.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.server.util.AiCommand;
import ru.imine.server.util.PermsUtil;

import java.util.ArrayList;
import java.util.List;

public class SmilesReloadCommand extends AiCommand
{
    @Override
    public String getName()
    {
        return "smilesreload";
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        SmileManager.readSmiles();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return PermsUtil.hasPermission(sender,"imine.smileresload");
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
