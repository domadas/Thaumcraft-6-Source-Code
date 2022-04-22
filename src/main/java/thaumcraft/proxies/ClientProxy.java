// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.proxies;

import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import thaumcraft.client.ColorHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thaumcraft.client.lib.ender.ShaderHelper;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.common.lib.events.KeyHandler;

public class ClientProxy extends CommonProxy
{
    ProxyEntities proxyEntities;
    ProxyTESR proxyTESR;
    KeyHandler kh;
    
    public ClientProxy() {
        proxyEntities = new ProxyEntities();
        proxyTESR = new ProxyTESR();
        kh = new KeyHandler();
    }
    
    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        super.preInit(event);
        OBJLoader.INSTANCE.addDomain("thaumcraft".toLowerCase());
        ShaderHelper.initShaders();
    }
    
    @Override
    public void init(final FMLInitializationEvent event) {
        super.init(event);
        ColorHandler.registerColourHandlers();
        registerKeyBindings();
        proxyEntities.setupEntityRenderers();
        proxyTESR.setupTESR();
    }
    
    @Override
    public void postInit(final FMLPostInitializationEvent event) {
        super.postInit(event);
    }
    
    public void registerKeyBindings() {
        MinecraftForge.EVENT_BUS.register(kh);
    }
    
    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().world;
    }
    
    @Override
    public World getWorld(final int dim) {
        return getClientWorld();
    }
    
    @Override
    public boolean getSingleplayer() {
        return Minecraft.getMinecraft().isSingleplayer();
    }
    
    @Override
    public boolean isShiftKeyDown() {
        return GuiScreen.isShiftKeyDown();
    }
    
    public void setOtherBlockRenderers() {
    }
    
    @Override
    public void registerModel(final ItemBlock itemBlock) {
        ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(itemBlock.getRegistryName(), "inventory"));
    }
}
