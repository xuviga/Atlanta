package ru.imine.shared.fancychat.chat;

import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.fancychat.chat.util.ChatBcgColor;
import ru.imine.shared.fancychat.chat.util.ChatTag;

public class FancyChatLine extends FancyLine
{
    private static final long serialVersionUID = 322L;

    public AiMineUser sender;
    public long timeStamp;
    public ChatTag tag;

    public FancyChatLine(FancyLine line, AiMineUser sender, long timeStamp, ChatBcgColor color, ChatTag tag)
    {
        this(sender,timeStamp,color,tag);
        addAll(line);
    }

    public FancyChatLine(FancyLine line, AiMineUser sender, long timeStamp, ChatTag tag)
    {

        this(sender,timeStamp,line.bcgColor,tag);
        addAll(line);
    }

    public FancyChatLine(AiMineUser sender, long timeStamp, ChatBcgColor bcgColor, ChatTag tag)
    {
        this.sender=sender;
        this.timeStamp = timeStamp;
        this.bcgColor=bcgColor;
        this.tag = tag;
    }

    @Override
    public FancyChatLine clone()
    {
        FancyChatLine clone = new FancyChatLine(sender,timeStamp,bcgColor,tag);
        for (FancyChatElement element : this)
            clone.add(element.clone());
        return clone;
    }

    @Override
    public String toString()
    {
        return String.format("FancyChatLine={sender=%s; timeStamp=%s; bcgColor=%s; tag=%s, elements=%s}",
                sender,timeStamp,bcgColor,tag,super.toString());
    }
}