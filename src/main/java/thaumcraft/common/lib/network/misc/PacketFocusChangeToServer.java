// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.util.IThreadListener;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.api.casters.ICaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFocusChangeToServer implements IMessage, IMessageHandler<PacketFocusChangeToServer, IMessage>
{
    private String focus;
    
    public PacketFocusChangeToServer() {
    }
    
    public PacketFocusChangeToServer(final String focus) {
        this.focus = focus;
    }
    
    public void toBytes(final ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, focus);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        focus = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(final PacketFocusChangeToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final World world = ctx.getServerHandler().player.getServerWorld();
                if (world == null) {
                    return;
                }
                final Entity player = ctx.getServerHandler().player;
                if (player != null && player instanceof EntityPlayer && ((EntityPlayer)player).getHeldItemMainhand().getItem() instanceof ICaster) {
                    CasterManager.changeFocus(((EntityPlayer)player).getHeldItemMainhand(), world, (EntityPlayer)player, message.focus);
                }
                else if (player != null && player instanceof EntityPlayer && ((EntityPlayer)player).getHeldItemOffhand().getItem() instanceof ICaster) {
                    CasterManager.changeFocus(((EntityPlayer)player).getHeldItemOffhand(), world, (EntityPlayer)player, message.focus);
                }
            }
        });
        return null;
    }
}
