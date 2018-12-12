package its_meow.coloredchests.chest;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import its_meow.coloredchests.ColoredChestsMod;

/**
 * Created by Dark on 7/27/2015.
 */
public class ItemBlockChest extends ItemBlock
{
    public ItemBlockChest(Block p_i45326_1_)
    {
        super(p_i45326_1_);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn)
    {
        if (stack != null && stack.getTagCompound() != null)
        {
            if (stack.getTagCompound().hasKey("rgb"))
            {
                Color color = ColoredChestsMod.getColor(stack.getTagCompound().getInteger("rgb"));
                list.add("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue());
            }

            if (stack.getTagCompound().hasKey("colorName"))
            {
                list.add("N: " + stack.getTagCompound().getString("colorName"));
            }
        }
    }
}