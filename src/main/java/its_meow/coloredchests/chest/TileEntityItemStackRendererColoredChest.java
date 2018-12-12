package its_meow.coloredchests.chest;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class TileEntityItemStackRendererColoredChest extends TileEntityItemStackRenderer {
	
	private final ModelChest chest = new ModelChest();
	private TileEntityColoredChest chestT = null;
	
	@Override
	public void renderByItem(ItemStack itemStackIn, float partialTicks) {
		BlockColoredChest block = ((BlockColoredChest)BlockColoredChest.getBlockFromItem(itemStackIn.getItem()));
		if(block != null && block.color != null)
		{
			if(chestT == null) {
				chestT = new TileEntityColoredChest(block.color);
			}
			TileEntityRendererDispatcher.instance.render(chestT, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
			/*GlStateManager.pushMatrix();
			float r = (float)block.color.getRed() / 255f;
			float g = (float)block.color.getGreen() / 255f;
			float b = (float)block.color.getBlue() / 255f;
			GL11.glColor3f(r, g, b);
			Minecraft.getMinecraft().getTextureManager().bindTexture(RenderChest.TEXTURE_NORMAL);
			this.chest.renderAll();
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();*/
		}
		//super.renderByItem(itemStackIn);
	}



}
