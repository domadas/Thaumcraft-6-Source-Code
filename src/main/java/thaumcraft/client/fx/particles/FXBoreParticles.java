// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;

@SideOnly(Side.CLIENT)
public class FXBoreParticles extends Particle
{
    private IBlockState blockInstance;
    private ItemStack itemInstance;
    private int side;
    private Entity target;
    private double targetX;
    private double targetY;
    private double targetZ;
    
    public FXBoreParticles(final World par1World, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz, final IBlockState par14Block, final int par15) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        blockInstance = par14Block;
        try {
            setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Item.getItemFromBlock(par14Block.getBlock()), par15));
        }
        catch (final Exception e) {
            setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Item.getItemFromBlock(Blocks.STONE), 0));
            particleMaxAge = 0;
        }
        particleGravity = par14Block.getBlock().blockParticleGravity;
        final float particleRed = 0.6f;
        particleBlue = particleRed;
        particleGreen = particleRed;
        this.particleRed = particleRed;
        particleScale = rand.nextFloat() * 0.3f + 0.4f;
        side = par15;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        final double dx = tx - posX;
        final double dy = ty - posY;
        final double dz = tz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 10.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base / 2 + rand.nextInt(base);
        final float f3 = 0.01f;
        motionX = (float) world.rand.nextGaussian() * f3;
        motionY = (float) world.rand.nextGaussian() * f3;
        motionZ = (float) world.rand.nextGaussian() * f3;
        particleGravity = 0.01f;
    }
    
    public FXBoreParticles(final World par1World, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz, final double sx, final double sy, final double sz, final ItemStack item) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        itemInstance = item;
        setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item.getItem(), item.getItemDamage()));
        particleGravity = Blocks.SNOW_LAYER.blockParticleGravity;
        final float particleRed = 0.6f;
        particleBlue = particleRed;
        particleGreen = particleRed;
        this.particleRed = particleRed;
        particleScale = rand.nextFloat() * 0.3f + 0.4f;
        side = 0;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        final double dx = tx - posX;
        final double dy = ty - posY;
        final double dz = tz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 10.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base / 2 + rand.nextInt(base);
        final float f3 = 0.01f;
        motionX = sx + (float) world.rand.nextGaussian() * f3;
        motionY = sy + (float) world.rand.nextGaussian() * f3;
        motionZ = sz + (float) world.rand.nextGaussian() * f3;
        particleGravity = 0.01f;
        final Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
        int visibleDistance = 64;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics) {
            visibleDistance = 32;
        }
        if (renderentity.getDistance(posX, posY, posZ) > visibleDistance) {
            particleMaxAge = 0;
        }
    }
    
    public void setTarget(final Entity target) {
        this.target = target;
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (target != null) {
            targetX = target.posX;
            targetY = target.posY + target.getEyeHeight();
            targetZ = target.posZ;
        }
        if (particleAge++ >= particleMaxAge || (MathHelper.floor(posX) == MathHelper.floor(targetX) && MathHelper.floor(posY) == MathHelper.floor(targetY) && MathHelper.floor(posZ) == MathHelper.floor(targetZ))) {
            setExpired();
            return;
        }
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.95;
        motionZ *= 0.985;
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        final double d11 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        final double clamp = Math.min(0.25, d11 / 15.0);
        if (d11 < 2.0) {
            particleScale *= 0.9f;
        }
        dx /= d11;
        dy /= d11;
        dz /= d11;
        motionX += dx * clamp;
        motionY += dy * clamp;
        motionZ += dz * clamp;
        motionX = MathHelper.clamp((float) motionX, -clamp, clamp);
        motionY = MathHelper.clamp((float) motionY, -clamp, clamp);
        motionZ = MathHelper.clamp((float) motionZ, -clamp, clamp);
        motionX += rand.nextGaussian() * 0.005;
        motionY += rand.nextGaussian() * 0.005;
        motionZ += rand.nextGaussian() * 0.005;
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public FXBoreParticles getObjectColor(final BlockPos pos) {
        if (blockInstance == null || world.getBlockState(pos) != blockInstance) {
            try {
                final int var4 = Minecraft.getMinecraft().getItemColors().colorMultiplier(itemInstance, 0);
                particleRed *= (var4 >> 16 & 0xFF) / 255.0f;
                particleGreen *= (var4 >> 8 & 0xFF) / 255.0f;
                particleBlue *= (var4 & 0xFF) / 255.0f;
            }
            catch (final Exception ex) {}
            return this;
        }
        if (blockInstance == Blocks.GRASS && side != 1) {
            return this;
        }
        try {
            final int var4 = Minecraft.getMinecraft().getBlockColors().colorMultiplier(blockInstance, world, pos, 0);
            particleRed *= (var4 >> 16 & 0xFF) / 255.0f;
            particleGreen *= (var4 >> 8 & 0xFF) / 255.0f;
            particleBlue *= (var4 & 0xFF) / 255.0f;
        }
        catch (final Exception ex2) {}
        return this;
    }
    
    public void renderParticle(final BufferBuilder p_180434_1_, final Entity p_180434_2_, final float p_180434_3_, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        float f6 = (particleTextureIndexX + particleTextureJitterX / 4.0f) / 16.0f;
        float f7 = f6 + 0.015609375f;
        float f8 = (particleTextureIndexY + particleTextureJitterY / 4.0f) / 16.0f;
        float f9 = f8 + 0.015609375f;
        final float f10 = 0.1f * particleScale;
        if (particleTexture != null) {
            f6 = particleTexture.getInterpolatedU(particleTextureJitterX / 4.0f * 16.0f);
            f7 = particleTexture.getInterpolatedU((particleTextureJitterX + 1.0f) / 4.0f * 16.0f);
            f8 = particleTexture.getInterpolatedV(particleTextureJitterY / 4.0f * 16.0f);
            f9 = particleTexture.getInterpolatedV((particleTextureJitterY + 1.0f) / 4.0f * 16.0f);
        }
        final int i = getBrightnessForRender(p_180434_3_);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float f11 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - FXBoreParticles.interpPosX);
        final float f12 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - FXBoreParticles.interpPosY);
        final float f13 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - FXBoreParticles.interpPosZ);
        p_180434_1_.pos(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10).tex(f6, f9).color(particleRed, particleGreen, particleBlue, 1.0f).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10).tex(f6, f8).color(particleRed, particleGreen, particleBlue, 1.0f).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 + p_180434_6_ * f10 + p_180434_8_ * f10).tex(f7, f8).color(particleRed, particleGreen, particleBlue, 1.0f).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 + p_180434_6_ * f10 - p_180434_8_ * f10).tex(f7, f9).color(particleRed, particleGreen, particleBlue, 1.0f).lightmap(j, k).endVertex();
    }
}
