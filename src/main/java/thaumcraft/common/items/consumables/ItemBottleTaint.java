// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import net.minecraft.util.EnumActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;

public class ItemBottleTaint extends ItemTCBase
{
    public ItemBottleTaint() {
        super("bottle_taint", new String[0]);
        this.maxStackSize = 8;
        this.setMaxDamage(0);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setHasSubtypes(false);
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }
        player.playSound(SoundEvents.ENTITY_EGG_THROW, 0.5f, 0.4f / (ItemBottleTaint.itemRand.nextFloat() * 0.4f + 0.8f));
        if (!world.isRemote) {
            final EntityBottleTaint entityBottle = new EntityBottleTaint(world, player);
            entityBottle.shoot(player, player.rotationPitch, player.rotationYaw, -5.0f, 0.66f, 1.0f);
            world.spawnEntity(entityBottle);
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}