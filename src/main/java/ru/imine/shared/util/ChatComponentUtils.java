package ru.imine.shared.util;

import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatComponentUtils
{
    public static List<ITextComponent> extractSiblings(ITextComponent sourceComponent)
    {
        List<ITextComponent> result = new ArrayList<>();
        ITextComponent component = sourceComponent.createCopy();
        component.getSiblings().clear();
        for (Object obj : sourceComponent.getSiblings())
            result.addAll(extractSiblings((ITextComponent) obj));
        return result;
    }

    public static List<ITextComponent> splitTextComponent(ITextComponent sourceComponent, int pos, int limit)
    {
        if (pos==0)
            return Collections.singletonList(sourceComponent);
        List<ITextComponent> result = new ArrayList<>();

        String rawText = sourceComponent.getUnformattedComponentText();
        if (pos>=rawText.length())
            return Collections.singletonList(sourceComponent);
        if (rawText.length() <= pos)
        {
            result.add(sourceComponent);
        }
        else
        {
            for (int i = 1; limit <= 0 || i < limit; i++)
            {
                if (rawText.length() > pos)
                {
                    ITextComponent currentComponent = new TextComponentString(rawText.substring(0, pos));
                    currentComponent.setStyle(sourceComponent.getStyle());
                    result.add(currentComponent);
                    rawText = rawText.substring(pos);
                }
            }
            ITextComponent currentComponent = new TextComponentString(rawText);
            currentComponent.setStyle(sourceComponent.getStyle());
            result.add(currentComponent);
        }
        return result;
    }
}
