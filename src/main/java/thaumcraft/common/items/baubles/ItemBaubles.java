// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.baubles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import baubles.api.BaubleType;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IVisDiscountGear;
import baubles.api.IBauble;
import thaumcraft.common.items.ItemTCBase;

public class ItemBaubles extends ItemTCBase implements IBauble, IVisDiscountGear
{
    public ItemBaubles() {
        super("baubles", "amulet_mundane", "ring_mundane", "girdle_mundane", "ring_apprentice", "amulet_fancy", "ring_fancy", "girdle_fancy");
        this.maxStackSize = 1;
        this.setMaxDamage(0);
    }
    
    public BaubleType getBaubleType(final ItemStack itemstack) {
        switch (itemstack.getItemDamage()) {
            case 1:
            case 3:
            case 5: {
                return BaubleType.RING;
            }
            case 2:
            case 6: {
                return BaubleType.BELT;
            }
            default: {
                return BaubleType.AMULET;
            }
        }
    }
    
    public EnumRarity getRarity(final ItemStack stack) {
        if (stack.getItemDamage() >= 3) {
            return EnumRarity.UNCOMMON;
        }
        return super.getRarity(stack);
    }
    
    public int getVisDiscount(final ItemStack stack, final EntityPlayer player) {
        if (stack.getItemDamage() == 3) {
            return 5;
        }
        return 0;
    }
}
