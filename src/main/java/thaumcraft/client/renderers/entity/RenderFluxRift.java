// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.lib.ender.ShaderHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.entities.EntityFluxRift;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.ARBShaderObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.ender.ShaderCallback;
import net.minecraft.client.renderer.entity.Render;

public class RenderFluxRift extends Render
{
    private final ShaderCallback shaderCallback;
    private static final ResourceLocation starsTexture;
    CoreGLE gle;
    
    public RenderFluxRift(final RenderManager rm) {
        super(rm);
        this.gle = new CoreGLE();
        this.shadowSize = 0.0f;
        this.shaderCallback = new ShaderCallback() {
            @Override
            public void call(final int shader) {
                final Minecraft mc = Minecraft.getMinecraft();
                final int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                final int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
    }
    
    public void doRender(final Entity entity, final double x, final double y, final double z, final float yaw, final float pt) {
        final EntityFluxRift rift = (EntityFluxRift)entity;
        final boolean goggles = EntityUtils.hasGoggles(Minecraft.getMinecraft().player);
        GL11.glPushMatrix();
        this.bindTexture(RenderFluxRift.starsTexture);
        ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
        final float amp = 1.0f;
        final float stab = MathHelper.clamp(1.0f - rift.getRiftStability() / 50.0f, 0.0f, 1.5f);
        GL11.glEnable(3042);
        for (int q = 0; q <= 3; ++q) {
            if (q < 3) {
                GlStateManager.depthMask(false);
                if (q == 0 && goggles) {
                    GL11.glDisable(2929);
                }
            }
            GL11.glBlendFunc(770, (q < 3) ? 1 : 771);
            if (rift.points.size() > 2) {
                GL11.glPushMatrix();
                final double[][] pp = new double[rift.points.size()][3];
                final float[][] colours = new float[rift.points.size()][4];
                final double[] radii = new double[rift.points.size()];
                for (int a = 0; a < rift.points.size(); ++a) {
                    float var = rift.ticksExisted + pt;
                    if (a > rift.points.size() / 2) {
                        var -= a * 10;
                    }
                    else if (a < rift.points.size() / 2) {
                        var += a * 10;
                    }
                    pp[a][0] = rift.points.get(a).x + x + Math.sin(var / 50.0f * amp) * 0.10000000149011612 * stab;
                    pp[a][1] = rift.points.get(a).y + y + Math.sin(var / 60.0f * amp) * 0.10000000149011612 * stab;
                    pp[a][2] = rift.points.get(a).z + z + Math.sin(var / 70.0f * amp) * 0.10000000149011612 * stab;
                    colours[a][0] = 1.0f;
                    colours[a][1] = 1.0f;
                    colours[a][2] = 1.0f;
                    colours[a][3] = 1.0f;
                    final double w = 1.0 - Math.sin(var / 8.0f * amp) * 0.10000000149011612 * stab;
                    radii[a] = rift.pointsWidth.get(a) * w * ((q < 3) ? (1.25f + 0.5f * q) : 1.0f);
                }
                this.gle.set_POLYCYL_TESS(6);
                this.gle.gleSetJoinStyle(1026);
                this.gle.glePolyCone(pp.length, pp, colours, radii, 1.0f, 0.0f);
                GL11.glPopMatrix();
            }
            if (q < 3) {
                GlStateManager.depthMask(true);
                if (q == 0 && goggles) {
                    GL11.glEnable(2929);
                }
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        ShaderHelper.releaseShader();
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
