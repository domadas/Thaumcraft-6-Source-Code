// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.blocks.devices.BlockArcaneEarToggle;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import java.util.WeakHashMap;
import net.minecraft.util.ITickable;
import net.minecraft.tileentity.TileEntity;

public class TileArcaneEar extends TileEntity implements ITickable
{
    public byte note;
    public byte tone;
    public int redstoneSignal;
    public static WeakHashMap<Integer, ArrayList<Integer[]>> noteBlockEvents;
    
    public TileArcaneEar() {
        note = 0;
        tone = 0;
        redstoneSignal = 0;
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("note", note);
        par1NBTTagCompound.setByte("tone", tone);
        return par1NBTTagCompound;
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        note = par1NBTTagCompound.getByte("note");
        tone = par1NBTTagCompound.getByte("tone");
        if (note < 0) {
            note = 0;
        }
        if (note > 24) {
            note = 24;
        }
    }
    
    public void update() {
        if (!world.isRemote) {
            if (redstoneSignal > 0) {
                --redstoneSignal;
                if (redstoneSignal == 0) {
                    final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata()).getOpposite();
                    final TileEntity tileentity = world.getTileEntity(pos);
                    world.setBlockState(pos, world.getBlockState(pos).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    if (tileentity != null) {
                        tileentity.validate();
                        world.setTileEntity(pos, tileentity);
                    }
                    world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
                    world.notifyNeighborsOfStateChange(pos.offset(facing), getBlockType(), true);
                    final IBlockState state = world.getBlockState(pos);
                    world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
                }
            }
            final ArrayList<Integer[]> nbe = TileArcaneEar.noteBlockEvents.get(world.provider.getDimension());
            if (nbe != null) {
                for (final Integer[] dat : nbe) {
                    if (dat[3] == tone && dat[4] == note && getDistanceSq(dat[0] + 0.5, dat[1] + 0.5, dat[2] + 0.5) <= 4096.0) {
                        final EnumFacing facing2 = BlockStateUtils.getFacing(getBlockMetadata()).getOpposite();
                        triggerNote(world, pos, true);
                        final TileEntity tileentity2 = world.getTileEntity(pos);
                        final IBlockState state2 = world.getBlockState(pos);
                        if (getBlockType() instanceof BlockArcaneEarToggle) {
                            world.setBlockState(pos, state2.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)!BlockStateUtils.isEnabled(state2)), 3);
                        }
                        else {
                            redstoneSignal = 10;
                            world.setBlockState(pos, state2.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true), 3);
                        }
                        if (tileentity2 != null) {
                            tileentity2.validate();
                            world.setTileEntity(pos, tileentity2);
                        }
                        world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
                        world.notifyNeighborsOfStateChange(pos.offset(facing2), getBlockType(), true);
                        final IBlockState state3 = world.getBlockState(pos);
                        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state3, state3, 3);
                        break;
                    }
                }
            }
        }
    }
    
    public void updateTone() {
        try {
            final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata()).getOpposite();
            final IBlockState iblockstate = world.getBlockState(pos.offset(facing));
            final Material material = iblockstate.getMaterial();
            tone = 0;
            if (material == Material.ROCK) {
                tone = 1;
            }
            if (material == Material.SAND) {
                tone = 2;
            }
            if (material == Material.GLASS) {
                tone = 3;
            }
            if (material == Material.WOOD) {
                tone = 4;
            }
            final Block block = iblockstate.getBlock();
            if (block == Blocks.CLAY) {
                tone = 5;
            }
            if (block == Blocks.GOLD_BLOCK) {
                tone = 6;
            }
            if (block == Blocks.WOOL) {
                tone = 7;
            }
            if (block == Blocks.PACKED_ICE) {
                tone = 8;
            }
            if (block == Blocks.BONE_BLOCK) {
                tone = 9;
            }
            markDirty();
        }
        catch (final Exception ex) {}
    }
    
    public void changePitch() {
        note = (byte)((note + 1) % 25);
        markDirty();
    }
    
    public void triggerNote(final World world, final BlockPos pos, final boolean sound) {
        byte i = -1;
        if (sound) {
            final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata()).getOpposite();
            final IBlockState iblockstate = world.getBlockState(pos.offset(facing));
            final Material material = iblockstate.getMaterial();
            i = 0;
            if (material == Material.ROCK) {
                i = 1;
            }
            if (material == Material.SAND) {
                i = 2;
            }
            if (material == Material.GLASS) {
                i = 3;
            }
            if (material == Material.WOOD) {
                i = 4;
            }
            final Block block = iblockstate.getBlock();
            if (block == Blocks.CLAY) {
                i = 5;
            }
            if (block == Blocks.GOLD_BLOCK) {
                i = 6;
            }
            if (block == Blocks.WOOL) {
                i = 7;
            }
            if (block == Blocks.PACKED_ICE) {
                i = 8;
            }
            if (block == Blocks.BONE_BLOCK) {
                i = 9;
            }
        }
        world.addBlockEvent(pos, getBlockType(), i, note);
    }
    
    static {
        TileArcaneEar.noteBlockEvents = new WeakHashMap<Integer, ArrayList<Integer[]>>();
    }
}
