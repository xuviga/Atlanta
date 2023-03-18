package ru.imine.client.fancychat.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.imine.client.fancychat.chat.tab.ChatTab;
import ru.imine.client.fancychat.gui.button.GuiColorOverlayButton;
import ru.imine.client.fancychat.gui.button.GuiSmileOverlayButton;
import ru.imine.client.fancychat.gui.overlay.GuiFancyBcgColorOverlay;
import ru.imine.client.fancychat.gui.overlay.GuiFancyColorOverlay;
import ru.imine.client.fancychat.gui.overlay.GuiFancySmileOverlay;
import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.core.player.PlayerRank;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.FancyChatElement;
import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.FancyLine;
import ru.imine.shared.fancychat.chat.util.ChatTag;
import ru.imine.shared.fancychat.packet.FCPacket0ChatMessage;
import ru.imine.version.client.GuiMapper;
import ru.imine.version.client.MinecraftMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuiFancyChatInput extends GuiChat implements IFancyGuiScreen
{
    private final String defaultInputFieldText;
    private GuiFancySmileOverlay smileOverlay;
    private GuiFancyColorOverlay colorOverlay;
    private GuiFancyBcgColorOverlay bcgColorOverlay;

    private int sentHistoryCursor = 0;
    private static List<FancyLine> myLineHistory = new ArrayList<>();

    public GuiFancyChatInput(String defaultInputFieldText)
    {
        super(defaultInputFieldText);
        this.defaultInputFieldText = defaultInputFieldText;
    }

    @Override
    public void initGui()
    {
        FontRenderer fontRenderer = GuiMapper.instance().getFontRenderer(this);
        Keyboard.enableRepeatEvents(true);
        sentHistoryCursor = myLineHistory.size();
        inputField = new GuiFancyTextField(0, fontRenderer, 4, height - 12, width - 24, 12, 120, 1);
        inputField.setMaxStringLength(64);
        inputField.setEnableBackgroundDrawing(false);
        inputField.setFocused(true);
        inputField.setText(defaultInputFieldText);
        inputField.setCursorPosition(defaultInputFieldText.length());
        inputField.setCanLoseFocus(false);
        //this.tabCompleter = new TabCompleter(this.inputField);
        this.tabCompleter = new ChatTabCompleter(this.inputField);

        buttonList.add(new GuiSmileOverlayButton(0, width - 14, height - 14, 13, 13, this));
        buttonList.add(new GuiColorOverlayButton(1, width - 28, height - 14, 13, 13, this));

        smileOverlay = new GuiFancySmileOverlay(this, getInputField(), width - 114, height - 100);
        colorOverlay = new GuiFancyColorOverlay(this, getInputField(), width - GuiFancyColorOverlay.WIDTH - 15, height - GuiFancyColorOverlay.HEIGHT - 15);
        bcgColorOverlay = new GuiFancyBcgColorOverlay(this, getInputField(), width - 15, height - 15);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float wut)
    {
        FontRenderer fontRenderer = GuiMapper.instance().getFontRenderer(this);
        int rowSize = getInputField().fancyLine.getTotalRowSize(fontRenderer, GuiFancyChat.CHAT_WIDTH);

        drawRect(2, height - 2 - 12 * rowSize, width - 2, height - 1, 0x7F000000 | getInputField().fancyLine.bcgColor.value);
        GuiMapper.instance().setY(inputField, height - 12 * rowSize);
        inputField.drawTextBox();

        for (GuiButton butt : buttonList)
            butt.drawButton(mc, mouseX, mouseY, 0);

        smileOverlay.drawOverlay(fontRenderer, mouseX, mouseY);
        int[] range = getInputField().getSelectionRange();
        if (range[0] != range[1] && AiMineUser.getLocalPlayer().hasRank(PlayerRank.DIAMOND))
        {
            colorOverlay.isOverlayEnabled = true;
            colorOverlay.xPosition = getInputField().fontRenderer.getStringWidth(getInputField().fancyLine.toPlainText().substring(0, range[1]));
        }
        else
            colorOverlay.isOverlayEnabled = false;
        colorOverlay.drawOverlay(fontRenderer, mouseX, mouseY, wut);
        bcgColorOverlay.drawOverlay(fontRenderer, mouseX, mouseY, wut);
        GuiFancyChat.instance.processHover(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        for (GuiButton butt : buttonList)
            if (butt.mousePressed(mc, x, y))
            {
                if (butt.id == 0)
                {
                    smileOverlay.isOverlayEnabled = !smileOverlay.isOverlayEnabled;
                    bcgColorOverlay.isOverlayEnabled = false;
                }
                else if (butt.id == 1 && AiMineUser.getLocalPlayer().hasRank(PlayerRank.EMERALD))
                {
                    smileOverlay.isOverlayEnabled = false;
                    bcgColorOverlay.isOverlayEnabled = !bcgColorOverlay.isOverlayEnabled;
                }
                return;
            }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int chatY = scaledresolution.getScaledHeight() - y;
        if (chatY >= 190 && chatY <= 205)
        {
            for (ChatTab tab : GuiFancyChat.instance.tabs)
            {
                if (x > tab.x1 && x < tab.x2)
                {
                    GuiFancyChat.instance.currentTab = tab;
                    return;
                }
            }
        }

        if (!smileOverlay.mouseClicked(x, y) && !colorOverlay.mouseClicked(x, y, button) && !bcgColorOverlay.mouseClicked(x, y, button))
        {
            FancyChatElement element = GuiFancyChat.instance.getHoveredElement();
            if (element!=null)
            {
                element.mouseClicked(this);
                return;
            }
            super.mouseClicked(x, y, button);
        }
    }

    @Override
    public void sendChatMessage(String ignored, boolean addToChat)
    {
        try
        {
            FancyLine line = ((GuiFancyTextField) inputField).fancyLine;
            if (!line.isEmpty())
            {
                myLineHistory.add((FancyLine) line.clone());
                ChatTag tag = GuiFancyChat.instance.currentTab.mainTag;
                if (tag == ChatTag.SERVER)
                    tag = ChatTag.LOCAL;
                String plainText = line.toPlainText();
                if (plainText.startsWith("/"))
                {
                    MinecraftMapper.instance().getPlayer().sendChatMessage(plainText);
                    return;
                }
                FancyChatLine chatLine = new FancyChatLine(line, null, 0, tag);
                FancyChat.network.sendToServer(new FCPacket0ChatMessage(chatLine));
            }
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to send the message", e);
        }
    }

    @Override
    public void getSentHistory(int shift)
    {
        if (myLineHistory.isEmpty())
            return;
        int j = sentHistoryCursor + shift;
        int k = myLineHistory.size();

        if (j >= k)
            j = k - 1;

        if (j < 0)
            j = 0;

        getInputField().fancyLine = (FancyLine) myLineHistory.get(j).clone();
        getInputField().setCursorPosition(getInputField().fancyLine.getSymbolCount());
        sentHistoryCursor = j;
    }

    @Override
    protected void keyTyped(char key, int code) throws IOException
    {
        if (key == 1)
        {
            if (smileOverlay.isOverlayEnabled)
            {
                smileOverlay.isOverlayEnabled = false;
                return;
            }
            if (colorOverlay.isOverlayEnabled)
            {
                colorOverlay.isOverlayEnabled = false;
                return;
            }
            if (bcgColorOverlay.isOverlayEnabled)
            {
                bcgColorOverlay.isOverlayEnabled = false;
                return;
            }
        }
        super.keyTyped(key, code);
    }

    @Override //Паблик Морозов
    public void drawHoveringText(List text, int x, int y, FontRenderer fontRenderer)
    {
        super.drawHoveringText(text, x, y, fontRenderer);
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long pressureTime)
    {
        if (!colorOverlay.mouseClickMove(x, y, button, pressureTime))
        {
            super.mouseClickMove(x, y, button, pressureTime);
            getInputField().mouseClickMove(x, y, button, pressureTime);
        }
    }

    @Override
    protected void mouseReleased(int x, int y, int which)
    {
        colorOverlay.mouseReleased(x, y, which);
        super.mouseReleased(x, y, which);
        //getInputField().mouseMovedOrUp(x, y, which);
    }

    /*@Override
    public void func_146406_a(String[] wut)
    {
        if (wut.length!=0 && getInputField().getCursorPosition()>0)
        {
            int cursor = getInputField().getCursorPosition();
            FancyLine.ElementPosData data = getInputField().fancyLine.findLastElementBySubString(" ", 0, cursor);
            if (data!=null)
                getInputField().fancyLine.removeSymbolsInRange(data.offsetInLine + 1, cursor);
            else
                getInputField().fancyLine.removeSymbolsInRange(0, cursor);
        }
        super.func_146406_a(wut);
    }*/ //todo починить.. это таб, полагаю?

    public GuiFancyTextField getInputField()
    {
        return (GuiFancyTextField) inputField;
    }

    @SideOnly(Side.CLIENT)
    private class TabCompleter extends ChatTabCompleter
    {
        public TabCompleter(GuiTextField inputField)
        {
            super(inputField);
        }

        @Override
        public void complete()
        {
            innerComplete();

            if (this.completions.size() > 1)
            {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : this.completions)
                {
                    if (stringbuilder.length() > 0)
                    {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s);
                }

                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(stringbuilder.toString()), 1);
            }
        }

        private void innerComplete()
        {
            if (this.didComplete)
            {
                this.textField.deleteFromCursor(0);
                this.textField.deleteFromCursor(this.textField.getNthWordFromPosWS(-1, this.textField.getCursorPosition(), false) - this.textField.getCursorPosition());

                if (this.completionIdx >= this.completions.size())
                {
                    this.completionIdx = 0;
                }
            }
            else
            {
                this.completions.clear();
                this.completionIdx = 0;
                String prefix = this.textField.getText().substring(0, this.textField.getCursorPosition());

                if (prefix.length() >= 1)
                {
                    net.minecraftforge.client.ClientCommandHandler.instance.autoComplete(prefix);
                    Minecraft.getMinecraft().player.connection.sendPacket(new CPacketTabComplete(prefix, this.getTargetBlockPos(), this.hasTargetBlock));
                    this.requestedCompletions = true;
                }

                if (this.completions.isEmpty())
                    return;

                this.didComplete = true;

                int cursor = getInputField().getCursorPosition();
                FancyLine.ElementPosData data = getInputField().fancyLine.findLastElementBySubString(" ", 0, cursor);
                if (data!=null)
                    getInputField().fancyLine.removeSymbolsInRange(data.offsetInLine + 1, cursor);
                else
                    getInputField().fancyLine.removeSymbolsInRange(0, cursor);
            }

            this.textField.writeText(Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(this.completions.get(this.completionIdx++))));
        }
    }
}