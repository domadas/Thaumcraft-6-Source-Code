// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import net.minecraft.world.World;
import thaumcraft.api.casters.NodeSetting;
import java.util.Iterator;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;

public class FocusEffectCurse extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSCURSE";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.CURSE";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.DEATH;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("duration") + getSettingValue("power") * 3;
    }
    
    @Override
    public float getDamageForDisplay(final float finalPower) {
        return (1.0f + getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(final RayTraceResult target, final Trajectory trajectory, final float finalPower, final int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockBamf(target.hitVec.x, target.hitVec.y, target.hitVec.z, 6946821, true, true, null), new NetworkRegistry.TargetPoint(getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            final float damage = getDamageForDisplay(finalPower);
            final int duration = 20 * getSettingValue("duration");
            int eff = (int)(getSettingValue("power") * finalPower / 2.0f);
            if (eff < 0) {
                eff = 0;
            }
            target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage((target.entityHit != null) ? target.entityHit : getPackage().getCaster(), getPackage().getCaster()), damage);
            if (target.entityHit instanceof EntityLivingBase) {
                ((EntityLivingBase)target.entityHit).addPotionEffect(new PotionEffect(MobEffects.POISON, duration, Math.round((float)eff)));
                float c = 0.85f;
                if (getPackage().world.rand.nextFloat() < c) {
                    ((EntityLivingBase)target.entityHit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.rand.nextFloat() < c) {
                    ((EntityLivingBase)target.entityHit).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, duration, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.rand.nextFloat() < c) {
                    ((EntityLivingBase)target.entityHit).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, duration * 2, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.rand.nextFloat() < c) {
                    ((EntityLivingBase)target.entityHit).addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration * 3, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.rand.nextFloat() < c) {
                    ((EntityLivingBase)target.entityHit).addPotionEffect(new PotionEffect(MobEffects.UNLUCK, duration * 3, Math.round((float)eff)));
                }
            }
        }
        else if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            final float f = (float)Math.min(8.0, 1.5 * getSettingValue("power") * finalPower);
            for (final BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.getAllInBoxMutable(target.getBlockPos().add(-f, -f, -f), target.getBlockPos().add(f, f, f))) {
                if (blockpos$mutableblockpos1.distanceSqToCenter(target.hitVec.x, target.hitVec.y, target.hitVec.z) <= f * f && getPackage().world.isAirBlock(blockpos$mutableblockpos1.up()) && getPackage().world.isBlockFullCube(blockpos$mutableblockpos1)) {
                    getPackage().world.setBlockState(blockpos$mutableblockpos1.up(), BlocksTC.effectSap.getDefaultState());
                }
            }
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(1, 10)) };
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(final World world, final double posX, final double posY, final double posZ, final double motionX, final double motionY, final double motionZ) {
        final FXGeneric fb = new FXGeneric(world, posX, posY, posZ, motionX, motionY, motionZ);
        fb.setMaxAge(8);
        fb.setRBGColorF(0.41f + world.rand.nextFloat() * 0.2f, 0.0f, 0.019f + world.rand.nextFloat() * 0.2f);
        fb.setAlphaF(0.0f, world.rand.nextFloat(), world.rand.nextFloat(), world.rand.nextFloat(), 0.0f);
        fb.setGridSize(16);
        fb.setParticles(72 + world.rand.nextInt(4), 1, 1);
        fb.setScale(2.0f + world.rand.nextFloat() * 4.0f);
        fb.setLoop(false);
        fb.setSlowDown(0.9);
        fb.setGravity(0.0f);
        fb.setRotationSpeed(world.rand.nextFloat(), 0.0f);
        ParticleEngine.addEffectWithDelay(world, fb, world.rand.nextInt(4));
    }
    
    @Override
    public void onCast(final Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.PLAYERS, 0.15f, 1.0f + caster.getEntityWorld().rand.nextFloat() / 2.0f);
    }
}
