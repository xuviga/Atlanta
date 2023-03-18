package ru.will0376.cases.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.will0376.cases.gui.GuiCaseView;
import ru.will0376.cases.gui.GuiCasesShop;

public class CasesMainPacket implements IMessage {
	String text;

	public CasesMainPacket() {
	}

	public CasesMainPacket(String text) {
		this.text = text;
	}

	public void fromBytes(ByteBuf buf) {
		this.text = ByteBufUtils.readUTF8String(buf);
	}

	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.text);
	}

	public static class Handler implements IMessageHandler<CasesMainPacket, IMessage> {
		public IMessage onMessage(CasesMainPacket message, MessageContext ctx) {
			if (message.text.equals("Clear")) {
				Recieve.CASES_LIST = "";
				Recieve.CURRENT_CASE_ITEMS_LIST = "";
				Recieve.WON_ITEM = "";
			}

			if (message.text.equals("ClearLast")) {
				Recieve.CURRENT_CASE_ITEMS_LIST = "";
			}

			if (message.text.equals("Open")) {
				Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiCasesShop()));
			}

			String[] args = message.text.split(",");
			if (args[0].equals("View")) {
				Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiCaseView(args[1])));
			}

			if (args[0].equals("SetWon")) {
				Recieve.WON_ITEM = args[1] + "," + args[2] + "," + args[3] + "," + args[4];
			}

			if (message.text.equals("RollCase")) {
				Recieve.isRolling = true;
			}
			if (message.text.equals("CloseGui")) {
				Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(null));
			}
			return null;
		}
	}
}
