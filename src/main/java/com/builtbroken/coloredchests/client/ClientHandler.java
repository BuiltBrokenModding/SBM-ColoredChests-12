package com.builtbroken.coloredchests.client;

import com.builtbroken.coloredchests.ColoredChestsMod;
import com.builtbroken.coloredchests.chest.TileEntityColoredChest;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = ColoredChestsMod.MOD_ID, value = Side.CLIENT)
public class ClientHandler {
    
    public static final ModelResourceLocation mrl = new ModelResourceLocation(ColoredChestsMod.MOD_ID + ":coloredchest");
    
    @SubscribeEvent
    public static void modelReg(ModelRegistryEvent event) {
        for(ItemBlock itemBlock : ColoredChestsMod.ITEMBLOCKS) {
            itemBlock.setTileEntityItemStackRenderer(new TileEntityItemStackRendererColoredChest());
            ModelLoader.setCustomModelResourceLocation(itemBlock, 0, mrl);
        }
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColoredChest.class, new RenderChest());
    }
    
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        IBakedModel model = event.getModelRegistry().getObject(mrl);
        event.getModelRegistry().putObject(mrl, new ChestItemModelWrapper(model));
    }
    
    public static void initModel(Block block, int meta) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }
    
}
