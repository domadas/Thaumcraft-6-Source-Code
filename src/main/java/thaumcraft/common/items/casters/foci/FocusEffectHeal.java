// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;

public class FocusEffectHeal extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSHEAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.HEAL";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.LIFE;
    }
    
    @Override
    public int getComplexity() {
        return this.getSettingValue("power") * 4;
    }
    
    @Override
    public float getDamageForDisplay(final float finalPower) {
        return -this.getSettingValue("power") * finalPower;
    }
    
    @Override
    public boolean execute(final RayTraceResult target, final Trajectory trajectory, final float finalPower, final int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.hitVec.x, target.hitVec.y, target.hitVec.z, new String[] { this.getKey() }), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null && target.entityHit instanceof EntityLivingBase) {
            if (((EntityLivingBase)target.entityHit).isEntityUndead()) {
                target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.getPackage().getCaster(), this.getPackage().getCaster()), this.getSettingValue("power") * finalPower * 1.5f);
            }
            else {
                ((EntityLivingBase)target.entityHit).heal(this.getSettingValue("power") * finalPower);
            }
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.heal.power", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @Override
    public void onCast(final Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.PLAYERS, 2.0f, 2.0f + (float)(caster.world.rand.nextGaussian() * 0.10000000149011612));
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(final World world, final double x, final double y, final double z, final double vx, final double vy, final double vz) {
        final FXGeneric fb = new FXGeneric(world, x, y, z, vx + world.rand.nextGaussian() * 0.01, vy + world.rand.nextGaussian() * 0.01, vz + world.rand.nextGaussian() * 0.01);
        fb.setMaxAge((int)(10.0f + 10.0f * world.rand.nextFloat()));
        fb.setRBGColorF(1.0f, 1.0f, 1.0f);
        fb.setAlphaF(0.0f, 0.7f, 0.7f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(0, 1, 1);
        fb.setScale(world.rand.nextFloat() * 2.0f, world.rand.nextFloat());
        fb.setSlowDown(0.8);
        fb.setGravity((float)(world.rand.nextGaussian() * 0.10000000149011612));
        fb.setRandomMovementScale(0.0125f, 0.0125f, 0.0125f);
        fb.setRotationSpeed((float)world.rand.nextGaussian());
        ParticleEngine.addEffectWithDelay(world, fb, world.rand.nextInt(4));
    }
}
