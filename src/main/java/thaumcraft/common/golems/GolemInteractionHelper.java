// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems;

import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.EnumPacketDirection;
import net.minecraftforge.common.util.FakePlayerFactory;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.world.WorldServer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.world.World;

public class GolemInteractionHelper
{
    public static void golemClick(final World world, final IGolemAPI golem, final BlockPos pos, final EnumFacing face, final ItemStack clickStack, final boolean sneaking, final boolean rightClick) {
        final FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
        fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
        fp.setPositionAndRotation(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ, golem.getGolemEntity().rotationYaw, golem.getGolemEntity().rotationPitch);
        final IBlockState bs = world.getBlockState(pos);
        fp.setHeldItem(EnumHand.MAIN_HAND, clickStack);
        fp.setSneaking(sneaking);
        if (!rightClick) {
            try {
                fp.interactionManager.onBlockClicked(pos, face);
            }
            catch (final Exception ex) {}
        }
        else {
            if (fp.getHeldItemMainhand().getItem() instanceof ItemBlock && !mayPlace(world, ((ItemBlock)fp.getHeldItemMainhand().getItem()).getBlock(), pos, face)) {
                golem.getGolemEntity().setPosition(golem.getGolemEntity().posX + face.getFrontOffsetX(), golem.getGolemEntity().posY + face.getFrontOffsetY(), golem.getGolemEntity().posZ + face.getFrontOffsetZ());
            }
            try {
                fp.interactionManager.processRightClickBlock(fp, world, fp.getHeldItemMainhand(), EnumHand.MAIN_HAND, pos, face, 0.5f, 0.5f, 0.5f);
            }
            catch (final Exception ex2) {}
        }
        golem.addRankXp(1);
        if (!fp.getHeldItemMainhand().isEmpty() && fp.getHeldItemMainhand().getCount() <= 0) {
            fp.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }
        dropSomeItems(fp, golem);
        golem.swingArm();
    }
    
    private static boolean mayPlace(final World world, final Block blockIn, final BlockPos pos, final EnumFacing side) {
        final IBlockState block = world.getBlockState(pos);
        final AxisAlignedBB axisalignedbb = blockIn.getBoundingBox(blockIn.getDefaultState(), world, pos);
        return axisalignedbb == null || world.checkNoEntityCollision(axisalignedbb, null);
    }
    
    private static void dropSomeItems(final FakePlayer fp2, final IGolemAPI golem) {
        for (int i = 0; i < fp2.inventory.mainInventory.size(); ++i) {
            if (!fp2.inventory.mainInventory.get(i).isEmpty()) {
                if (golem.canCarry(fp2.inventory.mainInventory.get(i), true)) {
                    fp2.inventory.mainInventory.set(i, golem.holdItem(fp2.inventory.mainInventory.get(i)));
                }
                if (!fp2.inventory.mainInventory.get(i).isEmpty() && fp2.inventory.mainInventory.get(i).getCount() > 0) {
                    InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), fp2.inventory.mainInventory.get(i), golem.getGolemEntity());
                }
                fp2.inventory.mainInventory.set(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < fp2.inventory.armorInventory.size(); ++i) {
            if (!fp2.inventory.armorInventory.get(i).isEmpty()) {
                if (golem.canCarry(fp2.inventory.armorInventory.get(i), true)) {
                    fp2.inventory.armorInventory.set(i, golem.holdItem(fp2.inventory.armorInventory.get(i)));
                }
                if (!fp2.inventory.mainInventory.get(i).isEmpty() && fp2.inventory.armorInventory.get(i).getCount() > 0) {
                    InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), fp2.inventory.armorInventory.get(i), golem.getGolemEntity());
                }
                fp2.inventory.armorInventory.set(i, ItemStack.EMPTY);
            }
        }
    }
}
