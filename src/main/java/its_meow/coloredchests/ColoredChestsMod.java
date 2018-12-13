package its_meow.coloredchests;

import java.awt.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import its_meow.coloredchests.chest.BlockColoredChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Ref.MOD_ID, name = Ref.NAME, version = Ref.VERSION, acceptedMinecraftVersions = Ref.acceptedMCV)//, updateJSON = Ref.updateJSON)
public class ColoredChestsMod {

	@Instance(Ref.MOD_ID) 
	public static ColoredChestsMod mod;

	@SidedProxy(clientSide = Ref.CLIENT_PROXY_C, serverSide = Ref.SERVER_PROXY_C)
	public static its_meow.coloredchests.proxy.CommonProxy proxy;

	public static CreativeTab tab = new CreativeTab("ColoredChests");

	public static Logger logger;



	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger("coloredchests");
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
		for (int i = 0; i < ItemDye.DYE_COLORS.length; i++)
		{
			ItemStack stack = new ItemStack((BlockColoredChest) BlockRegistry.blocksList.get(i));
			String name = EnumDyeColor.byDyeDamage(i).getName();

			GameRegistry.addShapedRecipe(new ResourceLocation(Ref.MOD_ID + ":fromnormalchesttocolored" + name), new ResourceLocation(Ref.MOD_ID), stack, "d", "c", 'd', new ItemStack(Items.DYE, 1, i), 'c', Blocks.CHEST);
			for (int b = 0; b < ItemDye.DYE_COLORS.length; b++)
			{
				if (b != i)
				{
					ItemStack stack2 = new ItemStack((BlockColoredChest) BlockRegistry.blocksList.get(b));
					String name2 = EnumDyeColor.byDyeDamage(b).getName();
					GameRegistry.addShapedRecipe(new ResourceLocation(Ref.MOD_ID, "fromcolored" + name + "tocolored" + name2), new ResourceLocation(Ref.MOD_ID), stack, "d", "c", 'd', new ItemStack(Items.DYE, 1, i), 'c', stack2);
				}
			}
		}
		
	}

	public static Color getColor(int rgb)
	{
		return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}

	public static int getRGB(Color color)
	{
		int rgb = color.getRed();
		rgb = (rgb << 8) + color.getGreen();
		rgb = (rgb << 8) + color.getBlue();
		return rgb;
	}

	public static boolean doColorsMatch(Color a, Color b)
	{
		return a == b || a != null && b != null && a.getRGB() == b.getRGB();
	}
}
