// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.curios;

import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import thaumcraft.api.research.ResearchCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.capabilities.IPlayerWarp;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemCurio extends ItemTCBase
{
    public ItemCurio() {
        super("curio", "arcane", "preserved", "ancient", "eldritch", "knowledge", "twisted", "rites");
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        tooltip.add(I18n.translateToLocal("item.curio.text"));
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer player, final EnumHand hand) {
        worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.learn, SoundCategory.NEUTRAL, 0.5f, 0.4f / (ItemCurio.itemRand.nextFloat() * 0.4f + 0.8f));
        if (!worldIn.isRemote) {
            final int oProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
            final int tProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
            switch (player.getHeldItem(hand).getItemDamage()) {
                default: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("AUROMANCY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("AUROMANCY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 1: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ALCHEMY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ALCHEMY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 2: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("GOLEMANCY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("GOLEMANCY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 3: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
                    break;
                }
                case 4: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("INFUSION"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("INFUSION"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 5: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ARTIFICE"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ARTIFICE"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 6: {
                    final int aw = ThaumcraftApi.internalMethods.getActualWarp(player);
                    if (aw > 20) {
                        final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                        if (!knowledge.isResearchKnown("CrimsonRites")) {
                            ThaumcraftApi.internalMethods.completeResearch(player, "CrimsonRites");
                        }
                        ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                        ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
                        if (player.getRNG().nextBoolean()) {
                            ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.PERMANENT);
                        }
                        break;
                    }
                    player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("fail.crimsonrites")));
                    return super.onItemRightClick(worldIn, player, hand);
                }
            }
            final ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
            if (!player.capabilities.isCreativeMode) {
                player.getHeldItem(hand).shrink(1);
            }
            player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("tc.knowledge.gained")));
        }
        player.addStat(StatList.getObjectUseStats(this));
        return super.onItemRightClick(worldIn, player, hand);
    }
}
