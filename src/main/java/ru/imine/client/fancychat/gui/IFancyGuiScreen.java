package ru.imine.client.fancychat.gui;

import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public interface IFancyGuiScreen
{
    void drawHoveringText(List<String> text, int x, int y, FontRenderer fontRenderer);
}
