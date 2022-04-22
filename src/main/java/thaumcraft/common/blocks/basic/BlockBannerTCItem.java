// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.util.math.MathHelper;
import thaumcraft.common.tiles.misc.TileBanner;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class BlockBannerTCItem extends ItemBlock
{
    public BlockBannerTCItem(final BlockBannerTC block) {
        super(block);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().getString("aspect") != null && Aspect.getAspect(stack.getTagCompound().getString("aspect")) != null) {
            tooltip.add(Aspect.getAspect(stack.getTagCompound().getString("aspect")).getName());
        }
    }
    
    public EnumActionResult onItemUse(final EntityPlayer player, final World worldIn, BlockPos pos, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (side == EnumFacing.DOWN) {
            return EnumActionResult.FAIL;
        }
        if (!worldIn.getBlockState(pos).getMaterial().isSolid()) {
            return EnumActionResult.FAIL;
        }
        pos = pos.offset(side);
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
        }
        if (!Blocks.STANDING_BANNER.canPlaceBlockAt(worldIn, pos)) {
            return EnumActionResult.FAIL;
        }
        if (worldIn.isRemote) {
            return EnumActionResult.FAIL;
        }
        worldIn.setBlockState(pos, block.getDefaultState(), 3);
        final TileBanner tile = (TileBanner)worldIn.getTileEntity(pos);
        if (tile != null) {
            if (side == EnumFacing.UP) {
                final int i = MathHelper.floor((player.rotationYaw + 180.0f) * 16.0f / 360.0f + 0.5) & 0xF;
                tile.setBannerFacing((byte)i);
            }
            else {
                tile.setWall(true);
                int i = 0;
                if (side == EnumFacing.NORTH) {
                    i = 8;
                }
                if (side == EnumFacing.WEST) {
                    i = 4;
                }
                if (side == EnumFacing.EAST) {
                    i = 12;
                }
                tile.setBannerFacing((byte)i);
            }
            if (player.getHeldItem(hand).hasTagCompound() && player.getHeldItem(hand).getTagCompound().getString("aspect") != null) {
                tile.setAspect(Aspect.getAspect(player.getHeldItem(hand).getTagCompound().getString("aspect")));
            }
            tile.markDirty();
            worldIn.markAndNotifyBlock(pos, worldIn.getChunkFromBlockCoords(pos), block.getDefaultState(), block.getDefaultState(), 3);
        }
        player.getHeldItem(hand).shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
