// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import net.minecraft.util.ResourceLocation;

public class SealBreakerAdvanced extends SealBreaker
{
    ResourceLocation icon;
    protected ISealConfigToggles.SealToggle[] props;
    
    public SealBreakerAdvanced() {
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker_advanced");
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(false, "psilk", "golem.prop.silk") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:breaker_advanced";
    }
    
    @Override
    public int getFilterSize() {
        return 9;
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return icon;
    }
    
    @Override
    public ISealConfigToggles.SealToggle[] getToggles() {
        return props;
    }
    
    @Override
    public void setToggle(final int indx, final boolean value) {
        props[indx].setValue(value);
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.SMART };
    }
}
