// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.client.particle.Particle;

public class FXSwarm extends Particle
{
    private Entity target;
    private float turnSpeed;
    private float speed;
    int deathtimer;
    private static ArrayList<Long> buzzcount;
    float rotationPitch;
    float rotationYaw;
    public int particle;
    
    public FXSwarm(final World par1World, final double x, final double y, final double z, final Entity target, final float r, final float g, final float b) {
        super(par1World, x, y, z, 0.0, 0.0, 0.0);
        turnSpeed = 10.0f;
        speed = 0.2f;
        deathtimer = 0;
        particle = 40;
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
        particleScale = rand.nextFloat() * 0.5f + 1.0f;
        this.target = target;
        final float f3 = 0.2f;
        motionX = (rand.nextFloat() - rand.nextFloat()) * f3;
        motionY = (rand.nextFloat() - rand.nextFloat()) * f3;
        motionZ = (rand.nextFloat() - rand.nextFloat()) * f3;
        particleGravity = 0.1f;
    }
    
    public FXSwarm(final World par1World, final double x, final double y, final double z, final Entity target, final float r, final float g, final float b, final float sp, final float ts, final float pg) {
        this(par1World, x, y, z, target, r, g, b);
        speed = sp;
        turnSpeed = ts;
        particleGravity = pg;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        final float bob = MathHelper.sin(particleAge / 3.0f) * 0.25f + 1.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        final int part = 7 + particleAge % 8;
        final float var8 = part / 64.0f;
        final float var9 = var8 + 0.015625f;
        final float var10 = 0.0625f;
        final float var11 = var10 + 0.015625f;
        final float var12 = 0.1f * particleScale * bob;
        final float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXSwarm.interpPosX);
        final float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXSwarm.interpPosY);
        final float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXSwarm.interpPosZ);
        final float var16 = 1.0f;
        final float trans = (50.0f - deathtimer) / 50.0f;
        final int i = 240;
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        float dd = 1.0f;
        if (target instanceof EntityLivingBase && ((EntityLivingBase) target).hurtTime > 0) {
            dd = 2.0f;
        }
        wr.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16 / dd, particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
        wr.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16 / dd, particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16 / dd, particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
        wr.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16 / dd, particleBlue * var16 / dd, trans).lightmap(j, k).endVertex();
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        ++particleAge;
        if (target == null || target.isDead || (target instanceof EntityLivingBase && ((EntityLivingBase) target).deathTime > 0)) {
            ++deathtimer;
            motionX *= 0.9;
            motionZ *= 0.9;
            motionY -= particleGravity / 2.0f;
            if (deathtimer > 50) {
                setExpired();
            }
        }
        else {
            motionY += particleGravity;
        }
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        if (target != null && !target.isDead && (!(target instanceof EntityLivingBase) || ((EntityLivingBase) target).deathTime <= 0)) {
            boolean hurt = false;
            if (target instanceof EntityLivingBase) {
                hurt = (((EntityLivingBase) target).hurtTime > 0);
            }
            final Vec3d v1 = new Vec3d(posX, posY, posZ);
            if (v1.squareDistanceTo(target.posX, target.posY, target.posZ) > target.width && !hurt) {
                faceEntity(target, turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f)), turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f)));
            }
            else {
                faceEntity(target, -(turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f))), -(turnSpeed / 2.0f + rand.nextInt((int)(turnSpeed / 2.0f))));
            }
            motionX = -MathHelper.sin(rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(rotationPitch / 180.0f * 3.1415927f);
            motionZ = MathHelper.cos(rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(rotationPitch / 180.0f * 3.1415927f);
            motionY = -MathHelper.sin(rotationPitch / 180.0f * 3.1415927f);
            setHeading(motionX, motionY, motionZ, speed, 15.0f);
        }
        if (FXSwarm.buzzcount.size() < 3 && rand.nextInt(50) == 0 && world.getClosestPlayer(posX, posY, posZ, 8.0, false) != null) {
            world.playSound(posX, posY, posZ, SoundsTC.fly, SoundCategory.HOSTILE, 0.03f, 0.5f + rand.nextFloat() * 0.4f, false);
            FXSwarm.buzzcount.add(System.nanoTime() + 1500000L);
        }
        if (FXSwarm.buzzcount.size() >= 3 && FXSwarm.buzzcount.get(0) < System.nanoTime()) {
            FXSwarm.buzzcount.remove(0);
        }
    }
    
    public void faceEntity(final Entity par1Entity, final float par2, final float par3) {
        final double d0 = par1Entity.posX - posX;
        final double d2 = par1Entity.posZ - posZ;
        final double d3 = (par1Entity.getEntityBoundingBox().minY + par1Entity.getEntityBoundingBox().maxY) / 2.0 - (getBoundingBox().minY + getBoundingBox().maxY) / 2.0;
        final double d4 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        final float f2 = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0f;
        final float f3 = (float)(-(Math.atan2(d3, d4) * 180.0 / 3.141592653589793));
        rotationPitch = updateRotation(rotationPitch, f3, par3);
        rotationYaw = updateRotation(rotationYaw, f2, par2);
    }
    
    private float updateRotation(final float par1, final float par2, final float par3) {
        float f3 = MathHelper.wrapDegrees(par2 - par1);
        if (f3 > par3) {
            f3 = par3;
        }
        if (f3 < -par3) {
            f3 = -par3;
        }
        return par1 + f3;
    }
    
    public void setHeading(double par1, double par3, double par5, final float par7, final float par8) {
        final float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par3 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par5 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        motionX = par1;
        motionY = par3;
        motionZ = par5;
    }
    
    static {
        FXSwarm.buzzcount = new ArrayList<Long>();
    }
}
