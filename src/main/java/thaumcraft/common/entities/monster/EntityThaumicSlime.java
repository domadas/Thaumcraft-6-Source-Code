// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster;

import thaumcraft.common.config.ConfigItems;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.Item;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import net.minecraft.entity.monster.EntitySlime;

public class EntityThaumicSlime extends EntitySlime implements ITaintedMob
{
    int launched;
    int spitCounter;
    
    public EntityThaumicSlime(final World par1World) {
        super(par1World);
        launched = 10;
        spitCounter = 100;
        final int i = 1 << 1 + rand.nextInt(3);
        setSlimeSize(i, true);
    }
    
    public EntityThaumicSlime(final World par1World, final EntityLivingBase par2EntityLiving, final EntityLivingBase par3EntityLiving) {
        super(par1World);
        launched = 10;
        spitCounter = 100;
        setSlimeSize(1, true);
        posY = (par2EntityLiving.getEntityBoundingBox().minY + par2EntityLiving.getEntityBoundingBox().maxY) / 2.0;
        final double var6 = par3EntityLiving.posX - par2EntityLiving.posX;
        final double var7 = par3EntityLiving.getEntityBoundingBox().minY + par3EntityLiving.height / 3.0f - posY;
        final double var8 = par3EntityLiving.posZ - par2EntityLiving.posZ;
        final double var9 = MathHelper.sqrt(var6 * var6 + var8 * var8);
        if (var9 >= 1.0E-7) {
            final float var10 = (float)(Math.atan2(var8, var6) * 180.0 / 3.141592653589793) - 90.0f;
            final float var11 = (float)(-(Math.atan2(var7, var9) * 180.0 / 3.141592653589793));
            final double var12 = var6 / var9;
            final double var13 = var8 / var9;
            setLocationAndAngles(par2EntityLiving.posX + var12, posY, par2EntityLiving.posZ + var13, var10, var11);
            final float var14 = (float)var9 * 0.2f;
            shoot(var6, var7 + var14, var8, 1.5f, 1.0f);
        }
    }
    
    public void shoot(double par1, double par3, double par5, final float par7, final float par8) {
        final float var9 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= var9;
        par3 /= var9;
        par5 /= var9;
        par1 += rand.nextGaussian() * 0.007499999832361937 * par8;
        par3 += rand.nextGaussian() * 0.007499999832361937 * par8;
        par5 += rand.nextGaussian() * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        motionX = par1;
        motionY = par3;
        motionZ = par5;
        final float var10 = MathHelper.sqrt(par1 * par1 + par5 * par5);
        final float n = (float)(Math.atan2(par1, par5) * 180.0 / 3.141592653589793);
        rotationYaw = n;
        prevRotationYaw = n;
        final float n2 = (float)(Math.atan2(par3, var10) * 180.0 / 3.141592653589793);
        rotationPitch = n2;
        prevRotationPitch = n2;
    }
    
    public IEntityLivingData onInitialSpawn(final DifficultyInstance p_180482_1_, final IEntityLivingData p_180482_2_) {
        int i = rand.nextInt(3);
        if (i < 2 && rand.nextFloat() < 0.5f * p_180482_1_.getClampedAdditionalDifficulty()) {
            ++i;
        }
        final int j = 1 << i;
        setSlimeSize(j, true);
        return super.onInitialSpawn(p_180482_1_, p_180482_2_);
    }
    
    public void setSlimeSize(final int par1, final boolean t) {
        super.setSlimeSize(par1, t);
        experienceValue = par1 + 2;
    }
    
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }
    
    public void onUpdate() {
        final int i = getSlimeSize();
        if (onGround && !wasOnGround) {
            wasOnGround = true;
            final float sa = squishAmount;
            super.onUpdate();
            squishAmount = sa;
            if (world.isRemote) {
                for (int j = 0; j < i * 2; ++j) {
                    FXDispatcher.INSTANCE.slimeJumpFX(this, i);
                }
            }
            if (makesSoundOnJump()) {
                playSound(getJumpSound(), getSoundVolume(), ((getRNG().nextFloat() - getRNG().nextFloat()) * 0.2f + 1.0f) * 0.8f);
            }
            squishAmount = -0.5f;
            wasOnGround = onGround;
            alterSquishAmount();
        }
        else {
            super.onUpdate();
        }
        if (world.isRemote) {
            if (launched > 0) {
                --launched;
                for (int k = 0; k < i * (launched + 1); ++k) {
                    FXDispatcher.INSTANCE.slimeJumpFX(this, i);
                }
            }
            final float ff = (float) getSlimeSize();
            setSize(0.6f * ff, 0.6f * ff);
            setSize(0.51000005f * ff, 0.51000005f * ff);
        }
        else if (!isDead) {
            final EntityPlayer entityplayer = world.getClosestPlayerToEntity(this, 16.0);
            if (entityplayer != null) {
                if (spitCounter > 0) {
                    --spitCounter;
                }
                faceEntity(entityplayer, 10.0f, 20.0f);
                if (getDistance(entityplayer) > 4.0f && spitCounter <= 0 && getSlimeSize() > 2) {
                    spitCounter = 101;
                    if (!world.isRemote) {
                        final EntityThaumicSlime flyslime = new EntityThaumicSlime(world, this, entityplayer);
                        world.spawnEntity(flyslime);
                    }
                    playSound(SoundsTC.gore, 1.0f, ((rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                    setSlimeSize(getSlimeSize() - 1, true);
                }
            }
        }
    }
    
    protected EntityThaumicSlime createInstance() {
        return new EntityThaumicSlime(world);
    }
    
    public void setDead() {
        final int i = getSlimeSize();
        if (!world.isRemote && i > 1 && getHealth() <= 0.0f) {
            for (int k = 0; k < i; ++k) {
                final float f = (k % 2 - 0.5f) * i / 4.0f;
                final float f2 = (k / 2 - 0.5f) * i / 4.0f;
                final EntityThaumicSlime entityslime = createInstance();
                entityslime.setSlimeSize(1, true);
                entityslime.setLocationAndAngles(posX + f, posY + 0.5, posZ + f2, rand.nextFloat() * 360.0f, 0.0f);
                world.spawnEntity(entityslime);
            }
        }
        isDead = true;
    }
    
    public boolean getCanSpawnHere() {
        return false;
    }
    
    protected int getAttackStrength() {
        return getSlimeSize() + 1;
    }
    
    protected boolean canDamagePlayer() {
        return true;
    }
    
    protected void dealDamage(final EntityLivingBase p_175451_1_) {
        int i = getSlimeSize();
        if (launched > 0) {
            i += 2;
        }
        if (isEntityAlive() && canEntityBeSeen(p_175451_1_) && getDistanceSq(p_175451_1_) < 0.6 * i * 0.6 * i && p_175451_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getAttackStrength())) {
            playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0f, (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f);
            applyEnchantments(this, p_175451_1_);
        }
    }
    
    protected Item getDropItem() {
        return (getSlimeSize() > 1) ? ItemsTC.crystalEssence : Item.getItemById(0);
    }
    
    protected void dropFewItems(final boolean flag, final int i) {
        if (getSlimeSize() > 1) {
            entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0f);
        }
    }
}
