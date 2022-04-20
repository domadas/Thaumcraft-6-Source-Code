// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.construct;

import java.util.List;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.items.ItemTCBase;

public class ItemTurretPlacer extends ItemTCBase
{
    public ItemTurretPlacer() {
        super("turret", new String[] { "basic", "advanced", "bore" });
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        if (side == EnumFacing.DOWN) {
            return EnumActionResult.PASS;
        }
        final boolean flag = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
        final BlockPos blockpos = flag ? pos : pos.offset(side);
        if (!player.canPlayerEdit(blockpos, side, player.getHeldItem(hand))) {
            return EnumActionResult.PASS;
        }
        final BlockPos blockpos2 = blockpos.up();
        boolean flag2 = !world.isAirBlock(blockpos) && !world.getBlockState(blockpos).getBlock().isReplaceable(world, blockpos);
        flag2 |= (!world.isAirBlock(blockpos2) && !world.getBlockState(blockpos2).getBlock().isReplaceable(world, blockpos2));
        if (flag2) {
            return EnumActionResult.PASS;
        }
        final double d0 = blockpos.getX();
        final double d2 = blockpos.getY();
        final double d3 = blockpos.getZ();
        final List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(d0, d2, d3, d0 + 1.0, d2 + 2.0, d3 + 1.0));
        if (!list.isEmpty()) {
            return EnumActionResult.PASS;
        }
        if (!world.isRemote) {
            world.setBlockToAir(blockpos);
            world.setBlockToAir(blockpos2);
            EntityOwnedConstruct turret = null;
            switch (player.getHeldItem(hand).getItemDamage()) {
                case 0: {
                    turret = new EntityTurretCrossbow(world, blockpos);
                    break;
                }
                case 1: {
                    turret = new EntityTurretCrossbowAdvanced(world, blockpos);
                    break;
                }
                case 2: {
                    turret = new EntityArcaneBore(world, blockpos, player.getHorizontalFacing());
                    break;
                }
            }
            if (turret != null) {
                world.spawnEntity(turret);
                turret.setOwned(true);
                turret.setValidSpawn();
                turret.setOwnerId(player.getUniqueID());
                world.playSound(null, turret.posX, turret.posY, turret.posZ, SoundEvents.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75f, 0.8f);
            }
            player.getHeldItem(hand).shrink(1);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
