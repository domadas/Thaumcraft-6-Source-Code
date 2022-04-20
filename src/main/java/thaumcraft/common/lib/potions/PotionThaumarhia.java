// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.potions;

import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.Potion;

public class PotionThaumarhia extends Potion
{
    public static Potion instance;
    private int statusIconIndex;
    static final ResourceLocation rl;
    
    public PotionThaumarhia(final boolean par2, final int par3) {
        super(par2, par3);
        this.statusIconIndex = -1;
        this.setIconIndex(0, 0);
        this.setPotionName("potion.thaumarhia");
        this.setIconIndex(7, 2);
        this.setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(PotionThaumarhia.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(final EntityLivingBase target, final int par2) {
        if (!target.world.isRemote && target.world.rand.nextInt(15) == 0 && target.world.isAirBlock(new BlockPos(target))) {
            target.world.setBlockState(new BlockPos(target), BlocksTC.fluxGoo.getDefaultState());
        }
    }
    
    public boolean isReady(final int par1, final int par2) {
        return par1 % 20 == 0;
    }
    
    static {
        PotionThaumarhia.instance = null;
        rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    }
}