// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.playerdata;

import net.minecraft.util.IThreadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlayerFlagToServer implements IMessage, IMessageHandler<PacketPlayerFlagToServer, IMessage>
{
    byte flag;
    
    public PacketPlayerFlagToServer() {
    }
    
    public PacketPlayerFlagToServer(final EntityLivingBase player, final int i) {
        flag = (byte)i;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeByte(flag);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        flag = buffer.readByte();
    }
    
    public IMessage onMessage(final PacketPlayerFlagToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (ctx.getServerHandler().player != null) {
                    final EntityPlayer player = ctx.getServerHandler().player;
                    switch (message.flag) {
                        case 1: {
                            player.fallDistance = 0.0f;
                            break;
                        }
                    }
                }
            }
        });
        return null;
    }
}
