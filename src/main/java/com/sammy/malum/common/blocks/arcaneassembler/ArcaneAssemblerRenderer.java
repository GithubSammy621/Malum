package com.sammy.malum.common.blocks.arcaneassembler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sammy.malum.common.blocks.itempedestal.ItemPedestalTileEntity;
import com.sammy.malum.common.items.SpiritItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;


public class ArcaneAssemblerRenderer extends TileEntityRenderer<ArcaneAssemblerTileEntity>
{
    public ArcaneAssemblerRenderer(Object rendererDispatcherIn)
    {
        super((TileEntityRendererDispatcher) rendererDispatcherIn);
    }

    @Override
    public void render(ArcaneAssemblerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        World world = Minecraft.getInstance().world;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = tileEntityIn.inventory.getStackInSlot(0);
        if (!stack.isEmpty())
        {
            matrixStackIn.push();
            Vector3f offset = new Vector3f(ArcaneAssemblerTileEntity.itemOffset(tileEntityIn, stack.getItem()));
            matrixStackIn.translate(offset.getX(), offset.getY(), offset.getZ());
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((world.getGameTime() % 360) * 3 + partialTicks));
            matrixStackIn.scale(0.6f, 0.6f, 0.6f);
            itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, NO_OVERLAY, matrixStackIn, bufferIn);
            matrixStackIn.pop();
        }
    }
}