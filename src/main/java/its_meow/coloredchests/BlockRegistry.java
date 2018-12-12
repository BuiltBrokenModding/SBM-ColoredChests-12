package its_meow.coloredchests;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import its_meow.coloredchests.chest.BlockColoredChest;
import its_meow.coloredchests.chest.ItemBlockChest;
import its_meow.coloredchests.chest.TileEntityColoredChest;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(Ref.MOD_ID)
public class BlockRegistry {

	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();
		
		public static ArrayList<Block> blocksList = new ArrayList<Block>();

		/**
		 * Register this mod's {@link Block}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			final IForgeRegistry<Block> registry = event.getRegistry();
			
			
			for(int i = 0; i < EnumDyeColor.values().length; i++) {
				int colorI = EnumDyeColor.byDyeDamage(i).getColorValue();
				Color color = ColoredChestsMod.getColor(colorI);
				String name = EnumDyeColor.byDyeDamage(i).getName();
				BlockColoredChest block = new BlockColoredChest(color, name);
				blocksList.add(block);
			}
			
			Block[] blocks = {};
			blocks = blocksList.toArray(blocks);

			registry.registerAll(blocks);

			GameRegistry.registerTileEntity(TileEntityColoredChest.class, new ResourceLocation(Ref.MOD_ID, "coloredchesttileentity"));
		}

		public static ArrayList<ItemBlock> itemsList = new ArrayList<ItemBlock>();

		/**
		 * Register this mod's {@link ItemBlock}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
		
			
			for(int i = 0; i < EnumDyeColor.values().length; i++) {
				int colorI = EnumDyeColor.byDyeDamage(i).getColorValue();
				Color color = ColoredChestsMod.getColor(colorI);
				String name = EnumDyeColor.byDyeDamage(i).getName();
				BlockColoredChest block = new BlockColoredChest(color, name);
				itemsList.add(new ItemBlockChest(block));
			}
			
			ItemBlock[] items = {};
			items = itemsList.toArray(items);
			
			for (final ItemBlock item : items) {
				final Block block = item.getBlock();
				final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
				registry.register(item.setRegistryName(registryName));
				ITEM_BLOCKS.add(item);
			}
		}

		@SubscribeEvent
		public static void registerItemBlockModels(final ModelRegistryEvent event) {
			//initModel(blockChest, 0);
			for(ItemBlock itemBlock : itemsList) {
				ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(Ref.MOD_ID + ":coloredchest"));
				//ForgeHooksClient.registerTESRItemStack(itemBlock, 0, TileEntityColoredChest.class);
			}
		}


		public static void initModel(Block block, int meta) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(block.getRegistryName(), "inventory"));
		}

		public static void initModelOBJ(Block block, int meta) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(block.getRegistryName() + ".obj", "inventory"));
		}

	}
}
