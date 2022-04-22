// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import java.util.Set;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.api.crafting.IInfusionStabiliser;
import net.minecraft.init.Blocks;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.InventoryFake;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.potions.PotionVisExhaust;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.lib.events.EssentiaHandler;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.InfusionRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import java.util.List;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.devices.BlockPedestal;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import java.text.DecimalFormat;
import net.minecraft.block.Block;
import java.util.HashMap;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import thaumcraft.api.items.IGogglesDisplayExtended;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileInfusionMatrix extends TileThaumcraft implements IInteractWithCaster, IAspectContainer, ITickable, IGogglesDisplayExtended
{
    private ArrayList<BlockPos> pedestals;
    private int dangerCount;
    public boolean active;
    public boolean crafting;
    public boolean checkSurroundings;
    public float costMult;
    private int cycleTime;
    public int stabilityCap;
    public float stability;
    public float stabilityReplenish;
    private AspectList recipeEssentia;
    private ArrayList<ItemStack> recipeIngredients;
    private Object recipeOutput;
    private String recipePlayer;
    private String recipeOutputLabel;
    private ItemStack recipeInput;
    private int recipeInstability;
    private int recipeXP;
    private int recipeType;
    public HashMap<String, SourceFX> sourceFX;
    public int count;
    public int craftCount;
    public float startUp;
    private int countDelay;
    ArrayList<ItemStack> ingredients;
    int itemCount;
    private ArrayList<BlockPos> problemBlocks;
    HashMap<Block, Integer> tempBlockCount;
    static DecimalFormat myFormatter;
    
    public TileInfusionMatrix() {
        pedestals = new ArrayList<BlockPos>();
        dangerCount = 0;
        active = false;
        crafting = false;
        checkSurroundings = true;
        costMult = 0.0f;
        cycleTime = 20;
        stabilityCap = 25;
        stability = 0.0f;
        stabilityReplenish = 0.0f;
        recipeEssentia = new AspectList();
        recipeIngredients = null;
        recipeOutput = null;
        recipePlayer = null;
        recipeOutputLabel = null;
        recipeInput = null;
        recipeInstability = 0;
        recipeXP = 0;
        recipeType = 0;
        sourceFX = new HashMap<String, SourceFX>();
        count = 0;
        craftCount = 0;
        countDelay = cycleTime / 2;
        ingredients = new ArrayList<ItemStack>();
        itemCount = 0;
        problemBlocks = new ArrayList<BlockPos>();
        tempBlockCount = new HashMap<Block, Integer>();
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 0.1, getPos().getY() - 0.1, getPos().getZ() - 0.1, getPos().getX() + 1.1, getPos().getY() + 1.1, getPos().getZ() + 1.1);
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbtCompound) {
        active = nbtCompound.getBoolean("active");
        crafting = nbtCompound.getBoolean("crafting");
        stability = nbtCompound.getFloat("stability");
        recipeInstability = nbtCompound.getInteger("recipeinst");
        recipeEssentia.readFromNBT(nbtCompound);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbtCompound) {
        nbtCompound.setBoolean("active", active);
        nbtCompound.setBoolean("crafting", crafting);
        nbtCompound.setFloat("stability", stability);
        nbtCompound.setInteger("recipeinst", recipeInstability);
        recipeEssentia.writeToNBT(nbtCompound);
        return nbtCompound;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        final NBTTagList nbttaglist = nbtCompound.getTagList("recipein", 10);
        recipeIngredients = new ArrayList<ItemStack>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            recipeIngredients.add(new ItemStack(nbttagcompound1));
        }
        final String rot = nbtCompound.getString("rotype");
        if (rot != null && rot.equals("@")) {
            recipeOutput = new ItemStack(nbtCompound.getCompoundTag("recipeout"));
        }
        else if (rot != null) {
            recipeOutputLabel = rot;
            recipeOutput = nbtCompound.getTag("recipeout");
        }
        recipeInput = new ItemStack(nbtCompound.getCompoundTag("recipeinput"));
        recipeType = nbtCompound.getInteger("recipetype");
        recipeXP = nbtCompound.getInteger("recipexp");
        recipePlayer = nbtCompound.getString("recipeplayer");
        if (recipePlayer.isEmpty()) {
            recipePlayer = null;
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        if (recipeIngredients != null && recipeIngredients.size() > 0) {
            final NBTTagList nbttaglist = new NBTTagList();
            for (final ItemStack stack : recipeIngredients) {
                if (!stack.isEmpty()) {
                    final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("item", (byte) count);
                    stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                    ++count;
                }
            }
            nbtCompound.setTag("recipein", nbttaglist);
        }
        if (recipeOutput != null && recipeOutput instanceof ItemStack) {
            nbtCompound.setString("rotype", "@");
        }
        if (recipeOutput != null && recipeOutput instanceof NBTBase) {
            nbtCompound.setString("rotype", recipeOutputLabel);
        }
        if (recipeOutput != null && recipeOutput instanceof ItemStack) {
            nbtCompound.setTag("recipeout", ((ItemStack) recipeOutput).writeToNBT(new NBTTagCompound()));
        }
        if (recipeOutput != null && recipeOutput instanceof NBTBase) {
            nbtCompound.setTag("recipeout", (NBTBase) recipeOutput);
        }
        if (recipeInput != null) {
            nbtCompound.setTag("recipeinput", recipeInput.writeToNBT(new NBTTagCompound()));
        }
        nbtCompound.setInteger("recipetype", recipeType);
        nbtCompound.setInteger("recipexp", recipeXP);
        if (recipePlayer == null) {
            nbtCompound.setString("recipeplayer", "");
        }
        else {
            nbtCompound.setString("recipeplayer", recipePlayer);
        }
        return nbtCompound;
    }
    
    private EnumStability getStability() {
        return (stability > stabilityCap / 2) ? EnumStability.VERY_STABLE : ((stability >= 0.0f) ? EnumStability.STABLE : ((stability > -25.0f) ? EnumStability.UNSTABLE : EnumStability.VERY_UNSTABLE));
    }
    
    private float getModFromCurrentStability() {
        switch (getStability()) {
            case VERY_STABLE: {
                return 5.0f;
            }
            case STABLE: {
                return 6.0f;
            }
            case UNSTABLE: {
                return 7.0f;
            }
            case VERY_UNSTABLE: {
                return 8.0f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public void update() {
        ++count;
        if (checkSurroundings) {
            checkSurroundings = false;
            getSurroundings();
        }
        if (world.isRemote) {
            doEffects();
        }
        else {
            if (count % (crafting ? 20 : 100) == 0 && !validLocation()) {
                active = false;
                markDirty();
                syncTile(false);
                return;
            }
            if (active && !crafting && stability < stabilityCap && count % Math.max(5, countDelay) == 0) {
                stability += Math.max(0.1f, stabilityReplenish);
                if (stability > stabilityCap) {
                    stability = (float) stabilityCap;
                }
                markDirty();
                syncTile(false);
            }
            if (active && crafting && count % countDelay == 0) {
                craftCycle();
                markDirty();
            }
        }
    }
    
    public boolean validLocation() {
        return world.getBlockState(pos.add(0, -2, 0)).getBlock() instanceof BlockPedestal && world.getBlockState(pos.add(1, -2, 1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(1, -2, -1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar;
    }
    
    public void craftingStart(final EntityPlayer player) {
        if (!validLocation()) {
            active = false;
            markDirty();
            syncTile(false);
            return;
        }
        getSurroundings();
        TileEntity te = null;
        recipeInput = ItemStack.EMPTY;
        te = world.getTileEntity(pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            final TilePedestal ped = (TilePedestal)te;
            if (!ped.getStackInSlot(0).isEmpty()) {
                recipeInput = ped.getStackInSlot(0).copy();
            }
        }
        if (recipeInput == null || recipeInput.isEmpty()) {
            return;
        }
        final ArrayList<ItemStack> components = new ArrayList<ItemStack>();
        for (final BlockPos cc : pedestals) {
            te = world.getTileEntity(cc);
            if (te != null && te instanceof TilePedestal) {
                final TilePedestal ped2 = (TilePedestal)te;
                if (ped2.getStackInSlot(0).isEmpty()) {
                    continue;
                }
                components.add(ped2.getStackInSlot(0).copy());
            }
        }
        if (components.size() == 0) {
            return;
        }
        final InfusionRecipe recipe = ThaumcraftCraftingManager.findMatchingInfusionRecipe(components, recipeInput, player);
        if (costMult < 0.5) {
            costMult = 0.5f;
        }
        if (recipe != null) {
            recipeType = 0;
            recipeIngredients = components;
            if (recipe.getRecipeOutput(player, recipeInput, components) instanceof Object[]) {
                final Object[] obj = (Object[])recipe.getRecipeOutput(player, recipeInput, components);
                recipeOutputLabel = (String)obj[0];
                recipeOutput = obj[1];
            }
            else {
                recipeOutput = recipe.getRecipeOutput(player, recipeInput, components);
            }
            recipeInstability = recipe.getInstability(player, recipeInput, components);
            final AspectList al = recipe.getAspects(player, recipeInput, components);
            final AspectList al2 = new AspectList();
            for (final Aspect as : al.getAspects()) {
                if ((int)(al.getAmount(as) * costMult) > 0) {
                    al2.add(as, (int)(al.getAmount(as) * costMult));
                }
            }
            recipeEssentia = al2;
            recipePlayer = player.getName();
            crafting = true;
            world.playSound(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5f, 1.0f);
            syncTile(false);
            markDirty();
        }
    }
    
    private float getLossPerCycle() {
        return recipeInstability / getModFromCurrentStability();
    }
    
    public void craftCycle() {
        boolean valid = false;
        final float ff = world.rand.nextFloat() * getLossPerCycle();
        stability -= ff;
        stability += stabilityReplenish;
        if (stability < -100.0f) {
            stability = -100.0f;
        }
        if (stability > stabilityCap) {
            stability = (float) stabilityCap;
        }
        TileEntity te = world.getTileEntity(pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            final TilePedestal ped = (TilePedestal)te;
            if (!ped.getStackInSlot(0).isEmpty()) {
                final ItemStack i2 = ped.getStackInSlot(0).copy();
                if (recipeInput.getItemDamage() == 32767) {
                    i2.setItemDamage(32767);
                }
                if (ThaumcraftInvHelper.areItemStacksEqualForCrafting(i2, recipeInput)) {
                    valid = true;
                }
            }
        }
        if (!valid || (stability < 0.0f && world.rand.nextInt(1500) <= Math.abs(stability))) {
            switch (world.rand.nextInt(24)) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    inEvEjectItem(0);
                    break;
                }
                case 4:
                case 5:
                case 6: {
                    inEvWarp();
                    break;
                }
                case 7:
                case 8:
                case 9: {
                    inEvZap(false);
                    break;
                }
                case 10:
                case 11: {
                    inEvZap(true);
                    break;
                }
                case 12:
                case 13: {
                    inEvEjectItem(1);
                    break;
                }
                case 14:
                case 15: {
                    inEvEjectItem(2);
                    break;
                }
                case 16: {
                    inEvEjectItem(3);
                    break;
                }
                case 17: {
                    inEvEjectItem(4);
                    break;
                }
                case 18:
                case 19: {
                    inEvHarm(false);
                    break;
                }
                case 20:
                case 21: {
                    inEvEjectItem(5);
                    break;
                }
                case 22: {
                    inEvHarm(true);
                    break;
                }
                case 23: {
                    world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5f + world.rand.nextFloat(), false);
                    break;
                }
            }
            stability += 5.0f + world.rand.nextFloat() * 5.0f;
            inResAdd();
            if (valid) {
                return;
            }
        }
        if (!valid) {
            crafting = false;
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            syncTile(false);
            world.playSound(null, pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 1.0f, 0.6f);
            markDirty();
            return;
        }
        if (recipeType == 1 && recipeXP > 0) {
            final List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
            if (targets != null && targets.size() > 0) {
                for (final EntityPlayer target : targets) {
                    if (target.capabilities.isCreativeMode || target.experienceLevel > 0) {
                        if (!target.capabilities.isCreativeMode) {
                            target.addExperienceLevel(-1);
                        }
                        --recipeXP;
                        target.attackEntityFrom(DamageSource.MAGIC, (float) world.rand.nextInt(2));
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(pos, pos, target.getEntityId()), new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                        target.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 1.0f, 2.0f + world.rand.nextFloat() * 0.4f);
                        countDelay = cycleTime;
                        return;
                    }
                }
                final Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && world.rand.nextInt(3) == 0) {
                    final Aspect as = ingEss[world.rand.nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            return;
        }
        if (recipeType == 1 && recipeXP == 0) {
            countDelay = cycleTime / 2;
        }
        if (countDelay < 1) {
            countDelay = 1;
        }
        if (recipeEssentia.visSize() > 0) {
            for (final Aspect aspect : recipeEssentia.getAspects()) {
                final int na = recipeEssentia.getAmount(aspect);
                if (na > 0) {
                    if (EssentiaHandler.drainEssentia(this, aspect, null, 12, (na > 1) ? countDelay : 0)) {
                        recipeEssentia.reduce(aspect, 1);
                        syncTile(false);
                        markDirty();
                        return;
                    }
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            checkSurroundings = true;
            return;
        }
        if (recipeIngredients.size() > 0) {
            for (int a = 0; a < recipeIngredients.size(); ++a) {
                for (final BlockPos cc : pedestals) {
                    te = world.getTileEntity(cc);
                    if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getStackInSlot(0) != null && !((TilePedestal)te).getStackInSlot(0).isEmpty() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(((TilePedestal)te).getStackInSlot(0), recipeIngredients.get(a))) {
                        if (itemCount == 0) {
                            itemCount = 5;
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(pos, cc, 0), new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                        }
                        else if (itemCount-- <= 1) {
                            final ItemStack is = ((TilePedestal)te).getStackInSlot(0).getItem().getContainerItem(((TilePedestal)te).getStackInSlot(0));
                            ((TilePedestal)te).setInventorySlotContents(0, (is == null || is.isEmpty()) ? ItemStack.EMPTY : is.copy());
                            te.markDirty();
                            ((TilePedestal)te).syncTile(false);
                            recipeIngredients.remove(a);
                            markDirty();
                        }
                        return;
                    }
                }
                final Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && world.rand.nextInt(1 + a) == 0) {
                    final Aspect as = ingEss[world.rand.nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            return;
        }
        crafting = false;
        craftingFinish(recipeOutput, recipeOutputLabel);
        recipeOutput = null;
        syncTile(false);
        markDirty();
    }
    
    private void inEvZap(final boolean all) {
        final List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (final EntityLivingBase target : targets) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, target, 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                target.attackEntityFrom(DamageSource.MAGIC, (float)(4 + world.rand.nextInt(4)));
                if (!all) {
                    break;
                }
            }
        }
    }
    
    private void inEvHarm(final boolean all) {
        final List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (final EntityLivingBase target : targets) {
                if (world.rand.nextBoolean()) {
                    target.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 120, 0, false, true));
                }
                else {
                    final PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 2400, 0, true, true);
                    pe.getCurativeItems().clear();
                    target.addPotionEffect(pe);
                }
                if (!all) {
                    break;
                }
            }
        }
    }
    
    private void inResAdd() {
        final List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            for (final EntityPlayer player : targets) {
                final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                if (!knowledge.isResearchKnown("!INSTABILITY")) {
                    knowledge.addResearch("!INSTABILITY");
                    knowledge.sync((EntityPlayerMP)player);
                    player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.instability")), true);
                }
            }
        }
    }
    
    private void inEvWarp() {
        final List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            final EntityPlayer target = targets.get(world.rand.nextInt(targets.size()));
            if (world.rand.nextFloat() < 0.25f) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 1, IPlayerWarp.EnumWarpType.NORMAL);
            }
            else {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 2 + world.rand.nextInt(4), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
        }
    }
    
    private void inEvEjectItem(final int type) {
        for (int retries = 0; retries < 25 && pedestals.size() > 0; ++retries) {
            final BlockPos cc = pedestals.get(world.rand.nextInt(pedestals.size()));
            final TileEntity te = world.getTileEntity(cc);
            if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getStackInSlot(0) != null && !((TilePedestal)te).getStackInSlot(0).isEmpty()) {
                final BlockPos stabPos = ((TilePedestal)te).findInstabilityMitigator();
                if (stabPos != null) {
                    final TileEntity ste = world.getTileEntity(stabPos);
                    if (ste != null && ste instanceof TileStabilizer) {
                        final TileStabilizer tste = (TileStabilizer)ste;
                        if (tste.mitigate(MathHelper.getInt(world.rand, 5, 10))) {
                            world.addBlockEvent(cc, world.getBlockState(cc).getBlock(), 5, 0);
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, cc.up(), 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), cc.getX(), cc.getY(), cc.getZ(), 32.0));
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(cc.up(), stabPos, 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), stabPos.getX(), stabPos.getY(), stabPos.getZ(), 32.0));
                            return;
                        }
                    }
                }
                if (type <= 3 || type == 5) {
                    InventoryUtils.dropItems(world, cc);
                }
                else {
                    ((TilePedestal)te).setInventorySlotContents(0, ItemStack.EMPTY);
                }
                te.markDirty();
                ((TilePedestal)te).syncTile(false);
                if (type == 1 || type == 3) {
                    world.setBlockState(cc.up(), BlocksTC.fluxGoo.getDefaultState());
                    world.playSound(null, cc, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.3f, 1.0f);
                }
                else if (type == 2 || type == 4) {
                    final int a = 5 + world.rand.nextInt(5);
                    AuraHelper.polluteAura(world, cc, (float)a, true);
                }
                else if (type == 5) {
                    world.createExplosion(null, cc.getX() + 0.5f, cc.getY() + 0.5f, cc.getZ() + 0.5f, 1.0f, false);
                }
                world.addBlockEvent(cc, world.getBlockState(cc).getBlock(), 11, 0);
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, cc.up(), 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), cc.getX(), cc.getY(), cc.getZ(), 32.0));
                return;
            }
        }
    }
    
    public void craftingFinish(final Object out, final String label) {
        final TileEntity te = world.getTileEntity(pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            float dmg = 1.0f;
            if (out instanceof ItemStack) {
                final ItemStack qs = ((ItemStack)out).copy();
                if (((TilePedestal)te).getStackInSlot(0).isItemStackDamageable() && ((TilePedestal)te).getStackInSlot(0).isItemDamaged()) {
                    dmg = ((TilePedestal)te).getStackInSlot(0).getItemDamage() / (float)((TilePedestal)te).getStackInSlot(0).getMaxDamage();
                    if (qs.isItemStackDamageable() && !qs.isItemDamaged()) {
                        qs.setItemDamage((int)(qs.getMaxDamage() * dmg));
                    }
                }
                ((TilePedestal)te).setInventorySlotContentsFromInfusion(0, qs);
            }
            else if (out instanceof NBTBase) {
                final ItemStack temp = ((TilePedestal)te).getStackInSlot(0);
                final NBTBase tag = (NBTBase)out;
                temp.setTagInfo(label, tag);
                syncTile(false);
                te.markDirty();
            }
            else if (out instanceof Enchantment) {
                final ItemStack temp = ((TilePedestal)te).getStackInSlot(0);
                final Map enchantments = EnchantmentHelper.getEnchantments(temp);
                enchantments.put(out, EnchantmentHelper.getEnchantmentLevel((Enchantment)out, temp) + 1);
                EnchantmentHelper.setEnchantments(enchantments, temp);
                syncTile(false);
                te.markDirty();
            }
            if (recipePlayer != null) {
                final EntityPlayer p = world.getPlayerEntityByName(recipePlayer);
                if (p != null) {
                    FMLCommonHandler.instance().firePlayerCraftingEvent(p, ((TilePedestal)te).getStackInSlot(0), new InventoryFake(recipeIngredients));
                }
            }
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            syncTile(false);
            markDirty();
            world.addBlockEvent(pos.down(2), world.getBlockState(pos.down(2)).getBlock(), 12, 0);
            world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
    }
    
    private void getSurroundings() {
        final Set<Long> stuff = new HashSet<Long>();
        pedestals.clear();
        tempBlockCount.clear();
        problemBlocks.clear();
        cycleTime = 10;
        stabilityReplenish = 0.0f;
        costMult = 1.0f;
        try {
            for (int xx = -8; xx <= 8; ++xx) {
                for (int zz = -8; zz <= 8; ++zz) {
                    final boolean skip = false;
                    for (int yy = -3; yy <= 7; ++yy) {
                        if (xx != 0 || zz != 0) {
                            final int x = pos.getX() + xx;
                            final int y = pos.getY() - yy;
                            final int z = pos.getZ() + zz;
                            final BlockPos bp = new BlockPos(x, y, z);
                            final Block bi = world.getBlockState(bp).getBlock();
                            if (bi instanceof BlockPedestal) {
                                pedestals.add(bp);
                            }
                            try {
                                if (bi == Blocks.SKULL || (bi instanceof IInfusionStabiliser && ((IInfusionStabiliser)bi).canStabaliseInfusion(getWorld(), bp))) {
                                    stuff.add(bp.toLong());
                                }
                            }
                            catch (final Exception ex) {}
                        }
                    }
                }
            }
            while (!stuff.isEmpty()) {
                final Long[] posArray = stuff.toArray(new Long[stuff.size()]);
                if (posArray == null) {
                    break;
                }
                if (posArray[0] == null) {
                    break;
                }
                final long lp = posArray[0];
                try {
                    final BlockPos c1 = BlockPos.fromLong(lp);
                    final int x2 = pos.getX() - c1.getX();
                    final int z2 = pos.getZ() - c1.getZ();
                    final int x3 = pos.getX() + x2;
                    final int z3 = pos.getZ() + z2;
                    final BlockPos c2 = new BlockPos(x3, c1.getY(), z3);
                    final Block sb1 = world.getBlockState(c1).getBlock();
                    final Block sb2 = world.getBlockState(c2).getBlock();
                    float amt1 = 0.1f;
                    float amt2 = 0.1f;
                    if (sb1 instanceof IInfusionStabiliserExt) {
                        amt1 = ((IInfusionStabiliserExt)sb1).getStabilizationAmount(getWorld(), c1);
                    }
                    if (sb2 instanceof IInfusionStabiliserExt) {
                        amt2 = ((IInfusionStabiliserExt)sb2).getStabilizationAmount(getWorld(), c2);
                    }
                    if (sb1 == sb2 && amt1 == amt2) {
                        if (sb1 instanceof IInfusionStabiliserExt && ((IInfusionStabiliserExt)sb1).hasSymmetryPenalty(getWorld(), c1, c2)) {
                            stabilityReplenish -= ((IInfusionStabiliserExt)sb1).getSymmetryPenalty(getWorld(), c1);
                            problemBlocks.add(c1);
                        }
                        else {
                            stabilityReplenish += calcDeminishingReturns(sb1, amt1);
                        }
                    }
                    else {
                        stabilityReplenish -= Math.max(amt1, amt2);
                        problemBlocks.add(c1);
                    }
                    stuff.remove(c2.toLong());
                }
                catch (final Exception ex2) {}
                stuff.remove(lp);
            }
            if (world.getBlockState(pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(1, -2, -1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(1, -2, 1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar) {
                if (world.getBlockState(pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarAncient && world.getBlockState(pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarAncient && world.getBlockState(pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarAncient && world.getBlockState(pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarAncient) {
                    --cycleTime;
                    costMult -= 0.1f;
                    stabilityReplenish -= 0.1f;
                }
                if (world.getBlockState(pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && world.getBlockState(pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && world.getBlockState(pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarEldritch && world.getBlockState(pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarEldritch) {
                    cycleTime -= 3;
                    costMult += 0.05f;
                    stabilityReplenish += 0.2f;
                }
            }
            final int[] xm = { -1, 1, 1, -1 };
            final int[] zm = { -1, -1, 1, 1 };
            for (int a = 0; a < 4; ++a) {
                final Block b = world.getBlockState(pos.add(xm[a], -3, zm[a])).getBlock();
                if (b == BlocksTC.matrixSpeed) {
                    --cycleTime;
                    costMult += 0.01f;
                }
                if (b == BlocksTC.matrixCost) {
                    ++cycleTime;
                    costMult -= 0.02f;
                }
            }
            countDelay = cycleTime / 2;
            final int apc = 0;
            for (final BlockPos cc : pedestals) {
                final boolean items = false;
                final int x4 = pos.getX() - cc.getX();
                final int z4 = pos.getZ() - cc.getZ();
                final Block bb = world.getBlockState(cc).getBlock();
                if (bb == BlocksTC.pedestalEldritch) {
                    costMult += 0.0025f;
                }
                if (bb == BlocksTC.pedestalAncient) {
                    costMult -= 0.01f;
                }
            }
        }
        catch (final Exception ex3) {}
    }
    
    private float calcDeminishingReturns(final Block b, final float base) {
        float bb = base;
        final int c = tempBlockCount.containsKey(b) ? tempBlockCount.get(b) : 0;
        if (c > 0) {
            bb *= (float)Math.pow(0.75, c);
        }
        tempBlockCount.put(b, c + 1);
        return bb;
    }
    
    @Override
    public boolean onCasterRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final EnumHand hand) {
        if (world.isRemote && active && !crafting) {
            checkSurroundings = true;
        }
        if (!world.isRemote && active && !crafting) {
            craftingStart(player);
            return false;
        }
        if (!world.isRemote && !active && validLocation()) {
            world.playSound(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5f, 1.0f);
            active = true;
            syncTile(false);
            markDirty();
            return false;
        }
        return false;
    }
    
    private void doEffects() {
        if (crafting) {
            if (craftCount == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.infuserstart, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
            else if (craftCount == 0 || craftCount % 65 == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.infuser, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
            ++craftCount;
            FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() - 2, pos.getZ(), 0.5f + world.rand.nextFloat() * 0.2f, 0.1f, 0.7f + world.rand.nextFloat() * 0.3f, 25, -0.03f);
        }
        else if (craftCount > 0) {
            craftCount -= 2;
            if (craftCount < 0) {
                craftCount = 0;
            }
            if (craftCount > 50) {
                craftCount = 50;
            }
        }
        if (active && startUp != 1.0f) {
            if (startUp < 1.0f) {
                startUp += Math.max(startUp / 10.0f, 0.001f);
            }
            if (startUp > 0.999) {
                startUp = 1.0f;
            }
        }
        if (!active && startUp > 0.0f) {
            if (startUp > 0.0f) {
                startUp -= startUp / 10.0f;
            }
            if (startUp < 0.001) {
                startUp = 0.0f;
            }
        }
        for (final String fxk : sourceFX.keySet().toArray(new String[0])) {
            final SourceFX fx = sourceFX.get(fxk);
            if (fx.ticks <= 0) {
                sourceFX.remove(fxk);
            }
            else {
                if (fx.loc.equals(pos)) {
                    final Entity player = world.getEntityByID(fx.color);
                    if (player != null) {
                        for (int a = 0; a < 4; ++a) {
                            FXDispatcher.INSTANCE.drawInfusionParticles4(player.posX + (world.rand.nextFloat() - world.rand.nextFloat()) * player.width, player.getEntityBoundingBox().minY + world.rand.nextFloat() * player.height, player.posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * player.width, pos.getX(), pos.getY(), pos.getZ());
                        }
                    }
                }
                else {
                    final TileEntity tile = world.getTileEntity(fx.loc);
                    if (tile instanceof TilePedestal) {
                        final ItemStack is = ((TilePedestal)tile).getSyncedStackInSlot(0);
                        if (is != null && !is.isEmpty()) {
                            if (world.rand.nextInt(3) == 0) {
                                FXDispatcher.INSTANCE.drawInfusionParticles3(fx.loc.getX() + world.rand.nextFloat(), fx.loc.getY() + world.rand.nextFloat() + 1.0f, fx.loc.getZ() + world.rand.nextFloat(), pos.getX(), pos.getY(), pos.getZ());
                            }
                            else {
                                final Item bi = is.getItem();
                                if (bi instanceof ItemBlock) {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles2(fx.loc.getX() + world.rand.nextFloat(), fx.loc.getY() + world.rand.nextFloat() + 1.0f, fx.loc.getZ() + world.rand.nextFloat(), pos, Block.getBlockFromItem(bi).getDefaultState(), is.getItemDamage());
                                    }
                                }
                                else {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles1(fx.loc.getX() + 0.4f + world.rand.nextFloat() * 0.2f, fx.loc.getY() + 1.23f + world.rand.nextFloat() * 0.2f, fx.loc.getZ() + 0.4f + world.rand.nextFloat() * 0.2f, pos, is);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        fx.ticks = 0;
                    }
                }
                final SourceFX sourceFX = fx;
                --sourceFX.ticks;
                this.sourceFX.put(fxk, fx);
            }
        }
        if (crafting && stability < 0.0f && world.rand.nextInt(250) <= Math.abs(stability)) {
            FXDispatcher.INSTANCE.spark(getPos().getX() + world.rand.nextFloat(), getPos().getY() + world.rand.nextFloat(), getPos().getZ() + world.rand.nextFloat(), 3.0f + world.rand.nextFloat() * 2.0f, 0.7f + world.rand.nextFloat() * 0.1f, 0.1f, 0.65f + world.rand.nextFloat() * 0.1f, 0.8f);
        }
        if (active && !problemBlocks.isEmpty() && world.rand.nextInt(25) == 0) {
            final BlockPos p = problemBlocks.get(world.rand.nextInt(problemBlocks.size()));
            FXDispatcher.INSTANCE.spark(p.getX() + world.rand.nextFloat(), p.getY() + world.rand.nextFloat(), p.getZ() + world.rand.nextFloat(), 2.0f + world.rand.nextFloat(), 0.7f + world.rand.nextFloat() * 0.1f, 0.1f, 0.65f + world.rand.nextFloat() * 0.1f, 0.8f);
        }
    }
    
    @Override
    public AspectList getAspects() {
        return recipeEssentia;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
    }
    
    @Override
    public int addToContainer(final Aspect tag, final int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(final Aspect tag) {
        return 0;
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
    
    public String[] getIGogglesText() {
        final float lpc = getLossPerCycle();
        if (lpc != 0.0f) {
            return new String[] { TextFormatting.BOLD + I18n.translateToLocal("stability." + getStability().name()), TextFormatting.GOLD + "" + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(stabilityReplenish) + " " + I18n.translateToLocal("stability.gain"), TextFormatting.RED + "" + I18n.translateToLocal("stability.range") + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(lpc) + " " + I18n.translateToLocal("stability.loss") };
        }
        return new String[] { TextFormatting.BOLD + I18n.translateToLocal("stability." + getStability().name()), TextFormatting.GOLD + "" + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(stabilityReplenish) + " " + I18n.translateToLocal("stability.gain") };
    }
    
    static {
        TileInfusionMatrix.myFormatter = new DecimalFormat("#######.##");
    }
    
    public class SourceFX
    {
        public BlockPos loc;
        public int ticks;
        public int color;
        public int entity;
        
        public SourceFX(final BlockPos loc, final int ticks, final int color) {
            this.loc = loc;
            this.ticks = ticks;
            this.color = color;
        }
    }
    
    private enum EnumStability
    {
        VERY_STABLE, 
        STABLE, 
        UNSTABLE, 
        VERY_UNSTABLE;
    }
}
