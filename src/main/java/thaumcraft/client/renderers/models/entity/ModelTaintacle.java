// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.entity;

import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public class ModelTaintacle extends ModelBase
{
    public ModelRenderer tentacle;
    public ModelRenderer[] tents;
    public ModelRenderer orb;
    private int length;
    private boolean seed;
    
    public ModelTaintacle(final int length, final boolean seed) {
        this.tentacle = new ModelRendererTaintacle(this);
        this.orb = new ModelRendererTaintacle(this);
        this.length = 10;
        this.seed = false;
        this.seed = seed;
        final int var3 = 0;
        this.length = length;
        this.textureHeight = 64;
        this.textureWidth = 64;
        (this.tentacle = new ModelRendererTaintacle(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        this.tentacle.rotationPointX = 0.0f;
        this.tentacle.rotationPointZ = 0.0f;
        this.tentacle.rotationPointY = 12.0f;
        this.tents = new ModelRendererTaintacle[length];
        for (int k = 0; k < length - 1; ++k) {
            (this.tents[k] = new ModelRendererTaintacle(this, 0, 16)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
            this.tents[k].rotationPointY = -8.0f;
            if (k == 0) {
                this.tentacle.addChild(this.tents[k]);
            }
            else {
                this.tents[k - 1].addChild(this.tents[k]);
            }
        }
        if (!seed) {
            (this.orb = new ModelRendererTaintacle(this, 0, 56)).addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4);
            this.orb.rotationPointY = -8.0f;
            this.tents[length - 2].addChild(this.orb);
            (this.tents[length - 1] = new ModelRendererTaintacle(this, 0, 32)).addBox(-6.0f, -6.0f, -6.0f, 12, 12, 12);
            this.tents[length - 1].rotationPointY = -8.0f;
            this.tents[length - 2].addChild(this.tents[length - 1]);
        }
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity entity) {
        float flail = 0.0f;
        float ht = 0.0f;
        final int at = 0;
        if (entity instanceof EntityTaintacle) {
            final EntityTaintacle tentacle = (EntityTaintacle)entity;
            flail = tentacle.flailIntensity;
            ht = (float)tentacle.hurtTime;
            final float mod = par6 * 0.2f;
            final float fs = (flail > 1.0f) ? 3.0f : (1.0f + ((flail > 1.0f) ? mod : (-mod)));
            final float fi = flail + ((ht > 0.0f || at > 0) ? mod : (-mod));
            this.tentacle.rotateAngleX = 0.0f;
            for (int k = 0; k < this.length - 1; ++k) {
                this.tents[k].rotateAngleX = 0.15f * fi * MathHelper.sin(par3 * 0.1f * fs - k / 2.0f);
                this.tents[k].rotateAngleZ = 0.1f / fi * MathHelper.sin(par3 * 0.15f - k / 2.0f);
            }
        }
        if (entity instanceof EntityTaintSeed) {
            final EntityTaintSeed seed = (EntityTaintSeed)entity;
            ht = seed.hurtTime / 200.0f;
            flail = 0.1f;
            final float mod = par6 * 0.2f;
            final float fs = (flail > 1.0f) ? 3.0f : (1.0f + ((flail > 1.0f) ? mod : (-mod)));
            float fi = flail + ((ht > 0.0f || at > 0) ? mod : (-mod));
            fi *= 3.0f;
            this.tentacle.rotateAngleX = 0.0f;
            for (int k = 0; k < this.length - 1; ++k) {
                this.tents[k].rotateAngleX = 0.2f + 0.01f * k * k + ht + seed.attackAnim;
                this.tents[k].rotateAngleZ = 0.1f / fi * MathHelper.sin(par3 * 0.05f - k / 2.0f) / 5.0f;
            }
        }
    }
    
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (par1Entity instanceof EntityTaintSeed) {
            GL11.glTranslatef(0.0f, 1.0f, -0.2f);
            GL11.glScalef(par1Entity.width * 0.6f, par1Entity.height, par1Entity.width * 0.6f);
            ((ModelRendererTaintacle)this.tentacle).render(par7, this.seed ? 0.82f : 0.85f);
        }
        else {
            float height = 0.0f;
            final float hc = par1Entity.height * 10.0f;
            if (par1Entity.ticksExisted < hc) {
                height = (hc - par1Entity.ticksExisted) / hc * par1Entity.height;
            }
            GL11.glTranslatef(0.0f, ((par1Entity.height == 3.0f) ? 0.6f : 1.2f) + height, 0.0f);
            GL11.glScalef(par1Entity.height / 3.0f, par1Entity.height / 3.0f, par1Entity.height / 3.0f);
            ((ModelRendererTaintacle)this.tentacle).render(par7, 0.88f);
        }
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
