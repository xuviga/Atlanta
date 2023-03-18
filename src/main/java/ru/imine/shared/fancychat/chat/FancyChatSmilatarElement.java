package ru.imine.shared.fancychat.chat;

import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.smile.Smile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FancyChatSmilatarElement extends FancyChatSmileElement
{
    private static final long serialVersionUID = 322L;

    public FancyChatSmilatarElement(Smile smile)
    {
        super(smile.category.name, smile.name);
        this.smile = smile;
    }

    @Override
    public FancyLine getHover()
    {
        return FancyChat.stringToChatLine("Личный смайлопрефикс");
    }

    private void writeObject(ObjectOutputStream oos) throws IOException
    {
        oos.writeObject(smile);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
    {
        smile = (Smile) ois.readObject();
        smile.loadImage();
    }
}