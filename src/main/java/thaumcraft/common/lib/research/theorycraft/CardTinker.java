// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import java.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardTinker extends TheorycraftCard
{
    ItemStack stack;
    static ItemStack[] options;
    
    public CardTinker() {
        this.stack = ItemStack.EMPTY;
    }
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setTag("stack", this.stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        this.stack = new ItemStack(nbt.getCompoundTag("stack"));
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        final Random r = new Random(this.getSeed());
        this.stack = CardTinker.options[r.nextInt(CardTinker.options.length)].copy();
        return this.stack != null;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ARTIFICE";
    }
    
    private int getVal() {
        int q = 0;
        try {
            q += (int)Math.sqrt(ThaumcraftCraftingManager.getObjectTags(this.stack).visSize());
        }
        catch (final Exception ex) {}
        return q;
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.tinker.name", new Object[0]).getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        final int a = this.getVal() * 2;
        final int b = a + 10;
        return new TextComponentTranslation("card.tinker.text", new Object[] { a, b }).getFormattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { this.stack };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        final int q = this.getVal() * 2;
        data.addTotal(this.getResearchCategory(), MathHelper.getInt(player.getRNG(), q, q + 10));
        return true;
    }
    
    static {
        CardTinker.options = new ItemStack[] { new ItemStack(ItemsTC.visResonator), new ItemStack(ItemsTC.thaumometer), new ItemStack(Blocks.ANVIL), new ItemStack(Blocks.ACTIVATOR_RAIL), new ItemStack(Blocks.DISPENSER), new ItemStack(Blocks.DROPPER), new ItemStack(Blocks.ENCHANTING_TABLE), new ItemStack(Blocks.ENDER_CHEST), new ItemStack(Blocks.JUKEBOX), new ItemStack(Blocks.DAYLIGHT_DETECTOR), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.HOPPER), new ItemStack(Blocks.STICKY_PISTON), new ItemStack(Items.MAP), new ItemStack(Items.COMPASS), new ItemStack(Items.TNT_MINECART), new ItemStack(Items.COMPARATOR), new ItemStack(Items.CLOCK) };
    }
}
