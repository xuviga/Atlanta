package ru.imine.server.util;

import net.minecraft.command.ICommand;

import java.util.ArrayList;
import java.util.List;

public abstract class AiCommand implements ICommand
{
    public static final List<AiCommand> COMMANDS = new ArrayList<>();

    protected AiCommand()
    {
        COMMANDS.add(this);
    }

    @Override
    public int compareTo(ICommand o)
    {
        return 1;
    }
}