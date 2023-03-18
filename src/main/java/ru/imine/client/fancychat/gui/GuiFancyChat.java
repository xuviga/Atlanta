package ru.imine.client.fancychat.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import ru.imine.client.fancychat.chat.tab.ChatTab;
import ru.imine.client.fancychat.chat.tab.GeneralChatTab;
import ru.imine.client.fancychat.chat.tab.LocalChatTab;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.FancyChatComponentElement;
import ru.imine.shared.fancychat.chat.FancyChatElement;
import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.util.ChatBcgColor;
import ru.imine.shared.fancychat.chat.util.ChatTag;
import ru.imine.shared.fancychat.packet.FCPacket0ChatMessage;
import ru.imine.version.client.GuiMapper;

import java.util.ArrayList;
import java.util.List;

public class GuiFancyChat extends GuiNewChat
{
    public static final int CHAT_WIDTH = 370;
    public static final int LINE_SPACING = 4;
    public static final int MAX_VISIBLE_LINES = 13;

    public static GuiFancyChat instance;

    private final Minecraft mc;
    public List<ChatTab> tabs = new ArrayList<>();
    public ChatTab currentTab;
    private List<FancyChatLine> fancyChatLines = new ArrayList<>();

    private int scrollPos;
    private long prevTick = System.currentTimeMillis();
    private int mouseX, mouseY;
    private FancyChatElement hoveredElement;

    public GuiFancyChat(Minecraft mc)
    {
        super(mc);
        instance = this;
        this.mc = mc;
        tabs.add(new GeneralChatTab(ChatTag.GENERAL.name, ChatTag.GENERAL, "Общий", ChatTag.GENERAL.color));
        ChatTab tab = new LocalChatTab(ChatTag.LOCAL.name, ChatTag.LOCAL, "Локальный", ChatTag.LOCAL.color);
        tab.tags.add(ChatTag.LOCAL);
        tab.tags.add(ChatTag.SERVER);
        tab.tags.add(ChatTag.ADMIN);
        tabs.add(tab);
        tab = new ChatTab(ChatTag.SERVER.name, ChatTag.SERVER, "Инфо", ChatTag.SERVER.color);
        tab.tags.add(ChatTag.SERVER);
        tab.tags.add(ChatTag.ADMIN);
        tabs.add(tab);
        tab = new ChatTab(ChatTag.TRADE.name, ChatTag.TRADE, "Торговый", ChatTag.TRADE.color);
        tab.tags.add(ChatTag.TRADE);
        tab.tags.add(ChatTag.ADMIN);
        tabs.add(tab);
        currentTab = tabs.get(0);
    }

    @Override
    public void printChatMessageWithOptionalDeletion(ITextComponent component, int wut)
    {
        FancyChatLine line = new FancyChatLine(null, System.currentTimeMillis(), ChatBcgColor.SERVER, ChatTag.SERVER);
        line.add(new FancyChatComponentElement(component));
        FCPacket0ChatMessage.onMessageClient(new FCPacket0ChatMessage(line));
        FancyChat.CHAT_LOGGER.info(component.getUnformattedText());
    }

    public void addChatLine(FancyChatLine line)
    {
        fancyChatLines.add(0,line);
    }

    @Override
    public void scroll(int scroll)
    {
        scrollPos += scroll;
        if (scrollPos > fancyChatLines.size() - MAX_VISIBLE_LINES)
            scrollPos = fancyChatLines.size() - MAX_VISIBLE_LINES;
        if (scrollPos < 0)
            scrollPos = 0;
    }

    @Override
    public void drawChat(int updateCounter)
    {
        try
        {
            if (this.mc.gameSettings.chatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN)
                return;

            hoveredElement = null;
            FontRenderer fontRenderer = GuiMapper.instance().getFontRenderer();
            float scale = getChatScale();
            boolean isChatOpen = this.getChatOpen();

            GlStateManager.pushMatrix();
            GlStateManager.translate(2.0F, 8.0F, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);

            int rowHeight = fontRenderer.FONT_HEIGHT + LINE_SPACING;
            int xOffset = 5;
            int yOffset = 25;
            float opacity = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;

            int currentY = yOffset;
            int messageCounter = 0;
            int lineCounter = 0;
            int skipConter = 0;
            for (; skipConter + lineCounter + scrollPos < fancyChatLines.size()
                    && lineCounter < MAX_VISIBLE_LINES;)
            {
                int index = messageCounter + scrollPos + skipConter;
                if (index >= fancyChatLines.size())
                {
                    break;
                }
                FancyChatLine chatLine = fancyChatLines.get(index);

                if (!currentTab.suitable(chatLine))
                {
                    skipConter++;
                    continue;
                }

                int alpha = 0x7F;
                double lifeTime = System.currentTimeMillis() - chatLine.timeStamp;
                if (!isChatOpen)
                {
                    scrollPos = 0;
                    if (lifeTime >= 10000)
                        break;
                    if (lifeTime >= 8000)
                        alpha = (int) ((1 - (lifeTime - 7500) / 2000) * 0x7F);
                }

                int totalRowSize = chatLine.getTotalRowSize(fontRenderer, CHAT_WIDTH);

                if (messageCounter+totalRowSize>MAX_VISIBLE_LINES)
                    break;

                if (alpha > 10)
                {
                    alpha *= opacity;
                    alpha = alpha << 24;
                    int oldY = currentY;
                    currentY -= rowHeight * totalRowSize;

                    //todo photon
                    drawRect(-2, currentY - 2, CHAT_WIDTH +4, oldY - 2, alpha | chatLine.bcgColor.value);
                    if (chatLine.tag != null)
                        drawRect(-2, currentY - 2, 2, oldY - 2, alpha | chatLine.tag.color);
                    chatLine.draw(fontRenderer, xOffset, currentY, rowHeight, CHAT_WIDTH, alpha);

                    FancyChatElement hovered = chatLine.getElement(fontRenderer, rowHeight, CHAT_WIDTH, (int) (mouseX / scale) - xOffset, (int) (mouseY / scale) - currentY);
                    if (hovered != null)
                    {
                        if (hovered.getHover()!=null)
                            hovered.getHover().draw(fontRenderer, mouseX, mouseY, rowHeight, CHAT_WIDTH, 255);
                        hoveredElement = hovered;
                    }
                }

                lineCounter += totalRowSize;
                messageCounter++;
            }

            if (isChatOpen)
            {
                int alpha = (int)(0x7F * opacity);
                drawRect(-2, currentY - 2, CHAT_WIDTH + 4, -146, alpha << 24);

                for (ChatTab tab : tabs)
                {
                    int yAdd = tab.id.equalsIgnoreCase(currentTab.id) ? 4 : 0;
                    int lastX = xOffset;
                    tab.x1 = xOffset;
                    xOffset = fontRenderer.getStringWidth(tab.displayName) + lastX + 6;
                    tab.x2 = xOffset;
                    drawRect(lastX + 1, -161-yAdd, xOffset - 1, -146, (int)((yAdd==0 ? 0x3F : 0x7F) * opacity) << 24);
                    drawRect(lastX + 1, -161-yAdd, xOffset - 1, -159-yAdd, alpha | tab.color);
                    fontRenderer.drawString(tab.displayName, lastX + 3, -157-yAdd, alpha | 0x00FFFFFF, false);
                }
            }
            GlStateManager.popMatrix();
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to draw chat", e);
        }
    }

    protected FancyChatElement getHoveredElement()
    {
        return hoveredElement;
    }

    protected void processHover(int mouseX, int mouseY)
    {
        this.mouseX=mouseX;
        this.mouseY=mouseY;
    }
}