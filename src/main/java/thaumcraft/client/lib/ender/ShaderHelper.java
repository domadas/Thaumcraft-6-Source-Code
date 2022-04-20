// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.ender;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.client.renderer.OpenGlHelper;
import thaumcraft.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBShaderObjects;

public final class ShaderHelper
{
    private static final int VERT = 35633;
    private static final int FRAG = 35632;
    private static final String PREFIX = "/assets/thaumcraft/shader/";
    public static int endShader;
    public static int sketchShader;
    
    public static void initShaders() {
        if (!useShaders()) {
            return;
        }
        ShaderHelper.endShader = createProgram("ender.vert", "ender.frag");
        ShaderHelper.sketchShader = createProgram("sketch.vert", "sketch.frag");
    }
    
    public static void useShader(final int shader, final ShaderCallback callback) {
        if (!useShaders()) {
            return;
        }
        ARBShaderObjects.glUseProgramObjectARB(shader);
        if (shader != 0) {
            final int time = ARBShaderObjects.glGetUniformLocationARB(shader, "time");
            ARBShaderObjects.glUniform1iARB(time, Minecraft.getMinecraft().getRenderViewEntity().ticksExisted);
            if (callback != null) {
                callback.call(shader);
            }
        }
    }
    
    public static void useShader(final int shader) {
        useShader(shader, null);
    }
    
    public static void releaseShader() {
        useShader(0);
    }
    
    public static boolean useShaders() {
        return !ModConfig.CONFIG_GRAPHICS.disableShaders && OpenGlHelper.shadersSupported;
    }
    
    private static int createProgram(final String vert, final String frag) {
        int vertId = 0;
        int fragId = 0;
        int program = 0;
        if (vert != null) {
            vertId = createShader("/assets/thaumcraft/shader/" + vert, 35633);
        }
        if (frag != null) {
            fragId = createShader("/assets/thaumcraft/shader/" + frag, 35632);
        }
        program = ARBShaderObjects.glCreateProgramObjectARB();
        if (program == 0) {
            return 0;
        }
        if (vert != null) {
            ARBShaderObjects.glAttachObjectARB(program, vertId);
        }
        if (frag != null) {
            ARBShaderObjects.glAttachObjectARB(program, fragId);
        }
        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, 35714) == 0) {
            return 0;
        }
        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, 35715) == 0) {
            return 0;
        }
        return program;
    }
    
    private static int createShader(final String filename, final int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if (shader == 0) {
                return 0;
            }
            ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
            }
            return shader;
        }
        catch (final Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            e.printStackTrace();
            return -1;
        }
    }
    
    private static String getLogInfo(final int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, 35716));
    }

    private static String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        InputStream in = ShaderHelper.class.getResourceAsStream(filename);
        Exception exception = null;
        if (in == null) {
            return "";
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                Exception innerExc = null;

                try {
                    String line;
                    try {
                        while((line = reader.readLine()) != null) {
                            source.append(line).append('\n');
                        }
                    } catch (Exception var31) {
                        exception = var31;
                    }
                } finally {
                    try {
                        reader.close();
                    } catch (Exception var30) {
                        if (innerExc == null) {
                            innerExc = var30;
                        } else {
                            var30.printStackTrace();
                        }
                    }

                }

                if (innerExc != null) {
                    throw innerExc;
                }
            } catch (Exception var33) {
                exception = var33;
            } finally {
                try {
                    in.close();
                } catch (Exception var29) {
                    if (exception == null) {
                        exception = var29;
                    } else {
                        var29.printStackTrace();
                    }
                }

                if (exception != null) {
                    throw exception;
                }

            }

            return source.toString();
        }
    }
    
    static {
        ShaderHelper.endShader = 0;
        ShaderHelper.sketchShader = 0;
    }
}
