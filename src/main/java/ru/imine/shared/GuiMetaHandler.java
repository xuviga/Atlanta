package ru.imine.shared;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ru.imine.shared.util.annotations.KeepName;

import java.util.ArrayList;
import java.util.List;

public class GuiMetaHandler implements IGuiHandler
{
    private static final List<IGuiHandler> handlers = new ArrayList<>();

    public static void addGuiHandler(IGuiHandler handler)
    {
        handlers.add(handler);
    }

    @Override
    @KeepName
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (IGuiHandler handler : handlers)
        {
            Object result = handler.getServerGuiElement(ID,player,world,x,y,z);
            if (result!=null)
                return result;
        }
        return null;
    }

    @Override
    @KeepName
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (IGuiHandler handler : handlers)
        {
            Object result = handler.getClientGuiElement(ID,player,world,x,y,z);
            if (result!=null)
                return result;
        }
        return null;
    }
}

