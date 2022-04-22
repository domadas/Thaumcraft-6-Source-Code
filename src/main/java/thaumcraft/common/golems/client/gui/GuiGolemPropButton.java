// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import net.minecraft.client.gui.GuiButton;

public class GuiGolemPropButton extends GuiButton
{
    ISealConfigToggles.SealToggle prop;
    static ResourceLocation tex;
    
    public GuiGolemPropButton(final int buttonId, final int x, final int y, final int width, final int height, final String buttonText, final ISealConfigToggles.SealToggle prop) {
        super(buttonId, x, y, width, height, buttonText);
        this.prop = prop;
    }
    
    public void drawButton(final Minecraft mc, final int xx, final int yy, final float partialTicks) {
        if (visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiGolemPropButton.tex);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            hovered = (xx >= x && yy >= y && xx < x + width && yy < y + height);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            drawTexturedModalRect(x - 2, y - 2, 2, 18, 12, 12);
            if (prop.getValue()) {
                drawTexturedModalRect(x - 2, y - 2, 18, 18, 12, 12);
            }
            drawString(fontrenderer, I18n.translateToLocal(displayString), x + 12, y, 16777215);
            mouseDragged(mc, xx, yy);
        }
    }
    
    static {
        GuiGolemPropButton.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    }
}
