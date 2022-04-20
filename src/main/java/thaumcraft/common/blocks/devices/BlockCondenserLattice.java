// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileCondenser;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import java.util.ArrayList;
import net.minecraft.block.properties.PropertyBool;
import thaumcraft.common.blocks.BlockTC;

public class BlockCondenserLattice extends BlockTC
{
    public static final PropertyBool NORTH;
    public static final PropertyBool EAST;
    public static final PropertyBool SOUTH;
    public static final PropertyBool WEST;
    public static final PropertyBool UP;
    public static final PropertyBool DOWN;
    private ArrayList<Long> history;
    
    public BlockCondenserLattice(final boolean dirty) {
        super(Material.IRON, dirty ? "condenser_lattice_dirty" : "condenser_lattice");
        this.history = new ArrayList<Long>();
        this.setHardness(0.5f);
        this.setResistance(5.0f);
        this.setSoundType(SoundType.METAL);
        this.setLightLevel(dirty ? 0.0f : 0.33f);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)BlockCondenserLattice.NORTH, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.EAST, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.SOUTH, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.WEST, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.UP, (Comparable)false).withProperty((IProperty)BlockCondenserLattice.DOWN, (Comparable)false));
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {BlockCondenserLattice.NORTH, BlockCondenserLattice.EAST, BlockCondenserLattice.SOUTH, BlockCondenserLattice.WEST, BlockCondenserLattice.UP, BlockCondenserLattice.DOWN});
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }
    
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        final Boolean[] cons = this.makeConnections(state, worldIn, pos);
        return state.withProperty((IProperty)BlockCondenserLattice.DOWN, (Comparable)cons[0]).withProperty((IProperty)BlockCondenserLattice.UP, (Comparable)cons[1]).withProperty((IProperty)BlockCondenserLattice.NORTH, (Comparable)cons[2]).withProperty((IProperty)BlockCondenserLattice.SOUTH, (Comparable)cons[3]).withProperty((IProperty)BlockCondenserLattice.WEST, (Comparable)cons[4]).withProperty((IProperty)BlockCondenserLattice.EAST, (Comparable)cons[5]);
    }
    
    private Boolean[] makeConnections(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        final Boolean[] cons = { false, false, false, false, false, false };
        int a = 0;
        for (final EnumFacing face : EnumFacing.VALUES) {
            final Block b = world.getBlockState(pos.offset(face)).getBlock();
            if (b instanceof BlockCondenserLattice || (face == EnumFacing.DOWN && b == BlocksTC.condenser)) {
                cons[a] = true;
            }
            ++a;
        }
        return cons;
    }
    
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.triggerUpdate(worldIn, pos);
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (blockIn == BlocksTC.condenserlattice || blockIn == BlocksTC.condenserlatticeDirty || blockIn == BlocksTC.condenser) {
            this.triggerUpdate(worldIn, pos);
        }
    }
    
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        if (state.getBlock() == BlocksTC.condenserlatticeDirty && playerIn.getHeldItem(hand).getItem() == ItemsTC.filter) {
            playerIn.getHeldItem(hand).shrink(1);
            if (worldIn.rand.nextBoolean()) {
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5f + facing.getFrontOffsetX() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + facing.getFrontOffsetZ() / 3.0f, ConfigItems.FLUX_CRYSTAL.copy()));
            }
            worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7f + 1.0f) * 1.6f);
            worldIn.setBlockState(pos, BlocksTC.condenserlattice.getDefaultState(), 3);
            final IBlockState state2 = worldIn.getBlockState(pos);
            if (state2.getBlock() instanceof BlockCondenserLattice) {
                ((BlockCondenserLattice)state2.getBlock()).triggerUpdate(worldIn, pos);
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
    
    public void onBlockHarvested(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        this.triggerUpdate(worldIn, pos);
    }
    
    public void triggerUpdate(final World world, final BlockPos pos) {
        this.history.clear();
        final BlockPos p = this.processUpdate(world, pos);
        if (p == null || p.distanceSq(pos) > 74.0) {
            this.dropBlockAsItem(world, pos, this.getDefaultState(), 0);
            world.setBlockToAir(pos);
        }
        this.history.clear();
    }
    
    private BlockPos processUpdate(final World world, final BlockPos pos) {
        this.history.add(pos.toLong());
        for (final EnumFacing face : EnumFacing.VALUES) {
            final BlockPos p2 = pos.offset(face);
            if (!this.history.contains(p2.toLong())) {
                final Block b = world.getBlockState(p2).getBlock();
                if (b instanceof BlockCondenserLattice) {
                    final BlockPos pp = this.processUpdate(world, p2);
                    if (pp != null) {
                        return pp;
                    }
                }
                if (face == EnumFacing.DOWN && b == BlocksTC.condenser) {
                    final TileEntity te = world.getTileEntity(p2);
                    if (te != null && te instanceof TileCondenser) {
                        ((TileCondenser)te).latticeCount = -1.0f;
                    }
                    return p2;
                }
            }
        }
        return null;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        float minx = 0.3125f;
        float maxx = 0.6875f;
        float miny = 0.3125f;
        float maxy = 0.6875f;
        float minz = 0.3125f;
        float maxz = 0.6875f;
        EnumFacing fd = null;
        for (int side = 0; side < 6; ++side) {
            fd = EnumFacing.VALUES[side];
            final Block b = source.getBlockState(pos.offset(fd)).getBlock();
            if (b instanceof BlockCondenserLattice || (fd == EnumFacing.DOWN && b == BlocksTC.condenser)) {
                switch (side) {
                    case 0: {
                        miny = 0.0f;
                        break;
                    }
                    case 1: {
                        maxy = 1.0f;
                        break;
                    }
                    case 2: {
                        minz = 0.0f;
                        break;
                    }
                    case 3: {
                        maxz = 1.0f;
                        break;
                    }
                    case 4: {
                        minx = 0.0f;
                        break;
                    }
                    case 5: {
                        maxx = 1.0f;
                        break;
                    }
                }
            }
        }
        return new AxisAlignedBB(minx, miny, minz, maxx, maxy, maxz);
    }
    
    static {
        NORTH = PropertyBool.create("north");
        EAST = PropertyBool.create("east");
        SOUTH = PropertyBool.create("south");
        WEST = PropertyBool.create("west");
        UP = PropertyBool.create("up");
        DOWN = PropertyBool.create("down");
    }
}
