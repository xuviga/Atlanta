package ru.imine.version.server.v1_12_2;

import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ru.imine.version.server.CommandMapper;
import ru.imine.version.server.v1_12_2.command.DonateCommand;
import ru.imine.version.server.v1_12_2.command.SmilesReloadCommand;
import ru.imine.version.server.v1_12_2.command.StorageCommand;

import java.util.List;

public class CommandMapper1_12_2 extends CommandMapper
{
    @Override
    public List<String> getAliases(ICommand command)
    {
        return command.getAliases();
    }

    @Override
    public String getName(ICommand command)
    {
        return command.getName();
    }

    @Override
    public void registerCommands(FMLServerStartingEvent e)
    {
        e.registerServerCommand(new SmilesReloadCommand());
        e.registerServerCommand(new DonateCommand());
        e.registerServerCommand(new StorageCommand());
    }
}