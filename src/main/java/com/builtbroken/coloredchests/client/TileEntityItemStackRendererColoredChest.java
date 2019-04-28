package com.builtbroken.coloredchests.client;

import java.awt.Color;

import org.lwjgl.util.vector.Vector3f;

import com.builtbroken.coloredchests.chest.BlockColoredChest;
import com.builtbroken.coloredchests.chest.TileEntityColoredChest;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.model.TRSRTransformation;

public class TileEntityItemStackRendererColoredChest extends TileEntityItemStackRenderer {

    private TileEntityColoredChest chestT = null;
    private TileEntityColoredChest chestT2 = null;

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks) {
        BlockColoredChest block = ((BlockColoredChest)Block.getBlockFromItem(itemStackIn.getItem()));
        TileEntity te = null;
        if(block != null && block.color != null)
        {
            if(chestT == null) {
                chestT = new TileEntityColoredChest(block.color);
            }
            te = chestT;
        } else {
            if(chestT2 == null) {
                chestT2 = new TileEntityColoredChest(Color.WHITE);
            }
            te = chestT2;
        }
        GlStateManager.pushMatrix();
        {
            TransformType type = ChestItemModelWrapper.type;
            if(type == TransformType.GUI) {
                GlStateManager.enableLighting();
                GlStateManager.scale(-0.98F, 0.98F, -0.98F);
                GlStateManager.translate(-0.05F, 0.25F, 0F);
                ForgeHooksClient.multiplyCurrentGlMatrix(
                TRSRTransformation.from(
                new ItemTransformVec3f(new Vector3f(-30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.625F, 0.625F, 0.625F))
                ).getMatrix()
                );
            } else if(type == TransformType.GROUND) {
                GlStateManager.scale(0.25F, 0.25F, 0.25F);
                GlStateManager.translate(1.5F, 1.5F, 1.5F);
            } else if(type == TransformType.FIXED) {
                GlStateManager.rotate(180F, 0F, 1F, 1F);
                GlStateManager.rotate(90, 1F, 0F, 0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                GlStateManager.translate(-1.5F, 0.5F, -1.5F);
            } else if(type == TransformType.THIRD_PERSON_RIGHT_HAND) {
                GlStateManager.rotate(1F, 75F, 45F, 0F);
                GlStateManager.translate(0.3F, 0.5F, 0.35F);
                GlStateManager.scale(0.375F, 0.375F, 0.375F);
            } else if(type == TransformType.FIRST_PERSON_RIGHT_HAND) {
                GlStateManager.rotate(45F, 0F, -1F, 0F);
                GlStateManager.translate(0.6F, 0.4F, 0F);
                GlStateManager.scale(0.35F, 0.35F, 0.35F);
            } else if(type == TransformType.FIRST_PERSON_LEFT_HAND) {
                GlStateManager.translate(0.2F, 0.3F, 0.5F);
                GlStateManager.rotate(45F, 0F, 1F, 0F);
                GlStateManager.scale(0.4F, 0.4F, 0.4F);
            } else if(type == TransformType.THIRD_PERSON_LEFT_HAND) {
                GlStateManager.rotate(1F, 75F, 45F, 0F);
                GlStateManager.translate(0.3F, 0.5F, 0.35F);
                GlStateManager.scale(0.375F, 0.375F, 0.375F);
            }

            TileEntityRendererDispatcher.instance.render(te, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        }
        GlStateManager.popMatrix();
    }



}
