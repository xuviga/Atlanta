package ru.imine.client.fancychat.chat.tab;

import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.util.ChatTag;

import java.util.ArrayList;
import java.util.List;

public class ChatTab
{
    public final String id;
    public final String displayName;
    public final int color;
    public final ChatTag mainTag;
    public final List<ChatTag> tags = new ArrayList<>();

    public int x1,x2;

    public ChatTab(String id, ChatTag mainTag, String displayName, int color)
    {
        this.id = id;
        this.mainTag = mainTag;
        this.displayName = displayName;
        this.color = color;
    }

    public boolean suitable(FancyChatLine line)
    {
        return tags.contains(line.tag);
    }
}
