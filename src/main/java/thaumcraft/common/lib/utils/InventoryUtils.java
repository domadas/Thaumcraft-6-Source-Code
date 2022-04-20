// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.utils;

import net.minecraftforge.common.crafting.IngredientNBT;
import thaumcraft.api.crafting.IngredientNBTTC;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.common.entities.EntityFollowingItem;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.util.Tuple;
import net.minecraft.util.NonNullList;
import net.minecraft.inventory.EntityEquipmentSlot;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.IInventory;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.api.ThaumcraftInvHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;

public class InventoryUtils
{
    public static ItemStack copyMaxedStack(final ItemStack stack) {
        return copyLimitedStack(stack, stack.getMaxStackSize());
    }
    
    public static ItemStack copyLimitedStack(final ItemStack stack, final int limit) {
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        final ItemStack s = stack.copy();
        if (s.getCount() > limit) {
            s.setCount(limit);
        }
        return s;
    }
    
    public static boolean consumeItemsFromAdjacentInventoryOrPlayer(final World world, final BlockPos pos, final EntityPlayer player, final boolean sim, final ItemStack... items) {
        for (final ItemStack stack : items) {
            boolean b = checkAdjacentChests(world, pos, stack);
            if (!b) {
                b = isPlayerCarryingAmount(player, stack, true);
            }
            if (!b) {
                return false;
            }
        }
        if (!sim) {
            for (final ItemStack stack : items) {
                if (!consumeFromAdjacentChests(world, pos, stack.copy())) {
                    consumePlayerItem(player, stack, true, true);
                }
            }
        }
        return true;
    }
    
