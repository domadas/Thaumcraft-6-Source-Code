// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.events;

import net.minecraft.world.World;
import java.util.Iterator;
import net.minecraft.world.biome.Biome;
import net.minecraft.entity.SharedMonsterAttributes;
import thaumcraft.common.config.ConfigEntities;
import net.minecraftforge.common.BiomeDictionary;
import thaumcraft.common.config.ModConfig;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import java.util.UUID;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.item.EntityXPOrb;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import thaumcraft.common.entities.monster.mods.ChampionModTainted;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.api.entities.IEldritchMob;
import net.minecraft.entity.monster.EntityMob;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import thaumcraft.common.items.armor.ItemFortressArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.state.IBlockState;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.init.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.items.consumables.ItemBathSalts;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityEvents
{
    @SubscribeEvent
    public static void itemExpire(final ItemExpireEvent event) {
        if (event.getEntityItem().getItem() != null && !event.getEntityItem().getItem().isEmpty() && event.getEntityItem().getItem().getItem() != null && event.getEntityItem().getItem().getItem() instanceof ItemBathSalts) {
            final BlockPos bp = new BlockPos(event.getEntityItem());
            final IBlockState bs = event.getEntityItem().world.getBlockState(bp);
            if (bs.getBlock() == Blocks.WATER && bs.getBlock().getMetaFromState(bs) == 0) {
                event.getEntityItem().world.setBlockState(bp, BlocksTC.purifyingFluid.getDefaultState());
            }
        }
    }
    
    @SubscribeEvent
    public static void livingTick(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityCreature && !event.getEntity().isDead) {
            final EntityCreature mob = (EntityCreature)event.getEntity();
            if (mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
                final int t = (int)mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
                try {
                    if (t >= 0 && ChampionModifier.mods[t].type == 0) {
                        ChampionModifier.mods[t].effect.performEffect(mob, null, null, 0.0f);
                    }
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    if (t >= ChampionModifier.mods.length) {
                        mob.setDead();
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void entityHurt(final LivingHurtEvent event) {
        if (event.getSource().isFireDamage() && event.getEntity() instanceof EntityPlayer && ThaumcraftCapabilities.knowsResearchStrict((EntityPlayer)event.getEntity(), "BASEAUROMANCY@2") && !ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_onfire")) {
            final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge((EntityPlayer)event.getEntity());
            knowledge.addResearch("f_onfire");
            knowledge.sync((EntityPlayerMP)event.getEntity());
            ((EntityPlayer)event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.onfire")), true);
        }
        if (event.getSource().getImmediateSource() != null && event.getEntity() instanceof EntityPlayer && ThaumcraftCapabilities.knowsResearchStrict((EntityPlayer)event.getEntity(), "FOCUSPROJECTILE@2")) {
            final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge((EntityPlayer)event.getEntity());
            if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_arrow") && event.getSource().getImmediateSource() instanceof EntityArrow) {
                knowledge.addResearch("f_arrow");
                knowledge.sync((EntityPlayerMP)event.getEntity());
                ((EntityPlayer)event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.projectile")), true);
            }
            if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_fireball") && event.getSource().getImmediateSource() instanceof EntityFireball) {
                knowledge.addResearch("f_fireball");
                knowledge.sync((EntityPlayerMP)event.getEntity());
                ((EntityPlayer)event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.projectile")), true);
            }
            if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_spit") && event.getSource().getImmediateSource() instanceof EntityLlamaSpit) {
                knowledge.addResearch("f_spit");
                knowledge.sync((EntityPlayerMP)event.getEntity());
                ((EntityPlayer)event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.projectile")), true);
            }
        }
        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            final EntityPlayer leecher = (EntityPlayer)event.getSource().getTrueSource();
            final ItemStack helm = leecher.inventory.armorInventory.get(3);
            if (helm != null && !helm.isEmpty() && helm.getItem() instanceof ItemFortressArmor && helm.hasTagCompound() && helm.getTagCompound().hasKey("mask") && helm.getTagCompound().getInteger("mask") == 2 && leecher.world.rand.nextFloat() < event.getAmount() / 12.0f) {
                leecher.heal(1.0f);
            }
        }
        if (event.getEntity() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)event.getEntity();
            if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
                final EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
                final ItemStack helm2 = player.inventory.armorInventory.get(3);
                if (helm2 != null && !helm2.isEmpty() && helm2.getItem() instanceof ItemFortressArmor && helm2.hasTagCompound() && helm2.getTagCompound().hasKey("mask") && helm2.getTagCompound().getInteger("mask") == 1 && player.world.rand.nextFloat() < event.getAmount() / 10.0f) {
                    try {
                        attacker.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80));
                    }
                    catch (final Exception ex) {}
                }
            }
            final int charge = (int)player.getAbsorptionAmount();
            if (charge > 0 && PlayerEvents.runicInfo.containsKey(player.getEntityId()) && PlayerEvents.lastMaxCharge.containsKey(player.getEntityId())) {
                final long time = System.currentTimeMillis();
                int target = -1;
                if (event.getSource().getTrueSource() != null) {
                    target = event.getSource().getTrueSource().getEntityId();
                }
                if (event.getSource() == DamageSource.FALL) {
                    target = -2;
                }
                if (event.getSource() == DamageSource.FALLING_BLOCK) {
                    target = -3;
                }
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXShield(event.getEntity().getEntityId(), target), new NetworkRegistry.TargetPoint(event.getEntity().world.provider.getDimension(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 32.0));
            }
        }
        else {
            if (!event.getEntityLiving().world.isRemote && event.getEntityLiving().getHealth() < 2.0f && !event.getEntityLiving().isEntityUndead() && !event.getEntityLiving().isDead && !(event.getEntityLiving() instanceof EntityOwnedConstruct) && !(event.getEntityLiving() instanceof ITaintedMob) && event.getEntityLiving().isPotionActive(PotionFluxTaint.instance) && event.getEntityLiving().getRNG().nextBoolean()) {
                EntityUtils.makeTainted(event.getEntityLiving());
                return;
            }
            if (event.getEntity() instanceof EntityMob) {
                final IAttributeInstance cai = ((EntityMob)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
                if ((cai != null && cai.getAttributeValue() >= 0.0) || event.getEntity() instanceof IEldritchMob) {
                    final EntityMob mob = (EntityMob)event.getEntity();
                    final int t = (int)cai.getAttributeValue();
                    if ((t == 5 || event.getEntity() instanceof IEldritchMob) && mob.getAbsorptionAmount() > 0.0f) {
                        int target2 = -1;
                        if (event.getSource().getTrueSource() != null) {
                            target2 = event.getSource().getTrueSource().getEntityId();
                        }
                        if (event.getSource() == DamageSource.FALL) {
                            target2 = -2;
                        }
                        if (event.getSource() == DamageSource.FALLING_BLOCK) {
                            target2 = -3;
                        }
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXShield(mob.getEntityId(), target2), new NetworkRegistry.TargetPoint(event.getEntity().world.provider.getDimension(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 32.0));
                        event.getEntity().playSound(SoundsTC.runicShieldCharge, 0.66f, 1.1f + event.getEntity().world.rand.nextFloat() * 0.1f);
                    }
                    else if (t >= 0 && ChampionModifier.mods[t].type == 2 && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
                        final EntityLivingBase attacker2 = (EntityLivingBase)event.getSource().getTrueSource();
                        event.setAmount(ChampionModifier.mods[t].effect.performEffect(mob, attacker2, event.getSource(), event.getAmount()));
                    }
                }
                if (event.getAmount() > 0.0f && event.getSource().getTrueSource() != null && event.getEntity() instanceof EntityLivingBase && event.getSource().getTrueSource() instanceof EntityMob && ((EntityMob)event.getSource().getTrueSource()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() >= 0.0) {
                    final EntityMob mob = (EntityMob)event.getSource().getTrueSource();
                    final int t = (int)mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
                    if (ChampionModifier.mods[t].type == 1) {
                        event.setAmount(ChampionModifier.mods[t].effect.performEffect(mob, (EntityLivingBase)event.getEntity(), event.getSource(), event.getAmount()));
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void itemPickup(final EntityItemPickupEvent event) {
        if (event.getEntityPlayer().getName().startsWith("FakeThaumcraft")) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void entityConstuct(final EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof EntityCreature && !(event.getEntity() instanceof EntityOwnedConstruct)) {
            final EntityCreature mob = (EntityCreature)event.getEntity();
            mob.getAttributeMap().registerAttribute(ThaumcraftApiHelper.CHAMPION_MOD).setBaseValue(-2.0);
            mob.getAttributeMap().registerAttribute(ChampionModTainted.TAINTED_MOD).setBaseValue(0.0);
        }
    }
    
    @SubscribeEvent
    public static void livingDrops(final LivingDropsEvent event) {
        final boolean fakeplayer = event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof FakePlayer;
        if (!event.getEntity().world.isRemote && event.isRecentlyHit() && !fakeplayer && event.getEntity() instanceof EntityMob && !(event.getEntity() instanceof EntityThaumcraftBoss) && ((EntityMob)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() >= 0.0 && ((EntityMob)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() != 13.0) {
            int i = 5 + event.getEntity().world.rand.nextInt(3);
            while (i > 0) {
                final int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                event.getEntity().world.spawnEntity(new EntityXPOrb(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, j));
            }
            final int lb = Math.min(2, MathHelper.floor((event.getEntity().world.rand.nextInt(9) + event.getLootingLevel()) / 5.0f));
            event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntityLiving().posX, event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(), event.getEntityLiving().posZ, new ItemStack(ItemsTC.lootBag, 1, lb)));
        }
        if (event.getEntityLiving() instanceof EntityZombie && !(event.getEntityLiving() instanceof EntityBrainyZombie) && event.isRecentlyHit() && event.getEntity().world.rand.nextInt(10) - event.getLootingLevel() < 1) {
            event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntityLiving().posX, event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(), event.getEntityLiving().posZ, new ItemStack(ItemsTC.brain)));
        }
        if (event.getEntityLiving() instanceof EntityCultist && !fakeplayer && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            final EntityPlayer p = (EntityPlayer)event.getSource().getTrueSource();
            int c = ThaumcraftCapabilities.getKnowledge(p).isResearchKnown("!CrimsonCultist@2") ? 20 : 4;
            if (InventoryUtils.getPlayerSlotFor(p, new ItemStack(ItemsTC.curio, 1, 6)) >= 0) {
                c = 50;
            }
            if (event.getEntity().world.rand.nextInt(c) == 0) {
                event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntityLiving().posX, event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(), event.getEntityLiving().posZ, new ItemStack(ItemsTC.curio, 1, 6)));
            }
        }
        if (event.getSource() == DamageSourceThaumcraft.dissolve) {
            final AspectList aspects = AspectHelper.getEntityAspects(event.getEntityLiving());
            if (aspects != null && aspects.size() > 0) {
                final Aspect[] al = aspects.getAspects();
                for (int q = MathHelper.getInt(event.getEntity().getEntityWorld().rand, 1, 1 + aspects.visSize() / 10), a = 0; a < q; ++a) {
                    final Aspect aspect = al[event.getEntity().getEntityWorld().rand.nextInt(al.length)];
                    final ItemStack stack = ThaumcraftApiHelper.makeCrystal(aspect);
                    event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntityLiving().posX, event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(), event.getEntityLiving().posZ, stack));
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void entitySpawns(final EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote) {
            if (event.getEntity() instanceof EntityCreature && ((EntityCreature)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null && ((EntityCreature)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() == 13.0) {
                final IAttributeInstance modai = ((EntityCreature)event.getEntity()).getEntityAttribute(ChampionModTainted.TAINTED_MOD);
                modai.removeModifier(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 1.0, 0));
                modai.applyModifier(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 0.0, 0));
            }
            if (event.getEntity() instanceof EntityMob) {
                final EntityMob mob = (EntityMob)event.getEntity();
                if (mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() < -1.0) {
                    int c = event.getWorld().rand.nextInt(100);
                    if (event.getWorld().getDifficulty() == EnumDifficulty.EASY || !ModConfig.CONFIG_WORLD.allowChampionMobs) {
                        c += 2;
                    }
                    if (event.getWorld().getDifficulty() == EnumDifficulty.HARD) {
                        c -= (ModConfig.CONFIG_WORLD.allowChampionMobs ? 2 : 0);
                    }
                    if (event.getWorld().provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
                        c -= 3;
                    }
                    final Biome bg = mob.world.getBiome(new BlockPos(mob));
                    if (BiomeDictionary.hasType(bg, BiomeDictionary.Type.SPOOKY) || BiomeDictionary.hasType(bg, BiomeDictionary.Type.NETHER) || BiomeDictionary.hasType(bg, BiomeDictionary.Type.END)) {
                        c -= (ModConfig.CONFIG_WORLD.allowChampionMobs ? 2 : 1);
                    }
                    if (isDangerousLocation(mob.world, MathHelper.ceil(mob.posX), MathHelper.ceil(mob.posY), MathHelper.ceil(mob.posZ))) {
                        c -= (ModConfig.CONFIG_WORLD.allowChampionMobs ? 10 : 3);
                    }
                    int cc = 0;
                    boolean whitelisted = false;
                    for (final Class clazz : ConfigEntities.championModWhitelist.keySet()) {
                        if (clazz.isAssignableFrom(event.getEntity().getClass())) {
                            whitelisted = true;
                            if (!ModConfig.CONFIG_WORLD.allowChampionMobs && !(event.getEntity() instanceof EntityThaumcraftBoss)) {
                                continue;
                            }
                            cc = Math.max(cc, ConfigEntities.championModWhitelist.get(clazz) - 1);
                        }
                    }
                    c -= cc;
                    if (whitelisted && c <= 0 && mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() >= 10.0) {
                        EntityUtils.makeChampion(mob, false);
                    }
                    else {
                        final IAttributeInstance modai2 = mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
                        modai2.removeModifier(ChampionModifier.ATTRIBUTE_MOD_NONE);
                        modai2.applyModifier(ChampionModifier.ATTRIBUTE_MOD_NONE);
                    }
                }
            }
        }
    }
    
    private static boolean isDangerousLocation(final World world, final int x, final int y, final int z) {
        return false;
    }
}
