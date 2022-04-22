// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.container.ContainerPotionSprayer;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiPotionSprayer extends GuiContainer
{
    private TilePotionSprayer inventory;
    private ContainerPotionSprayer container;
    private EntityPlayer player;
    ResourceLocation tex;
    int startAspect;
    
    public GuiPotionSprayer(final InventoryPlayer par1InventoryPlayer, final TilePotionSprayer tilePotionSprayer) {
        super(new ContainerPotionSprayer(par1InventoryPlayer, tilePotionSprayer));
        container = null;
        player = null;
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_potion_sprayer.png");
        startAspect = 0;
        xSize = 192;
        ySize = 233;
        inventory = tilePotionSprayer;
        container = (ContainerPotionSprayer) inventorySlots;
        player = par1InventoryPlayer.player;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int mx, final int my) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        final int k = (width - xSize) / 2;
        final int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        if (inventory.charges > 0) {
            final Color c = new Color(inventory.color);
            GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
            final int scroll = player.ticksExisted % 256;
            drawTexturedModalRect(k + 128, l + 36 + (8 - inventory.charges) * 9, 232, scroll, 8, inventory.charges * 9);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        drawAspects(k, l);
        mc.renderEngine.bindTexture(tex);
        drawTexturedModalRect(k + 125, l + 28, 205, 28, 14, 88);
    }
    
    private void drawAspects(final int k, final int l) {
        int pos = 0;
        for (final Aspect aspect : inventory.recipe.getAspectsSortedByName()) {
            GL11.glPushMatrix();
            GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f);
            drawTexturedModalRect(k + 96 + 22 * (pos % 2), l + 46 + 16 * (pos / 2) - 14, 192, 56, 2, 14);
            final int i1 = (int)(inventory.recipeProgress.getAmount(aspect) / (float) inventory.recipe.getAmount(aspect) * 14.0f);
            final Color c = new Color(aspect.getColor());
            GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
            drawTexturedModalRect(k + 96 + 22 * (pos % 2), l + 46 + 16 * (pos / 2) - i1, 192, 56, 2, i1);
            GL11.glPopMatrix();
            ++pos;
        }
        pos = 0;
        for (final Aspect aspect : inventory.recipe.getAspectsSortedByName()) {
            UtilsFX.drawTag(k + 79 + 22 * (pos % 2), l + 31 + 16 * (pos / 2), aspect, (float) inventory.recipe.getAmount(aspect), 0, zLevel);
            ++pos;
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
