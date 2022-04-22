// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import java.awt.Color;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXZap implements IMessage, IMessageHandler<PacketFXZap, IMessage>
{
    private Vec3d source;
    private Vec3d target;
    private int color;
    private float width;
    
    public PacketFXZap() {
    }
    
    public PacketFXZap(final Vec3d source, final Vec3d target, final int color, final float width) {
        this.source = source;
        this.target = target;
        this.color = color;
        this.width = width;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeDouble(source.x);
        buffer.writeDouble(source.y);
        buffer.writeDouble(source.z);
        buffer.writeDouble(target.x);
        buffer.writeDouble(target.y);
        buffer.writeDouble(target.z);
        buffer.writeInt(color);
        buffer.writeFloat(width);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        source = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        target = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        color = buffer.readInt();
        width = buffer.readFloat();
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketFXZap message, final MessageContext ctx) {
        final Color c = new Color(message.color);
        FXDispatcher.INSTANCE.arcBolt(message.source.x, message.source.y, message.source.z, message.target.x, message.target.y, message.target.z, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, message.width);
        return null;
    }
}
