// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import thaumcraft.common.lib.utils.Utils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXGeneric extends Particle
{
    boolean doneFrames;
    boolean flipped;
    double windX;
    double windZ;
    int layer;
    float dr;
    float dg;
    float db;
    boolean loop;
    float rotationSpeed;
    int startParticle;
    int numParticles;
    int particleInc;
    float[] scaleKeys;
    float[] scaleFrames;
    float[] alphaKeys;
    float[] alphaFrames;
    double slowDown;
    float randomX;
    float randomY;
    float randomZ;
    int[] finalFrames;
    boolean angled;
    float angleYaw;
    float anglePitch;
    int gridSize;
    
    public FXGeneric(final World world, final double x, final double y, final double z, final double xx, final double yy, final double zz) {
        super(world, x, y, z, xx, yy, zz);
        doneFrames = false;
        flipped = false;
        layer = 0;
        dr = 0.0f;
        dg = 0.0f;
        db = 0.0f;
        loop = false;
        rotationSpeed = 0.0f;
        startParticle = 0;
        numParticles = 1;
        particleInc = 1;
        scaleKeys = new float[] { 1.0f };
        scaleFrames = new float[] { 0.0f };
        alphaKeys = new float[] { 1.0f };
        alphaFrames = new float[] { 0.0f };
        slowDown = 0.9800000190734863;
        finalFrames = null;
        angled = false;
        gridSize = 64;
        setSize(0.1f, 0.1f);
        setPosition(x, y, z);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        particleTextureJitterX = 0.0f;
        particleTextureJitterY = 0.0f;
        motionX = xx;
        motionY = yy;
        motionZ = zz;
    }
    
    public FXGeneric(final World world, final double x, final double y, final double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        doneFrames = false;
        flipped = false;
        layer = 0;
        dr = 0.0f;
        dg = 0.0f;
        db = 0.0f;
        loop = false;
        rotationSpeed = 0.0f;
        startParticle = 0;
        numParticles = 1;
        particleInc = 1;
        scaleKeys = new float[] { 1.0f };
        scaleFrames = new float[] { 0.0f };
        alphaKeys = new float[] { 1.0f };
        alphaFrames = new float[] { 0.0f };
        slowDown = 0.9800000190734863;
        finalFrames = null;
        angled = false;
        gridSize = 64;
        setSize(0.1f, 0.1f);
        setPosition(x, y, z);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        particleTextureJitterX = 0.0f;
        particleTextureJitterY = 0.0f;
    }
    
    void calculateFrames() {
        doneFrames = true;
        if (alphaKeys == null) {
            setAlphaF(1.0f);
        }
        alphaFrames = new float[particleMaxAge + 1];
        float inc = (alphaKeys.length - 1) / (float) particleMaxAge;
        float is = 0.0f;
        for (int a = 0; a <= particleMaxAge; ++a) {
            final int isF = MathHelper.floor(is);
            float diff = (isF < alphaKeys.length - 1) ? (diff = alphaKeys[isF + 1] - alphaKeys[isF]) : 0.0f;
            final float pa = is - isF;
            alphaFrames[a] = alphaKeys[isF] + diff * pa;
            is += inc;
        }
        if (scaleKeys == null) {
            setScale(1.0f);
        }
        scaleFrames = new float[particleMaxAge + 1];
        inc = (scaleKeys.length - 1) / (float) particleMaxAge;
        is = 0.0f;
        for (int a = 0; a <= particleMaxAge; ++a) {
            final int isF = MathHelper.floor(is);
            float diff = (isF < scaleKeys.length - 1) ? (diff = scaleKeys[isF + 1] - scaleKeys[isF]) : 0.0f;
            final float pa = is - isF;
            scaleFrames[a] = scaleKeys[isF] + diff * pa;
            is += inc;
        }
    }
    
    public void onUpdate() {
        if (!doneFrames) {
            calculateFrames();
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
        prevParticleAngle = particleAngle;
        particleAngle += 3.1415927f * rotationSpeed * 2.0f;
        motionY -= 0.04 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= slowDown;
        motionY *= slowDown;
        motionZ *= slowDown;
        motionX += world.rand.nextGaussian() * randomX;
        motionY += world.rand.nextGaussian() * randomY;
        motionZ += world.rand.nextGaussian() * randomZ;
        motionX += windX;
        motionZ += windZ;
        if (onGround && slowDown != 1.0) {
            motionX *= 0.699999988079071;
            motionZ *= 0.699999988079071;
        }
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        if (loop) {
            setParticleTextureIndex(startParticle + particleAge / particleInc % numParticles);
        }
        else {
            final float fs = particleAge / (float) particleMaxAge;
            setParticleTextureIndex((int)(startParticle + Math.min(numParticles * fs, (float)(numParticles - 1))));
        }
        if (finalFrames != null && finalFrames.length > 0 && particleAge > particleMaxAge - finalFrames.length) {
            int frame = particleMaxAge - particleAge;
            if (frame < 0) {
                frame = 0;
            }
            setParticleTextureIndex(finalFrames[frame]);
        }
        particleAlpha = ((alphaFrames.length <= 0) ? 0.0f : alphaFrames[Math.min(particleAge, alphaFrames.length - 1)]);
        particleScale = ((scaleFrames.length <= 0) ? 0.0f : scaleFrames[Math.min(particleAge, scaleFrames.length - 1)]);
        draw(wr, entity, f, f1, f2, f3, f4, f5);
    }
    
    public boolean isFlipped() {
        return flipped;
    }
    
    public void setFlipped(final boolean flip) {
        flipped = flip;
    }
    
    public void draw(final BufferBuilder wr, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        float tx1 = particleTextureIndexX / (float) gridSize;
        float tx2 = tx1 + 1.0f / gridSize;
        float ty1 = particleTextureIndexY / (float) gridSize;
        float ty2 = ty1 + 1.0f / gridSize;
        final float ts = 0.1f * particleScale;
        if (particleTexture != null) {
            tx1 = particleTexture.getMinU();
            tx2 = particleTexture.getMaxU();
            ty1 = particleTexture.getMinV();
            ty2 = particleTexture.getMaxV();
        }
        if (flipped) {
            final float t = tx1;
            tx1 = tx2;
            tx2 = t;
        }
        final float fs = MathHelper.clamp((particleAge + partialTicks) / particleMaxAge, 0.0f, 1.0f);
        final float pr = particleRed + (dr - particleRed) * fs;
        final float pg = particleGreen + (dg - particleGreen) * fs;
        final float pb = particleBlue + (db - particleBlue) * fs;
        final int i = getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float f5 = (float)(prevPosX + (posX - prevPosX) * partialTicks - FXGeneric.interpPosX);
        final float f6 = (float)(prevPosY + (posY - prevPosY) * partialTicks - FXGeneric.interpPosY);
        final float f7 = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks - FXGeneric.interpPosZ);
        if (angled) {
            Tessellator.getInstance().draw();
            GL11.glPushMatrix();
            GL11.glTranslated(f5, f6, f7);
            GL11.glRotatef(-angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(anglePitch + 90.0f, 1.0f, 0.0f, 0.0f);
            if (particleAngle != 0.0f) {
                final float f8 = particleAngle + (particleAngle - prevParticleAngle) * partialTicks;
                GL11.glRotated(f8 * 57.29577951308232, 0.0, 0.0, 1.0);
            }
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            wr.pos(-ts, -ts, 0.0).tex(tx2, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(-ts, ts, 0.0).tex(tx2, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(ts, ts, 0.0).tex(tx1, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(ts, -ts, 0.0).tex(tx1, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            Tessellator.getInstance().draw();
            GL11.glPopMatrix();
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
        else {
            final Vec3d[] avec3d = { new Vec3d(-rotationX * ts - rotationXY * ts, -rotationZ * ts, -rotationYZ * ts - rotationXZ * ts), new Vec3d(-rotationX * ts + rotationXY * ts, rotationZ * ts, -rotationYZ * ts + rotationXZ * ts), new Vec3d(rotationX * ts + rotationXY * ts, rotationZ * ts, rotationYZ * ts + rotationXZ * ts), new Vec3d(rotationX * ts - rotationXY * ts, -rotationZ * ts, rotationYZ * ts - rotationXZ * ts) };
            if (particleAngle != 0.0f) {
                final float f9 = particleAngle + (particleAngle - prevParticleAngle) * partialTicks;
                final float f10 = MathHelper.cos(f9 * 0.5f);
                final float f11 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.x;
                final float f12 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.y;
                final float f13 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.z;
                final Vec3d vec3d = new Vec3d(f11, f12, f13);
                for (int l = 0; l < 4; ++l) {
                    avec3d[l] = vec3d.scale(2.0 * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale(f10 * f10 - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale(2.0f * f10));
                }
            }
            wr.pos(f5 + avec3d[0].x, f6 + avec3d[0].y, f7 + avec3d[0].z).tex(tx2, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[1].x, f6 + avec3d[1].y, f7 + avec3d[1].z).tex(tx2, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[2].x, f6 + avec3d[2].y, f7 + avec3d[2].z).tex(tx1, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[3].x, f6 + avec3d[3].y, f7 + avec3d[3].z).tex(tx1, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
        }
    }
    
    public void setWind(final double d) {
        final int m = world.getMoonPhase();
        final Vec3d vsource = new Vec3d(0.0, 0.0, 0.0);
        Vec3d vtar = new Vec3d(0.1, 0.0, 0.0);
        vtar = Utils.rotateAroundY(vtar, m * (40 + world.rand.nextInt(10)) / 180.0f * 3.1415927f);
        final Vec3d vres = vsource.addVector(vtar.x, vtar.y, vtar.z);
        windX = vres.x * d;
        windZ = vres.z * d;
    }
    
    public void setLayer(final int layer) {
        this.layer = layer;
    }
    
    public void setRBGColorF(final float particleRedIn, final float particleGreenIn, final float particleBlueIn) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        dr = particleRedIn;
        dg = particleGreenIn;
        db = particleBlueIn;
    }
    
    public void setRBGColorF(final float particleRedIn, final float particleGreenIn, final float particleBlueIn, final float r2, final float g2, final float b2) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        dr = r2;
        dg = g2;
        db = b2;
    }
    
    public int getFXLayer() {
        return layer;
    }
    
    public void setLoop(final boolean loop) {
        this.loop = loop;
    }
    
    public void setRotationSpeed(final float rot) {
        rotationSpeed = (float)(rot * 0.017453292519943);
    }
    
    public void setRotationSpeed(final float start, final float rot) {
        particleAngle = (float)(start * 3.141592653589793 * 2.0);
        rotationSpeed = (float)(rot * 0.017453292519943);
    }
    
    public void setMaxAge(final int max) {
        particleMaxAge = max;
    }
    
    public void setParticles(final int startParticle, final int numParticles, final int particleInc) {
        this.numParticles = numParticles;
        this.particleInc = particleInc;
        setParticleTextureIndex(this.startParticle = startParticle);
    }
    
    public void setParticle(final int startParticle) {
        numParticles = 1;
        particleInc = 1;
        setParticleTextureIndex(this.startParticle = startParticle);
    }
    
    public void setScale(final float... scale) {
        particleScale = scale[0];
        scaleKeys = scale;
    }
    
    public void setAlphaF(final float... a1) {
        super.setAlphaF(a1[0]);
        alphaKeys = a1;
    }
    
    public void setAlphaF(final float a1) {
        super.setAlphaF(a1);
        (alphaKeys = new float[1])[0] = a1;
    }
    
    public void setSlowDown(final double slowDown) {
        this.slowDown = slowDown;
    }
    
    public void setRandomMovementScale(final float x, final float y, final float z) {
        randomX = x;
        randomY = y;
        randomZ = z;
    }
    
    public void setFinalFrames(final int... frames) {
        finalFrames = frames;
    }
    
    public void setAngles(final float yaw, final float pitch) {
        angleYaw = yaw;
        anglePitch = pitch;
        angled = true;
    }
    
    public void setGravity(final float g) {
        particleGravity = g;
    }
    
    public void setParticleTextureIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        particleTextureIndexX = index % gridSize;
        particleTextureIndexY = index / gridSize;
    }
    
    public void setGridSize(final int gridSize) {
        this.gridSize = gridSize;
    }
    
    public void setNoClip(final boolean clip) {
        canCollide = clip;
    }
}
