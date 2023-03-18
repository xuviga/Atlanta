package ru.imine.server.fancychat.smile;

import ru.imine.shared.fancychat.smile.Smile;

import java.util.ArrayList;
import java.util.List;

public class PlayerSmileData
{
    public final long playerId;
    protected List<Smile> unlockedSmiles = new ArrayList<>();

    protected PlayerSmileData(long playerId)
    {
        this.playerId=playerId;
    }

    public boolean hasSmile(Smile smile)
    {
        return unlockedSmiles.contains(smile);
    }

    public List<Smile> getUnlockedSmiles()
    {
        return new ArrayList<>(unlockedSmiles);
    }
}
