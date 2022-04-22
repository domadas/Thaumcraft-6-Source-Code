// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.slot.SlotOutput;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import net.minecraft.inventory.Container;

public class ContainerGolemBuilder extends Container
{
    private TileGolemBuilder builder;
    public static boolean redo;
    private int lastCost;
    private int lastMaxCost;
    
    public ContainerGolemBuilder(final InventoryPlayer par1InventoryPlayer, final TileGolemBuilder tileEntity) {
        builder = tileEntity;
        addSlotToContainer(new SlotOutput(tileEntity, 0, 160, 104));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 24 + j * 18, 142 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 24 + i * 18, 200));
        }
    }
    
    public ItemStack slotClick(final int slotId, final int clickedButton, final ClickType mode, final EntityPlayer playerIn) {
        ContainerGolemBuilder.redo = true;
        return super.slotClick(slotId, clickedButton, mode, playerIn);
    }
    
    public void putStackInSlot(final int p_75141_1_, final ItemStack p_75141_2_) {
        ContainerGolemBuilder.redo = true;
        super.putStackInSlot(p_75141_1_, p_75141_2_);
    }
    
    public boolean enchantItem(final EntityPlayer p, final int button) {
        if (button == 99) {
            ContainerGolemBuilder.redo = true;
        }
        return false;
    }
    
    public void addListener(final IContainerListener par1ICrafting) {
        super.addListener(par1ICrafting);
        par1ICrafting.sendWindowProperty(this, 0, builder.cost);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < listeners.size(); ++i) {
            final IContainerListener icrafting = listeners.get(i);
            if (lastCost != builder.cost) {
                icrafting.sendWindowProperty(this, 0, builder.cost);
            }
            if (lastMaxCost != builder.maxCost) {
                icrafting.sendWindowProperty(this, 1, builder.maxCost);
            }
        }
        lastCost = builder.cost;
        lastMaxCost = builder.maxCost;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
        if (par1 == 0) {
            builder.cost = par2;
        }
        if (par1 == 1) {
            builder.maxCost = par2;
        }
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return builder.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slot) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            final ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!builder.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!builder.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            }
            else {
                slotObject.onSlotChanged();
            }
        }
        return stack;
    }
    
    static {
        ContainerGolemBuilder.redo = false;
    }
}
