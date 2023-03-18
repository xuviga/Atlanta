package ru.imine.shared.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.imine.client.core.packet.PacketHandler;
import ru.imine.shared.AiMine;
import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.annotations.KeepClass;

import java.io.*;

@KeepClass
public class CPacketDCFix extends IMineMessage
{
    @Override
    public void fromBytes(ObjectInputStream input) throws IOException, ClassNotFoundException {

    }

    @Override
    public void toBytes(ObjectOutputStream output) throws IOException {

    }
}

