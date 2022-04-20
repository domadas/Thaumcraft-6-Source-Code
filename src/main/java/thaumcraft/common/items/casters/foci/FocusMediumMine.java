// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import thaumcraft.api.casters.NodeSetting;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.projectile.EntityFocusMine;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;

public class FocusMediumMine extends FocusMedium
{
    @Override
    public String getResearch() {
        return "FOCUSMINE";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.MINE";
    }
    
    @Override
    public int getComplexity() {
        return 4;
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.TRAP;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY };
    }
    
    @Override
    public boolean execute(final Trajectory trajectory) {
        final EntityFocusMine projectile = new EntityFocusMine(this.getRemainingPackage(), trajectory, this.getSettingValue("target") == 1);
        return this.getPackage().getCaster().world.spawnEntity(projectile);
    }
    
    @Override
    public boolean hasIntermediary() {
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        final int[] friend = { 0, 1 };
        final String[] friendDesc = { "focus.common.enemy", "focus.common.friend" };
        return new NodeSetting[] { new NodeSetting("target", "focus.common.target", new NodeSetting.NodeSettingIntList(friend, friendDesc)) };
    }
}
