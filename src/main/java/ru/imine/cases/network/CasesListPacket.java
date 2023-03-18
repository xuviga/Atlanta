package ru.will0376.cases.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CasesListPacket implements IMessage {
   String text;

   public CasesListPacket() {
   }

   public CasesListPacket(String text) {
      this.text = text;
   }

   public void fromBytes(ByteBuf buf) {
      this.text = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.text);
   }

   public static class Handler implements IMessageHandler<CasesListPacket, IMessage> {
      @Override
      public IMessage onMessage(CasesListPacket message, MessageContext ctx) {
         if (Recieve.CASES_LIST.equalsIgnoreCase("")) {
            Recieve.CASES_LIST = message.text;
         } else {
            Recieve.CASES_LIST = Recieve.CASES_LIST + "," + message.text;
         }

         return null;
      }
   }
}
