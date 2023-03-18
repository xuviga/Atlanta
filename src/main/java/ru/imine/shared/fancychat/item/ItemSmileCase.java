package ru.imine.shared.fancychat.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.shared.fancychat.smile.SmilePack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemSmileCase extends ItemFood
{
    private static final List<ItemSmileCase> items = new ArrayList<>();
    private SmilePack smilePack;

    private ItemSmileCase(SmilePack smilePack)
    {
        super(0, 0, false);
        this.smilePack = smilePack;
        setAlwaysEdible();
        String key = "smile_case_" + smilePack.name().toLowerCase();
        setRegistryName(key);
        setTranslationKey(key);
    }

    public SmilePack getSmilePack()
    {
        return smilePack;
    }

    public static List<ItemSmileCase> getItems()
    {
        return new ArrayList<>(items);
    }

    public static void generate(SmilePack smilePack)
    {
        ItemSmileCase item = new ItemSmileCase(smilePack);
        items.add(item);
        ForgeRegistries.ITEMS.register(item);
        //API.hideItem(new ItemStack(itemSmileCase));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
    {
        if (!(entityLiving instanceof EntityPlayer) || entityLiving instanceof FakePlayer)
            return stack;

        if (!world.isRemote)
        {
            AiMinePlayerMP player = AiMinePlayerMP.get(entityLiving);
            if (player==null)
                return stack;
            SmileManager.openSmileCase(smilePack, world, player);
        }

        return super.onItemUseFinish(stack,world,entityLiving);
    }

    // 1.10.2
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean wut)
    {
        addInformation(stack, player.world, info, ITooltipFlag.TooltipFlags.NORMAL);
    }

    // 1.12.2
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(I18n.format(getUnlocalizedNameInefficiently(stack) + ".desc").trim());
    }
}