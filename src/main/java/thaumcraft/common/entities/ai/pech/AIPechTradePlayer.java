// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.ai.pech;

import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.entity.ai.EntityAIBase;

public class AIPechTradePlayer extends EntityAIBase
{
    private EntityPech villager;
    
    public AIPechTradePlayer(final EntityPech par1EntityVillager) {
        villager = par1EntityVillager;
        setMutexBits(5);
    }
    
    public boolean shouldExecute() {
        return villager.isEntityAlive() && !villager.isInWater() && villager.isTamed() && villager.onGround && !villager.velocityChanged && villager.trading;
    }
    
    public void startExecuting() {
        villager.getNavigator().clearPath();
    }
    
    public void resetTask() {
        villager.trading = false;
    }
}
