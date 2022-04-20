// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.mods;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

public class ChampionModPoison implements IChampionModifierEffect
{
    @Override
    public float performEffect(final EntityLivingBase boss, final EntityLivingBase target, final DamageSource source, final float amount) {
        if (boss.world.rand.nextFloat() < 0.4f) {
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
        }
        return amount;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void showFX(final EntityLivingBase boss) {
        if (boss.world.rand.nextBoolean()) {
            return;
        }
        final float w = boss.world.rand.nextFloat() * boss.width;
        final float d = boss.world.rand.nextFloat() * boss.width;
        final float h = boss.world.rand.nextFloat() * boss.height;
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.0, 0.02, 0.0, 0.2f, 0.6f + boss.world.rand.nextFloat() * 0.1f, 0.2f + boss.world.rand.nextFloat() * 0.1f, 0.7f, false, 579, 4, 1, 8 + boss.world.rand.nextInt(4), 0, 0.5f + boss.world.rand.nextFloat() * 0.2f, 0.5f, 0);
    }
    
    @Override
    public void preRender(final EntityLivingBase boss, final RenderLivingBase renderLivingBase) {
        GL11.glColor4f(0.6f, 1.0f, 0.7f, 1.0f);
    }
}
