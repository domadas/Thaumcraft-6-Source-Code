// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import thaumcraft.common.lib.events.ServerEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.InventoryFake;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import thaumcraft.api.crafting.IDustTrigger;

public class DustTriggerSimple implements IDustTrigger
{
    Block target;
    ItemStack result;
    String research;
    
    public DustTriggerSimple(final String research, final Block target, final ItemStack result) {
        this.target = target;
        this.result = result;
        this.research = research;
    }
    
    @Override
    public Placement getValidFace(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing face) {
        return (world.getBlockState(pos).getBlock() == target && (research == null || ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research))) ? new Placement(0, 0, 0, null) : null;
    }
    
    @Override
    public void execute(final World world, final EntityPlayer player, final BlockPos pos, final Placement placement, final EnumFacing side) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, result, new InventoryFake(1));
        final IBlockState state = world.getBlockState(pos);
        ServerEvents.addRunnableServer(world, new Runnable() {
            @Override
            public void run() {
                ServerEvents.addSwapper(world, pos, state, result, false, 0, player, true, true, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0f);
            }
        }, 50);
    }
}
