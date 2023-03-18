package ru.imine.version.server;

import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ru.imine.version.server.v1_12_2.CommandMapper1_12_2;

import java.util.List;

public abstract class CommandMapper
{
    protected static CommandMapper instance;

    public static CommandMapper instance()
    {
        if (instance==null)
            instance = new CommandMapper1_12_2();
        return instance;
    }

    public abstract List<String> getAliases(ICommand command);
    public abstract String getName(ICommand command);
    public abstract void registerCommands(FMLServerStartingEvent e);
}