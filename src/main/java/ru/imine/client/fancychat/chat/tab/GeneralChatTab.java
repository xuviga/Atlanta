package ru.imine.client.fancychat.chat.tab;

import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.util.ChatTag;

public class GeneralChatTab extends ChatTab
{
    public GeneralChatTab(String id, ChatTag mainTag, String displayName, int color)
    {
        super(id, mainTag, displayName, color);
    }

    @Override
    public boolean suitable(FancyChatLine line)
    {
        if (ChatTag.SERVER.equals(line.tag))
            return line.timeStamp+120000>System.currentTimeMillis();
        return !ChatTag.TRADE.equals(line.tag);
    }
}