    public static boolean checkAdjacentChests(final World world, final BlockPos pos, final ItemStack itemStack) {
        int c = itemStack.getCount();
        for (final EnumFacing face : EnumFacing.VALUES) {
            if (face != EnumFacing.UP) {
                c -= ThaumcraftInvHelper.countTotalItemsIn(world, pos.offset(face), face.getOpposite(), itemStack.copy(), ThaumcraftInvHelper.InvFilter.BASEORE);
                if (c <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean consumeFromAdjacentChests(final World world, final BlockPos pos, final ItemStack itemStack) {
        for (final EnumFacing face : EnumFacing.VALUES) {
            if (face != EnumFacing.UP) {
                if (!itemStack.isEmpty()) {
                    final ItemStack os = removeStackFrom(world, pos.offset(face), face.getOpposite(), itemStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
                    itemStack.setCount(itemStack.getCount() - os.getCount());
                    if (itemStack.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return itemStack.isEmpty();
    }
    
    @Deprecated
    public static ItemStack insertStackAt(final World world, final BlockPos pos, final EnumFacing side, final ItemStack stack, final boolean simulate) {
        return ThaumcraftInvHelper.insertStackAt(world, pos, side, stack, simulate);
    }
    
    public static void ejectStackAt(final World world, final BlockPos pos, final EnumFacing side, final ItemStack out) {
        ejectStackAt(world, pos, side, out, false);
    }
    
    public static ItemStack ejectStackAt(final World world, BlockPos pos, final EnumFacing side, ItemStack out, final boolean smart) {
        out = ThaumcraftInvHelper.insertStackAt(world, pos.offset(side), side.getOpposite(), out, false);
        if (smart && ThaumcraftInvHelper.getItemHandlerAt(world, pos.offset(side), side.getOpposite()) != null) {
            return out;
        }
        if (!out.isEmpty()) {
            if (world.isBlockFullCube(pos.offset(side))) {
                pos = pos.offset(side.getOpposite());
            }
            final EntityItem entityitem2 = new EntityItem(world, (float)pos.getX() + 0.5 + 1 * side.getFrontOffsetX(), pos.getY() + (float)(1 * side.getFrontOffsetY()), (float)pos.getZ() + 0.5 + 1 * side.getFrontOffsetZ(), out);
            entityitem2.motionX = 0.3 * side.getFrontOffsetX();
            entityitem2.motionY = 0.3 * side.getFrontOffsetY();
            entityitem2.motionZ = 0.3 * side.getFrontOffsetZ();
            world.spawnEntity(entityitem2);
        }
        return ItemStack.EMPTY;
    }
    
    public static ItemStack removeStackFrom(final World world, final BlockPos pos, final EnumFacing side, final ItemStack stack, final ThaumcraftInvHelper.InvFilter filter, final boolean simulate) {
        return removeStackFrom(ThaumcraftInvHelper.getItemHandlerAt(world, pos, side), stack, filter, simulate);
    }
    
    public static ItemStack removeStackFrom(final IItemHandler inventory, final ItemStack stack, final ThaumcraftInvHelper.InvFilter filter, final boolean simulate) {
        final int amount = stack.getCount();
        int removed = 0;
        if (inventory != null) {
            for (int a = 0; a < inventory.getSlots(); ++a) {
                if (areItemStacksEqual(stack, inventory.getStackInSlot(a), filter)) {
                    final int s = Math.min(amount - removed, inventory.getStackInSlot(a).getCount());
                    final ItemStack es = inventory.extractItem(a, s, simulate);
                    if (es != null && !es.isEmpty()) {
                        removed += es.getCount();
                    }
                }
                if (removed >= amount) {
                    break;
                }
            }
        }
        if (removed == 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack s2 = stack.copy();
        s2.setCount(removed);
        return s2;
    }
    
    public static int countStackInWorld(final World world, final BlockPos pos, final ItemStack stack, final double range, final ThaumcraftInvHelper.InvFilter filter) {
        int count = 0;
        final List<EntityItem> l = EntityUtils.getEntitiesInRange(world, pos, null, EntityItem.class, range);
        for (final EntityItem ei : l) {
            if (ei.getItem() != null && ei.getItem().isEmpty() && areItemStacksEqual(stack, ei.getItem(), filter)) {
                count += ei.getItem().getCount();
            }
        }
        return count;
    }
    
    public static void dropItems(final World world, final BlockPos pos) {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        final IInventory inventory = (IInventory)tileEntity;
        InventoryHelper.dropInventoryItems(world, pos, inventory);
    }
    
    public static boolean consumePlayerItem(final EntityPlayer player, final ItemStack item, final boolean nocheck, final boolean ore) {
        if (!nocheck && !isPlayerCarryingAmount(player, item, ore)) {
            return false;
        }
        int count = item.getCount();
        for (int var2 = 0; var2 < player.inventory.mainInventory.size(); ++var2) {
            if (checkEnchantedPlaceholder(item, player.inventory.mainInventory.get(var2)) || areItemStacksEqual(player.inventory.mainInventory.get(var2), item, new ThaumcraftInvHelper.InvFilter(false, !item.hasTagCompound(), ore, false).setRelaxedNBT())) {
                if (player.inventory.mainInventory.get(var2).getCount() > count) {
                    player.inventory.mainInventory.get(var2).shrink(count);
                    count = 0;
                }
                else {
                    count -= player.inventory.mainInventory.get(var2).getCount();
                    player.inventory.mainInventory.set(var2, ItemStack.EMPTY);
                }
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean consumePlayerItem(final EntityPlayer player, final Item item, final int md, final int amt) {
        if (!isPlayerCarryingAmount(player, new ItemStack(item, amt, md), false)) {
            return false;
        }
        int count = amt;
        for (int var2 = 0; var2 < player.inventory.mainInventory.size(); ++var2) {
            if (player.inventory.mainInventory.get(var2).getItem() == item && player.inventory.mainInventory.get(var2).getItemDamage() == md) {
                if (player.inventory.mainInventory.get(var2).getCount() > count) {
                    player.inventory.mainInventory.get(var2).shrink(count);
                    count = 0;
                }
                else {
                    count -= player.inventory.mainInventory.get(var2).getCount();
                    player.inventory.mainInventory.set(var2, ItemStack.EMPTY);
                }
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean consumePlayerItem(final EntityPlayer player, final Item item, final int md) {
        for (int var2 = 0; var2 < player.inventory.mainInventory.size(); ++var2) {
            if (player.inventory.mainInventory.get(var2).getItem() == item && player.inventory.mainInventory.get(var2).getItemDamage() == md) {
                player.inventory.mainInventory.get(var2).shrink(1);
                if (player.inventory.mainInventory.get(var2).getCount() <= 0) {
                    player.inventory.mainInventory.set(var2, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPlayerCarryingAmount(final EntityPlayer player, final ItemStack stack, final boolean ore) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        int count = stack.getCount();
        for (int var2 = 0; var2 < player.inventory.mainInventory.size(); ++var2) {
            if (checkEnchantedPlaceholder(stack, player.inventory.mainInventory.get(var2)) || areItemStacksEqual(player.inventory.mainInventory.get(var2), stack, new ThaumcraftInvHelper.InvFilter(false, !stack.hasTagCompound(), ore, false).setRelaxedNBT())) {
                count -= player.inventory.mainInventory.get(var2).getCount();
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean checkEnchantedPlaceholder(final ItemStack stack, final ItemStack stack2) {
        if (stack.getItem() != ItemsTC.enchantedPlaceholder) {
            return false;
        }
        final Map<Enchantment, Integer> en = EnchantmentHelper.getEnchantments(stack);
        boolean b = !en.isEmpty();
    Label_0181:
        for (final Enchantment e : en.keySet()) {
            final Map<Enchantment, Integer> en2 = EnchantmentHelper.getEnchantments(stack2);
            if (en2.isEmpty()) {
                return false;
            }
            b = false;
            for (final Enchantment e2 : en2.keySet()) {
                if (!e2.equals(e)) {
                    continue;
                }
                b = true;
                if (en2.get(e2) < en.get(e)) {
                    b = false;
                    break Label_0181;
                }
            }
        }
        return b;
    }
    
    public static EntityEquipmentSlot isHoldingItem(final EntityPlayer player, final Item item) {
        if (player == null || item == null) {
            return null;
        }
        if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == item) {
            return EntityEquipmentSlot.MAINHAND;
        }
        if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == item) {
            return EntityEquipmentSlot.OFFHAND;
        }
        return null;
    }
    
    public static EntityEquipmentSlot isHoldingItem(final EntityPlayer player, final Class item) {
        if (player == null || item == null) {
            return null;
        }
        if (player.getHeldItemMainhand() != null && item.isAssignableFrom(player.getHeldItemMainhand().getItem().getClass())) {
            return EntityEquipmentSlot.MAINHAND;
        }
        if (player.getHeldItemOffhand() != null && item.isAssignableFrom(player.getHeldItemOffhand().getItem().getClass())) {
            return EntityEquipmentSlot.OFFHAND;
        }
        return null;
    }
    
    public static int getPlayerSlotFor(final EntityPlayer player, final ItemStack stack) {
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            if (!player.inventory.mainInventory.get(i).isEmpty() && stackEqualExact(stack, player.inventory.mainInventory.get(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean stackEqualExact(final ItemStack stack1, final ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
    
    public static boolean areItemStacksEqualStrict(final ItemStack stack0, final ItemStack stack1) {
        return areItemStacksEqual(stack0, stack1, ThaumcraftInvHelper.InvFilter.STRICT);
    }
    
    public static ItemStack findFirstMatchFromFilter(final NonNullList<ItemStack> filterStacks, final boolean blacklist, final IItemHandler inv, final EnumFacing face, final ThaumcraftInvHelper.InvFilter filter) {
        return findFirstMatchFromFilter(filterStacks, blacklist, inv, face, filter, false);
    }
    
    public static ItemStack findFirstMatchFromFilter(final NonNullList<ItemStack> filterStacks, final boolean blacklist, final IItemHandler inv, final EnumFacing face, final ThaumcraftInvHelper.InvFilter filter, final boolean leaveOne) {
    Label_0181:
        for (int a = 0; a < inv.getSlots(); ++a) {
            final ItemStack is = inv.getStackInSlot(a);
            if (is != null && !is.isEmpty()) {
                if (is.getCount() > 0) {
                    if (!leaveOne || ThaumcraftInvHelper.countTotalItemsIn(inv, is, filter) >= 2) {
                        boolean allow = false;
                        boolean allEmpty = true;
                        for (final ItemStack fs : filterStacks) {
                            if (fs != null) {
                                if (fs.isEmpty()) {
                                    continue;
                                }
                                allEmpty = false;
                                final boolean r = areItemStacksEqual(fs.copy(), is.copy(), filter);
                                if (blacklist) {
                                    if (r) {
                                        continue Label_0181;
                                    }
                                    allow = true;
                                }
                                else {
                                    if (r) {
                                        return is;
                                    }
                                    continue;
                                }
                            }
                        }
                        if (blacklist && (allow || allEmpty)) {
                            return is;
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
    
    public static boolean matchesFilters(final NonNullList<ItemStack> nonNullList, final boolean blacklist, final ItemStack is, final ThaumcraftInvHelper.InvFilter filter) {
        if (is == null || is.isEmpty() || is.getCount() <= 0) {
            return false;
        }
        boolean allow = false;
        boolean allEmpty = true;
        for (final ItemStack fs : nonNullList) {
            if (fs != null) {
                if (fs.isEmpty()) {
                    continue;
                }
                allEmpty = false;
                final boolean r = areItemStacksEqual(fs.copy(), is.copy(), filter);
                if (blacklist) {
                    if (r) {
                        return false;
                    }
                    allow = true;
                }
                else {
                    if (r) {
                        return true;
                    }
                    continue;
                }
            }
        }
        return blacklist && (allow || allEmpty);
    }
    
    public static ItemStack findFirstMatchFromFilter(final NonNullList<ItemStack> filterStacks, final NonNullList<Integer> filterStacksSizes, final boolean blacklist, final NonNullList<ItemStack> itemStacks, final ThaumcraftInvHelper.InvFilter filter) {
        return findFirstMatchFromFilterTuple(filterStacks, filterStacksSizes, blacklist, itemStacks, filter).getFirst();
    }
    
    public static Tuple<ItemStack, Integer> findFirstMatchFromFilterTuple(final NonNullList<ItemStack> filterStacks, final NonNullList<Integer> filterStacksSizes, final boolean blacklist, final NonNullList<ItemStack> stacks, final ThaumcraftInvHelper.InvFilter filter) {
    Label_0006:
        for (final ItemStack is : stacks) {
            if (is != null && !is.isEmpty()) {
                if (is.getCount() <= 0) {
                    continue;
                }
                boolean allow = false;
                boolean allEmpty = true;
                for (int idx = 0; idx < filterStacks.size(); ++idx) {
                    final ItemStack fs = filterStacks.get(idx);
                    if (fs != null) {
                        if (!fs.isEmpty()) {
                            allEmpty = false;
                            final boolean r = areItemStacksEqual(fs.copy(), is.copy(), filter);
                            if (blacklist) {
                                if (r) {
                                    continue Label_0006;
                                }
                                allow = true;
                            }
                            else if (r) {
                                return (Tuple<ItemStack, Integer>)new Tuple(is, filterStacksSizes.get(idx));
                            }
                        }
                    }
                }
                if (blacklist && (allow || allEmpty)) {
                    return (Tuple<ItemStack, Integer>)new Tuple(is, 0);
                }
                continue;
            }
        }
        return (Tuple<ItemStack, Integer>)new Tuple(ItemStack.EMPTY, 0);
    }
    
    public static boolean areItemStacksEqual(final ItemStack stack0, final ItemStack stack1, final ThaumcraftInvHelper.InvFilter filter) {
        if (stack0 == null && stack1 != null) {
            return false;
        }
        if (stack0 != null && stack1 == null) {
            return false;
        }
        if (stack0 == null && stack1 == null) {
            return true;
        }
        if (stack0.isEmpty() && !stack1.isEmpty()) {
            return false;
        }
        if (!stack0.isEmpty() && stack1.isEmpty()) {
            return false;
        }
        if (stack0.isEmpty() && stack1.isEmpty()) {
            return true;
        }
        if (filter.useMod) {
            String m1 = "A";
            String m2 = "B";
            final String a = stack0.getItem().getRegistryName().getResourceDomain();
            if (a != null) {
                m1 = a;
            }
            final String b = stack1.getItem().getRegistryName().getResourceDomain();
            if (b != null) {
                m2 = b;
            }
            return m1.equals(m2);
        }
        if (filter.useOre && !stack0.isEmpty()) {
            final int[] oreIDs;
            final int[] od = oreIDs = OreDictionary.getOreIDs(stack0);
            for (final int i : oreIDs) {
                if (ThaumcraftInvHelper.containsMatch(false, new ItemStack[] { stack1 }, OreDictionary.getOres(OreDictionary.getOreName(i), false))) {
                    return true;
                }
            }
        }
        boolean t1 = true;
        if (!filter.igNBT) {
            t1 = (filter.relaxedNBT ? ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(stack0, stack1) : ItemStack.areItemStackTagsEqual(stack0, stack1));
        }
        if (stack0.getItemDamage() == 32767 || stack1.getItemDamage() == 32767) {
            filter.igDmg = true;
        }
        final boolean t2 = !filter.igDmg && stack0.getItemDamage() != stack1.getItemDamage();
        return stack0.getItem() == stack1.getItem() && !t2 && t1;
    }
    
    public static void dropHarvestsAtPos(final World worldIn, final BlockPos pos, final List<ItemStack> list) {
        dropHarvestsAtPos(worldIn, pos, list, false, 0, null);
    }
    
    public static void dropHarvestsAtPos(final World worldIn, final BlockPos pos, final List<ItemStack> list, final boolean followItem, final int color, final Entity target) {
        for (final ItemStack item : list) {
            if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) {
                final float f = 0.5f;
                final double d0 = worldIn.rand.nextFloat() * f + (1.0f - f) * 0.5;
                final double d2 = worldIn.rand.nextFloat() * f + (1.0f - f) * 0.5;
                final double d3 = worldIn.rand.nextFloat() * f + (1.0f - f) * 0.5;
                EntityItem entityitem = null;
                if (followItem) {
                    entityitem = new EntityFollowingItem(worldIn, pos.getX() + d0, pos.getY() + d2, pos.getZ() + d3, item, target, color);
                }
                else {
                    entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d2, pos.getZ() + d3, item);
                }
                entityitem.setDefaultPickupDelay();
                worldIn.spawnEntity(entityitem);
            }
        }
    }
    
    public static void dropItemAtPos(final World world, final ItemStack item, final BlockPos pos) {
        if (!world.isRemote && item != null && !item.isEmpty() && item.getCount() > 0) {
            final EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item.copy());
            world.spawnEntity(entityItem);
        }
    }
    
    public static void dropItemAtEntity(final World world, final ItemStack item, final Entity entity) {
        if (!world.isRemote && item != null && !item.isEmpty() && item.getCount() > 0) {
            final EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY + entity.getEyeHeight() / 2.0f, entity.posZ, item.copy());
            world.spawnEntity(entityItem);
        }
    }
    
    public static void dropItemsAtEntity(final World world, final BlockPos pos, final Entity entity) {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof IInventory) || world.isRemote) {
            return;
        }
        final IInventory inventory = (IInventory)tileEntity;
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            final ItemStack item = inventory.getStackInSlot(i);
            if (!item.isEmpty() && item.getCount() > 0) {
                final EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY + entity.getEyeHeight() / 2.0f, entity.posZ, item.copy());
                world.spawnEntity(entityItem);
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }
    
    public static ItemStack cycleItemStack(final Object input) {
        return cycleItemStack(input, 0);
    }
    
    public static ItemStack cycleItemStack(Object input, int counter) {
        ItemStack it = ItemStack.EMPTY;
        if (input instanceof Ingredient) {
            final boolean b = !((Ingredient)input).isSimple() && !(input instanceof IngredientNBTTC) && !(input instanceof IngredientNBT);
            input = ((Ingredient)input).getMatchingStacks();
            if (b) {
                final ItemStack[] q = (ItemStack[])input;
                final ItemStack[] r = new ItemStack[q.length];
                for (int a = 0; a < q.length; ++a) {
                    (r[a] = q[a].copy()).setItemDamage(32767);
                }
                input = r;
            }
        }
        if (input instanceof ItemStack[]) {
            final ItemStack[] q2 = (ItemStack[])input;
            if (q2 != null && q2.length > 0) {
                final int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q2.length);
                it = cycleItemStack(q2[idx], counter++);
            }
        }
        else if (input instanceof ItemStack) {
            it = (ItemStack)input;
            if (it != null && !it.isEmpty() && it.getItem() != null && it.isItemStackDamageable() && it.getItemDamage() == 32767) {
                final int q3 = 5000 / it.getMaxDamage();
                final int md = (int)((counter + System.currentTimeMillis() / q3) % it.getMaxDamage());
                final ItemStack it2 = new ItemStack(it.getItem(), 1, md);
                it2.setTagCompound(it.getTagCompound());
                it = it2;
            }
        }
        else if (input instanceof List) {
            final List<ItemStack> q4 = (List<ItemStack>)input;
            if (q4 != null && q4.size() > 0) {
                final int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q4.size());
                it = cycleItemStack(q4.get(idx), counter++);
            }
        }
        else if (input instanceof String) {
            final List<ItemStack> q4 = OreDictionary.getOres((String)input, false);
            if (q4 != null && q4.size() > 0) {
                final int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q4.size());
                it = cycleItemStack(q4.get(idx), counter++);
            }
        }
        return it;
    }
}
