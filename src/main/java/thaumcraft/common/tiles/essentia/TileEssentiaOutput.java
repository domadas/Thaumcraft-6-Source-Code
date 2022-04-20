// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileEssentiaOutput extends TileThaumcraft implements IEssentiaTransport, ITickable
{
    int count;
    
    public TileEssentiaOutput() {
        this.count = 0;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face == this.getFacing().getOpposite();
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return false;
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return face == this.getFacing().getOpposite();
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
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
        return null;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return amount;
    }
    
    public void update() {
        if (!this.world.isRemote && ++this.count % 5 == 0) {
            this.fillBuffer();
        }
    }
    
    void fillBuffer() {
        final TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.getPos(), this.getFacing().getOpposite());
        if (te != null) {
            final IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canInputFrom(this.getFacing())) {
                return;
            }
            if (ic.getSuctionAmount(this.getFacing()) > 0 && ic.getSuctionType(this.getFacing()) != null) {
                final Aspect ta = ic.getSuctionType(this.getFacing());
                if (EssentiaHandler.drainEssentiaWithConfirmation(this, ta, this.getFacing(), 16, false, 5) && ic.addEssentia(ta, 1, this.getFacing()) > 0) {
                    EssentiaHandler.confirmDrain();
                }
            }
        }
    }
}
