package ru.imine.shared.fancychat.smile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.shared.util.Rarity;

import java.util.Random;

public enum SmilePack
{
    NORMAL
            {
                @Override
                @SideOnly(Side.SERVER)
                public void openCase(AiMinePlayerMP player)
                {
                    SmileManager.provideSmile(player, Rarity.NORMAL);
                    SmileManager.provideSmile(player, Rarity.NORMAL);
                }
            },
    RARE
            {
                @Override
                @SideOnly(Side.SERVER)
                public void openCase(AiMinePlayerMP player)
                {
                    Random random = new Random(); //todo PseudoRandom
                    SmileManager.provideSmile(player, Rarity.RARE);
                    SmileManager.provideSmile(player, random.nextDouble() < 0.1 ? Rarity.RARE : Rarity.NORMAL);
                    SmileManager.provideSmile(player, random.nextDouble() < 0.1 ? Rarity.RARE : Rarity.NORMAL);
                }
            },
    EPIC
            {
                @Override
                @SideOnly(Side.SERVER)
                public void openCase(AiMinePlayerMP player)
                {
                    Random random = new Random(); //todo PseudoRandom
                    SmileManager.provideSmile(player, Rarity.EPIC);
                    if (random.nextDouble() < 0.1)
                    {
                        SmileManager.provideSmile(player, Rarity.EPIC);
                        SmileManager.provideSmile(player, Rarity.NORMAL);
                    }
                    else if (random.nextDouble() < 0.3)
                    {
                        SmileManager.provideSmile(player, Rarity.RARE);
                        SmileManager.provideSmile(player, Rarity.RARE);
                    }
                    else if (random.nextDouble() < 0.6)
                    {
                        SmileManager.provideSmile(player, Rarity.RARE);
                        SmileManager.provideSmile(player, Rarity.NORMAL);
                    }
                    else
                    {
                        SmileManager.provideSmile(player, Rarity.NORMAL);
                        SmileManager.provideSmile(player, Rarity.NORMAL);
                    }
                }
            };

    public abstract void openCase(AiMinePlayerMP player);
}