// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.particle.Particle;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockResearchTable extends BlockTCDevice implements IBlockFacingHorizontal
{
    public BlockResearchTable() {
        super(Material.WOOD, TileResearchTable.class, "research_table");
        this.setSoundType(SoundType.WOOD);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        return false;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        player.openGui(Thaumcraft.instance, 10, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)placer.getHorizontalFacing());
        return bs;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        final TileEntity te = world.getTileEntity(pos);
        if (rand.nextInt(5) == 0 && te != null && ((TileResearchTable)te).data != null) {
            final double xx = rand.nextGaussian() / 2.0;
            final double zz = rand.nextGaussian() / 2.0;
            final double yy = 1.5 + rand.nextFloat();
            final int a = 40 + rand.nextInt(20);
            final FXGeneric fb = new FXGeneric(world, pos.getX() + 0.5 + xx, pos.getY() + yy, pos.getZ() + 0.5 + zz, -xx / a, -(yy - 0.85) / a, -zz / a);
            fb.setMaxAge(a);
            fb.setRBGColorF(0.5f + rand.nextFloat() * 0.5f, 0.5f + rand.nextFloat() * 0.5f, 0.5f + rand.nextFloat() * 0.5f);
            fb.setAlphaF(0.0f, 0.25f, 0.5f, 0.75f, 0.0f);
            fb.setParticles(384 + rand.nextInt(16), 1, 1);
            fb.setScale(0.8f + rand.nextFloat() * 0.3f, 0.3f);
            fb.setLayer(0);
            ParticleEngine.addEffect(world, fb);
        }
    }
}
