// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileAlembic extends TileThaumcraft implements IAspectContainer, IEssentiaTransport
{
    public Aspect aspect;
    public Aspect aspectFilter;
    public int amount;
    public int maxAmount;
    public int facing;
    public boolean aboveFurnace;
    EnumFacing fd;
    
    public TileAlembic() {
        aspectFilter = null;
        amount = 0;
        maxAmount = 128;
        facing = EnumFacing.DOWN.ordinal();
        aboveFurnace = false;
        fd = null;
    }
    
    @Override
    public AspectList getAspects() {
        return (aspect != null) ? new AspectList().add(aspect, amount) : new AspectList();
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 0.1, getPos().getY() - 0.1, getPos().getZ() - 0.1, getPos().getX() + 1.1, getPos().getY() + 1.1, getPos().getZ() + 1.1);
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        facing = nbttagcompound.getByte("facing");
        aspectFilter = Aspect.getAspect(nbttagcompound.getString("AspectFilter"));
        final String tag = nbttagcompound.getString("aspect");
        if (tag != null) {
            aspect = Aspect.getAspect(tag);
        }
        amount = nbttagcompound.getShort("amount");
        fd = EnumFacing.VALUES[facing];
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        if (aspect != null) {
            nbttagcompound.setString("aspect", aspect.getTag());
        }
        if (aspectFilter != null) {
            nbttagcompound.setString("AspectFilter", aspectFilter.getTag());
        }
        nbttagcompound.setShort("amount", (short) amount);
        nbttagcompound.setByte("facing", (byte) facing);
        return nbttagcompound;
    }
    
    @Override
    public int addToContainer(final Aspect tt, int am) {
        if (aspectFilter != null && tt != aspectFilter) {
            return am;
        }
        if ((amount < maxAmount && tt == aspect) || amount == 0) {
            aspect = tt;
            final int added = Math.min(am, maxAmount - amount);
            amount += added;
            am -= added;
        }
        markDirty();
        syncTile(false);
        return am;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tt, final int am) {
        if (amount == 0 || aspect == null) {
            aspect = null;
            amount = 0;
        }
        if (aspect != null && amount >= am && tt == aspect) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            markDirty();
            syncTile(false);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return amount > 0 && aspect != null && ot.getAmount(aspect) > 0;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tt, final int am) {
        return amount >= am && tt == aspect;
    }
    
    @Override
    public int containerContains(final Aspect tt) {
        return (tt == aspect) ? amount : 0;
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face != EnumFacing.VALUES[facing] && face != EnumFacing.DOWN;
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return false;
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return face != EnumFacing.VALUES[facing] && face != EnumFacing.DOWN;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return aspect;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return amount;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    protected static boolean processAlembics(final World world, final BlockPos pos, final Aspect aspect) {
        int deep = 1;
        while (true) {
            TileEntity te = world.getTileEntity(pos.up(deep));
            if (te != null && te instanceof TileAlembic) {
                final TileAlembic alembic = (TileAlembic)te;
                if (alembic.amount > 0 && alembic.aspect == aspect && alembic.addToContainer(aspect, 1) == 0) {
                    return true;
                }
                ++deep;
            }
            else {
                deep = 1;
                while (true) {
                    te = world.getTileEntity(pos.up(deep));
                    if (te == null || !(te instanceof TileAlembic)) {
                        return false;
                    }
                    final TileAlembic alembic = (TileAlembic)te;
                    if ((alembic.aspectFilter == null || alembic.aspectFilter == aspect) && alembic.addToContainer(aspect, 1) == 0) {
                        return true;
                    }
                    ++deep;
                }
            }
        }
    }
}
