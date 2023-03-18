package ru.imine.server.core.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SetBlockStateEvent extends Event
{
    public final BlockPos pos;
    public final IBlockState oldState;
    public final IBlockState newState;
    public final int flags;

    public SetBlockStateEvent(BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        this.pos = pos;
        this.oldState = oldState;
        this.newState = newState;
        this.flags = flags;
    }
}
