// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.biome.Biome;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.BlockGrass;

public class BlockGrassAmbient extends BlockGrass
{
    public BlockGrassAmbient() {
        this.setUnlocalizedName("grass_ambient");
        this.setRegistryName("thaumcraft", "grass_ambient");
        this.setCreativeTab(ConfigItems.TABTC);
        this.setHardness(0.6f);
        this.setSoundType(SoundType.GROUND);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World worldIn, final BlockPos pos, final Random rand) {
        final Biome biome = worldIn.getBiome(pos);
        int i = worldIn.getLightFor(EnumSkyBlock.SKY, pos.up()) - worldIn.getSkylightSubtracted();
        float f = worldIn.getCelestialAngleRadians(1.0f);
        final float f2 = (f < 3.1415927f) ? 0.0f : 6.2831855f;
        f += (f2 - f) * 0.2f;
        i = Math.round(i * MathHelper.cos(f));
        i = MathHelper.clamp(i, 0, 15);
        if (4 + i * 2 < 1 + rand.nextInt(13)) {
            final int x = MathHelper.getInt(rand, -8, 8);
            final int z = MathHelper.getInt(rand, -8, 8);
            BlockPos pp = pos.add(x, 5, z);
            for (int q = 0; q < 10 && pp.getY() > 50 && worldIn.getBlockState(pp).getBlock() != Blocks.GRASS; pp = pp.down(), ++q) {}
            if (worldIn.getBlockState(pp).getBlock() == Blocks.GRASS) {
                FXDispatcher.INSTANCE.drawWispyMotesOnBlock(pp.up(), 400, -0.01f);
            }
        }
    }
}
