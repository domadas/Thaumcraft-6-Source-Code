// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.tiles;

import net.minecraft.util.IThreadListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.common.tiles.TileThaumcraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTileToServer implements IMessage, IMessageHandler<PacketTileToServer, IMessage>
{
    private long pos;
    private NBTTagCompound nbt;
    
    public PacketTileToServer() {
    }
    
    public PacketTileToServer(final BlockPos pos, final NBTTagCompound nbt) {
        this.pos = pos.toLong();
        this.nbt = nbt;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeLong(this.pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, this.nbt);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.pos = buffer.readLong();
        this.nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    public IMessage onMessage(final PacketTileToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final World world = ctx.getServerHandler().player.getServerWorld();
                final BlockPos bp = BlockPos.fromLong(message.pos);
                if (world != null && bp != null) {
                    final TileEntity te = world.getTileEntity(bp);
                    if (te != null && te instanceof TileThaumcraft) {
                        ((TileThaumcraft)te).messageFromClient((message.nbt == null) ? new NBTTagCompound() : message.nbt, ctx.getServerHandler().player);
                    }
                }
            }
        });
        return null;
    }
}
