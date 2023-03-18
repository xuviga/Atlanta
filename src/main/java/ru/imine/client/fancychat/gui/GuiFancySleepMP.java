package ru.imine.client.fancychat.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.client.FMLClientHandler;
import ru.imine.version.client.MinecraftMapper;

import java.io.IOException;

public class GuiFancySleepMP extends GuiFancyChatInput
{
	public GuiFancySleepMP()
	{
		super("");
	}

	@Override
	public void initGui()
	{
		super.initGui();
		buttonList.add(new GuiButton(1, width / 2 - 100, height - 40, I18n.format("multiplayer.stopSleeping")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float wut)
	{
		super.drawScreen(mouseX, mouseY, wut);
		for (GuiButton button : this.buttonList)
			button.drawButton(mc, mouseX, mouseY, 0);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if (!FMLClientHandler.instance().getClientPlayerEntity().isPlayerSleeping())
		{
			FMLClientHandler.instance().getClient().displayGuiScreen(null);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if (keyCode == 1)
		{
			wakeFromSleep();
		}
		else
		{
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.id == 1)
		{
			wakeFromSleep();
		}
		else
		{
			super.actionPerformed(button);
		}
	}

	private void wakeFromSleep()
	{
		NetHandlerPlayClient nethandlerplayclient = MinecraftMapper.instance().getPlayer().connection;
		nethandlerplayclient.sendPacket(new CPacketEntityAction(MinecraftMapper.instance().getPlayer(), CPacketEntityAction.Action.STOP_SLEEPING));
	}
}