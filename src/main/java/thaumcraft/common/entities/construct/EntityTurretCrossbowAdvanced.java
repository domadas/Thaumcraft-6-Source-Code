// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.construct;

import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import com.google.common.base.Predicates;
import net.minecraft.util.EntitySelectors;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.entity.MoverType;
import thaumcraft.Thaumcraft;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.scoreboard.Team;
import thaumcraft.common.lib.utils.Utils;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.math.BlockPos;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.world.World;
import net.minecraft.network.datasync.DataParameter;

public class EntityTurretCrossbowAdvanced extends EntityTurretCrossbow
{
    private static final DataParameter<Byte> FLAGS;
    
    public EntityTurretCrossbowAdvanced(final World worldIn) {
        super(worldIn);
        setSize(0.95f, 1.5f);
        stepHeight = 0.0f;
    }
    
    @Override
    protected void initEntityAI() {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
        tasks.addTask(1, new EntityAIAttackRanged(this, 0.0, 20, 40, 24.0f));
        tasks.addTask(2, new EntityAIWatchTarget(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestValidTarget(this, EntityLivingBase.class, 5, true, false, null));
        setTargetMob(true);
    }
    
    @Override
    public float getEyeHeight() {
        return 1.0f;
    }
    
    public EntityTurretCrossbowAdvanced(final World worldIn, final BlockPos pos) {
        this(worldIn);
        setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register((DataParameter)EntityTurretCrossbowAdvanced.FLAGS, 0);
    }
    
    public boolean canAttackClass(final Class clazz) {
        if (IAnimals.class.isAssignableFrom(clazz) && !IMob.class.isAssignableFrom(clazz) && getTargetAnimal()) {
            return true;
        }
        if (IMob.class.isAssignableFrom(clazz) && getTargetMob()) {
            return true;
        }
        if (!EntityPlayer.class.isAssignableFrom(clazz) || !getTargetPlayer()) {
            return false;
        }
        if (!world.isRemote && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled() && !getTargetFriendly()) {
            setTargetPlayer(false);
            return false;
        }
        return true;
    }
    
    public boolean getTargetAnimal() {
        return Utils.getBit((byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS), 0);
    }
    
    public void setTargetAnimal(final boolean par1) {
        final byte var2 = (byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS);
        if (par1) {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.setBit(var2, 0));
        }
        else {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.clearBit(var2, 0));
        }
        setAttackTarget(null);
    }
    
    public boolean getTargetMob() {
        return Utils.getBit((byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS), 1);
    }
    
    public void setTargetMob(final boolean par1) {
        final byte var2 = (byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS);
        if (par1) {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.setBit(var2, 1));
        }
        else {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.clearBit(var2, 1));
        }
        setAttackTarget(null);
    }
    
    public boolean getTargetPlayer() {
        return Utils.getBit((byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS), 2);
    }
    
    public void setTargetPlayer(final boolean par1) {
        final byte var2 = (byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS);
        if (par1) {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.setBit(var2, 2));
        }
        else {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.clearBit(var2, 2));
        }
        setAttackTarget(null);
    }
    
    public boolean getTargetFriendly() {
        return Utils.getBit((byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS), 3);
    }
    
    public void setTargetFriendly(final boolean par1) {
        final byte var2 = (byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS);
        if (par1) {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.setBit(var2, 3));
        }
        else {
            getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, (byte)Utils.clearBit(var2, 3));
        }
        setAttackTarget(null);
    }
    
    @Override
    public Team getTeam() {
        if (isOwned()) {
            final EntityLivingBase entitylivingbase = getOwnerEntity();
            if (entitylivingbase != null) {
                return entitylivingbase.getTeam();
            }
        }
        return super.getTeam();
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0);
    }
    
    @Override
    public int getTotalArmorValue() {
        return 8;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled() && getAttackTarget() != null && getAttackTarget() instanceof EntityPlayer && getAttackTarget() != getOwnerEntity()) {
            setAttackTarget(null);
        }
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        getDataManager().set(EntityTurretCrossbowAdvanced.FLAGS, nbt.getByte("targets"));
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("targets", (byte) getDataManager().get((DataParameter)EntityTurretCrossbowAdvanced.FLAGS));
    }
    
    @Override
    public void knockBack(final Entity p_70653_1_, final float p_70653_2_, final double p_70653_3_, final double p_70653_5_) {
        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_ / 10.0, p_70653_5_ / 10.0);
    }
    
    @Override
    protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
        if (!world.isRemote && isOwner(player) && !isDead) {
            if (player.isSneaking()) {
                playSound(SoundsTC.zap, 1.0f, 1.0f);
                dropAmmo();
                entityDropItem(new ItemStack(ItemsTC.turretPlacer, 1, 1), 0.5f);
                setDead();
                player.swingArm(hand);
            }
            else {
                player.openGui(Thaumcraft.instance, 17, world, getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }
    
    @Override
    public void move(final MoverType t, final double x, final double y, final double z) {
        super.move(t, x / 15.0, y, z / 15.0);
    }
    
    @Override
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
        final float b = p_70628_2_ * 0.15f;
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.mind, 1, 1), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
        if (rand.nextFloat() < 0.3f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate, 1, 0), 0.5f);
        }
        if (rand.nextFloat() < 0.4f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate, 1, 1), 0.5f);
        }
        if (rand.nextFloat() < 0.4f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate, 1, 1), 0.5f);
        }
    }
    
    static {
        FLAGS = EntityDataManager.createKey(EntityTurretCrossbowAdvanced.class, DataSerializers.BYTE);
    }
    
    protected class EntityAIWatchTarget extends EntityAIBase
    {
        protected EntityLiving theWatcher;
        protected Entity closestEntity;
        private int lookTime;
        
        public EntityAIWatchTarget(final EntityLiving p_i1631_1_) {
            theWatcher = p_i1631_1_;
            setMutexBits(2);
        }
        
        public boolean shouldExecute() {
            if (theWatcher.getAttackTarget() != null) {
                closestEntity = theWatcher.getAttackTarget();
            }
            return closestEntity != null;
        }
        
        public boolean shouldContinueExecuting() {
            final float d = (float) getTargetDistance();
            return closestEntity.isEntityAlive() && theWatcher.getDistanceSq(closestEntity) <= d * d && lookTime > 0;
        }
        
        public void startExecuting() {
            lookTime = 40 + theWatcher.getRNG().nextInt(40);
        }
        
        public void resetTask() {
            closestEntity = null;
        }
        
        public void updateTask() {
            theWatcher.getLookHelper().setLookPosition(closestEntity.posX, closestEntity.posY + closestEntity.getEyeHeight(), closestEntity.posZ, 10.0f, (float) theWatcher.getVerticalFaceSpeed());
            --lookTime;
        }
        
        protected double getTargetDistance() {
            final IAttributeInstance iattributeinstance = theWatcher.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            return (iattributeinstance == null) ? 16.0 : iattributeinstance.getAttributeValue();
        }
    }
    
    protected class EntityAINearestValidTarget extends EntityAITarget
    {
        protected final Class<? extends EntityLivingBase> targetClass;
        private final int targetChance;
        protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
        protected Predicate<EntityLivingBase> targetEntitySelector;
        protected EntityLivingBase targetEntity;
        private int targetUnseenTicks;
        
        /*public EntityAINearestValidTarget(final EntityTurretCrossbowAdvanced this$0, final EntityCreature p_i45878_1_, final Class p_i45878_2_, final boolean p_i45878_3_) {
            this(this$0, p_i45878_1_, p_i45878_2_, p_i45878_3_, false);
        }
        
        public EntityAINearestValidTarget(final EntityTurretCrossbowAdvanced this$0, final EntityCreature p_i45879_1_, final Class p_i45879_2_, final boolean p_i45879_3_, final boolean p_i45879_4_) {
            this(this$0, p_i45879_1_, p_i45879_2_, 10, p_i45879_3_, p_i45879_4_, null);
        }*/
        
        public EntityAINearestValidTarget(final EntityCreature owner, final Class<? extends EntityLivingBase> targetCls, final int targetChance, final boolean checkSight, final boolean onlyNearby, final Predicate<EntityLivingBase> tselector) {
            super(owner, checkSight, onlyNearby);
            targetClass = targetCls;
            this.targetChance = targetChance;
            theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(owner);
            setMutexBits(1);
            targetEntitySelector = new Predicate<EntityLivingBase>() {
                private static final String __OBFID = "CL_00001621";
                
                public boolean applySelection(final EntityLivingBase entity) {
                    if (tselector != null && !tselector.apply(entity)) {
                        return false;
                    }
                    if (entity instanceof EntityPlayer) {
                        double d0 = getTargetDistance();
                        if (entity.isSneaking()) {
                            d0 *= 0.800000011920929;
                        }
                        if (entity.isInvisible()) {
                            float f = ((EntityPlayer)entity).getArmorVisibility();
                            if (f < 0.1f) {
                                f = 0.1f;
                            }
                            d0 *= 0.7f * f;
                        }
                        if (entity.getDistance(taskOwner) > d0) {
                            return false;
                        }
                    }
                    return isSuitableTarget(entity, false);
                }
                
                public boolean apply(final EntityLivingBase p_apply_1_) {
                    return applySelection(p_apply_1_);
                }
            };
        }
        
        public boolean shouldContinueExecuting() {
            final EntityLivingBase entitylivingbase = taskOwner.getAttackTarget();
            if (entitylivingbase == null) {
                return false;
            }
            if (!entitylivingbase.isEntityAlive()) {
                return false;
            }
            final Team team = taskOwner.getTeam();
            final Team team2 = entitylivingbase.getTeam();
            if (team != null && team2 == team && !((EntityTurretCrossbowAdvanced) taskOwner).getTargetFriendly()) {
                return false;
            }
            if (team != null && team2 != team && ((EntityTurretCrossbowAdvanced) taskOwner).getTargetFriendly()) {
                return false;
            }
            final double d0 = getTargetDistance();
            if (taskOwner.getDistanceSq(entitylivingbase) > d0 * d0) {
                return false;
            }
            if (shouldCheckSight) {
                if (taskOwner.getEntitySenses().canSee(entitylivingbase)) {
                    targetUnseenTicks = 0;
                }
                else if (++targetUnseenTicks > 60) {
                    return false;
                }
            }
            return true;
        }
        
        protected boolean isSuitableTarget(final EntityLivingBase p_75296_1_, final boolean p_75296_2_) {
            return isGoodTarget(taskOwner, p_75296_1_, p_75296_2_, shouldCheckSight) && taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(p_75296_1_));
        }
        
        private boolean isGoodTarget(final EntityLiving attacker, final EntityLivingBase posTar, final boolean p_179445_2_, final boolean checkSight) {
            if (posTar == null) {
                return false;
            }
            if (posTar == attacker) {
                return false;
            }
            if (!posTar.isEntityAlive()) {
                return false;
            }
            if (!attacker.canAttackClass(posTar.getClass())) {
                return false;
            }
            final Team team = attacker.getTeam();
            final Team team2 = posTar.getTeam();
            if (team != null && team2 == team && !((EntityTurretCrossbowAdvanced)attacker).getTargetFriendly()) {
                return false;
            }
            if (team != null && team2 != team && ((EntityTurretCrossbowAdvanced)attacker).getTargetFriendly()) {
                return false;
            }
            if (attacker instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)attacker).getOwnerId().toString())) {
                if (posTar instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)posTar).getOwnerId()) && !((EntityTurretCrossbowAdvanced)attacker).getTargetFriendly()) {
                    return false;
                }
                if (!(posTar instanceof IEntityOwnable) && !(posTar instanceof EntityPlayer) && ((EntityTurretCrossbowAdvanced)attacker).getTargetFriendly()) {
                    return false;
                }
                if (posTar instanceof IEntityOwnable && !((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)posTar).getOwnerId()) && ((EntityTurretCrossbowAdvanced)attacker).getTargetFriendly()) {
                    return false;
                }
                if (posTar == ((IEntityOwnable)attacker).getOwner() && !((EntityTurretCrossbowAdvanced)attacker).getTargetFriendly()) {
                    return false;
                }
            }
            else if (posTar instanceof EntityPlayer && !p_179445_2_ && ((EntityPlayer)posTar).capabilities.disableDamage && !((EntityTurretCrossbowAdvanced)attacker).getTargetFriendly()) {
                return false;
            }
            return !checkSight || attacker.getEntitySenses().canSee(posTar);
        }
        
        public boolean shouldExecute() {
            if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
                return false;
            }
            final double d0 = getTargetDistance();
            final List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
            Collections.sort(list, theNearestAttackableTargetSorter);
            if (list.isEmpty()) {
                return false;
            }
            targetEntity = list.get(0);
            return true;
        }
        
        public void startExecuting() {
            taskOwner.setAttackTarget(targetEntity);
            targetUnseenTicks = 0;
            super.startExecuting();
        }

        
        public class Sorter implements Comparator
        {
            private final Entity theEntity;
            private static final String __OBFID = "CL_00001622";
            
            public Sorter(final Entity p_i1662_1_) {
                theEntity = p_i1662_1_;
            }
            
            public int compare(final Entity p_compare_1_, final Entity p_compare_2_) {
                final double d0 = theEntity.getDistanceSq(p_compare_1_);
                final double d2 = theEntity.getDistanceSq(p_compare_2_);
                return (d0 < d2) ? -1 : ((d0 > d2) ? 1 : 0);
            }
            
            @Override
            public int compare(final Object p_compare_1_, final Object p_compare_2_) {
                return compare((Entity)p_compare_1_, (Entity)p_compare_2_);
            }
        }
    }
}
