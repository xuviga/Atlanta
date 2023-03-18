package ru.imine.client.fancychat.chat.tab;

import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.util.ChatTag;
import ru.imine.version.client.MinecraftMapper;

public class LocalChatTab extends ChatTab
{
    public LocalChatTab(String id, ChatTag mainTag, String displayName, int color)
    {
        super(id, mainTag, displayName, color);
    }

    @Override
    public boolean suitable(FancyChatLine line)
    {
        if (ChatTag.SERVER.equals(line.tag))
            return line.timeStamp+120000>System.currentTimeMillis();
        return super.suitable(line) || (line.tag.equals(ChatTag.GENERAL) && MinecraftMapper.instance().getPlayer().getUniqueID().equals(line.sender.uuid));
        //todo fix
    }
}
