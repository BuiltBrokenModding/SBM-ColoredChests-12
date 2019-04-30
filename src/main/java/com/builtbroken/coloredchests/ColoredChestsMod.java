package com.builtbroken.coloredchests;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.builtbroken.coloredchests.chest.BlockColoredChest;
import com.builtbroken.coloredchests.chest.TileEntityColoredChest;
import com.google.common.base.Preconditions;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ColoredChestsMod.MOD_ID)
@Mod(modid = ColoredChestsMod.MOD_ID, name = ColoredChestsMod.NAME, version = ColoredChestsMod.VERSION)
public class ColoredChestsMod {
    
    public static final String MOD_ID = "coloredchests";
    public static final String NAME = "ColoredChests";
    public static final String VERSION = "1.0.0";
    
    @Instance(MOD_ID) 
    public static ColoredChestsMod mod;
    
    public static ArrayList<Block> BLOCKS = new ArrayList<Block>();
    public static ArrayList<ItemBlock> ITEMBLOCKS = new ArrayList<ItemBlock>();

    public static CreativeTabs tab = new CreativeTabs("ColoredChests") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Blocks.CHEST);
        }
    };

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger("coloredchests");
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        for (int i = 0; i < ItemDye.DYE_COLORS.length; i++) {
            ItemStack stack = new ItemStack((BlockColoredChest) BLOCKS.get(i));
            String name = EnumDyeColor.byDyeDamage(i).getName();

            GameRegistry.addShapedRecipe(new ResourceLocation(ColoredChestsMod.MOD_ID + ":fromnormalchesttocolored" + name), new ResourceLocation(ColoredChestsMod.MOD_ID), stack, "d", "c", 'd', new ItemStack(Items.DYE, 1, i), 'c', Blocks.CHEST);
            for (int b = 0; b < ItemDye.DYE_COLORS.length; b++) {
                if (b != i)
                {
                    ItemStack stack2 = new ItemStack((BlockColoredChest) BLOCKS.get(b));
                    String name2 = EnumDyeColor.byDyeDamage(b).getName();
                    GameRegistry.addShapedRecipe(new ResourceLocation(ColoredChestsMod.MOD_ID, "fromcolored" + name + "tocolored" + name2), new ResourceLocation(ColoredChestsMod.MOD_ID), stack, "d", "c", 'd', new ItemStack(Items.DYE, 1, i), 'c', stack2);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();


        for(int i = 0; i < EnumDyeColor.values().length; i++) {
            int colorI = EnumDyeColor.byDyeDamage(i).getColorValue();
            Color color = ColoredChestsMod.getColor(colorI);
            String name = EnumDyeColor.byDyeDamage(i).getName();
            BlockColoredChest block = new BlockColoredChest(color, name);
            BLOCKS.add(block);
        }

        Block[] blocks = {};
        blocks = BLOCKS.toArray(blocks);

        registry.registerAll(blocks);

        GameRegistry.registerTileEntity(TileEntityColoredChest.class, new ResourceLocation(ColoredChestsMod.MOD_ID, "coloredchesttileentity"));
    }

    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();


        for(Block block : BLOCKS) {
            ITEMBLOCKS.add(new ItemBlock(block));
        }

        ItemBlock[] items = {};
        items = ITEMBLOCKS.toArray(items);

        for (final ItemBlock item : items) {
            final Block block = item.getBlock();
            final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
            registry.register(item.setRegistryName(registryName));
        }
    }

    public static Color getColor(int rgb) {
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    public static int getRGB(Color color) {
        int rgb = color.getRed();
        rgb = (rgb << 8) + color.getGreen();
        rgb = (rgb << 8) + color.getBlue();
        return rgb;
    }

    public static boolean doColorsMatch(Color a, Color b) {
        return a == b || a != null && b != null && a.getRGB() == b.getRGB();
    }
}
