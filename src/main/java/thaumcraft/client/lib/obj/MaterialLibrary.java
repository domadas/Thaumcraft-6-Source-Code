// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import java.util.HashSet;
import javax.vecmath.Vector3f;
import java.io.IOException;
import net.minecraft.client.resources.IResource;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Dictionary;

class MaterialLibrary extends Dictionary<String, Material>
{
    static final Set<String> unknownCommands;
    private final Dictionary<String, Material> materialLibrary;
    private Material currentMaterial;
    
    public MaterialLibrary() {
        materialLibrary = new Hashtable<String, Material>();
    }
    
    @Override
    public int size() {
        return materialLibrary.size();
    }
    
    @Override
    public boolean isEmpty() {
        return materialLibrary.isEmpty();
    }
    
    @Override
    public Enumeration<String> keys() {
        return materialLibrary.keys();
    }
    
    @Override
    public Enumeration<Material> elements() {
        return materialLibrary.elements();
    }
    
    @Override
    public Material get(final Object key) {
        return materialLibrary.get(key);
    }
    
    @Override
    public Material put(final String key, final Material value) {
        return materialLibrary.put(key, value);
    }
    
    @Override
    public Material remove(final Object key) {
        return materialLibrary.remove(key);
    }
    
    public void loadFromStream(final ResourceLocation loc) throws IOException {
        final IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
        final InputStreamReader lineStream = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
        final BufferedReader lineReader = new BufferedReader(lineStream);
        while (true) {
            final String currentLine = lineReader.readLine();
            if (currentLine == null) {
                break;
            }
            if (currentLine.length() == 0) {
                continue;
            }
            if (currentLine.startsWith("#")) {
                continue;
            }
            final String[] fields = currentLine.split(" ", 2);
            final String keyword = fields[0];
            final String data = fields[1];
            if (keyword.equalsIgnoreCase("newmtl")) {
                pushMaterial(data);
            }
            else if (keyword.equalsIgnoreCase("Ka")) {
                currentMaterial.AmbientColor = parseVector3f(data);
            }
            else if (keyword.equalsIgnoreCase("Kd")) {
                currentMaterial.DiffuseColor = parseVector3f(data);
            }
            else if (keyword.equalsIgnoreCase("Ks")) {
                currentMaterial.SpecularColor = parseVector3f(data);
            }
            else if (keyword.equalsIgnoreCase("Ns")) {
                currentMaterial.SpecularCoefficient = parseFloat(data);
            }
            else if (keyword.equalsIgnoreCase("Tr")) {
                currentMaterial.Transparency = parseFloat(data);
            }
            else if (keyword.equalsIgnoreCase("illum")) {
                currentMaterial.IlluminationModel = parseInt(data);
            }
            else if (keyword.equalsIgnoreCase("map_Ka")) {
                currentMaterial.AmbientTextureMap = data;
                final ResourceLocation resourceLocation = new ResourceLocation(data);
            }
            else if (keyword.equalsIgnoreCase("map_Kd")) {
                currentMaterial.DiffuseTextureMap = data;
                final ResourceLocation resourceLocation2 = new ResourceLocation(data);
            }
            else if (keyword.equalsIgnoreCase("map_Ks")) {
                currentMaterial.SpecularTextureMap = data;
            }
            else if (keyword.equalsIgnoreCase("map_Ns")) {
                currentMaterial.SpecularHighlightTextureMap = data;
            }
            else if (keyword.equalsIgnoreCase("map_d")) {
                currentMaterial.AlphaTextureMap = data;
            }
            else if (keyword.equalsIgnoreCase("map_bump")) {
                currentMaterial.BumpMap = data;
            }
            else if (keyword.equalsIgnoreCase("bump")) {
                currentMaterial.BumpMap = data;
            }
            else if (keyword.equalsIgnoreCase("disp")) {
                currentMaterial.DisplacementMap = data;
            }
            else if (keyword.equalsIgnoreCase("decal")) {
                currentMaterial.StencilDecalMap = data;
            }
            else {
                if (MaterialLibrary.unknownCommands.contains(keyword)) {
                    continue;
                }
                MaterialLibrary.unknownCommands.add(keyword);
            }
        }
    }
    
    private float parseFloat(final String data) {
        return Float.parseFloat(data);
    }
    
    private int parseInt(final String data) {
        return Integer.parseInt(data);
    }
    
    private Vector3f parseVector3f(final String data) {
        final String[] parts = data.split(" ");
        return new Vector3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
    }
    
    private void pushMaterial(final String materialName) {
        currentMaterial = new Material(materialName);
        materialLibrary.put(currentMaterial.Name, currentMaterial);
    }
    
    static {
        unknownCommands = new HashSet<String>();
    }
}
