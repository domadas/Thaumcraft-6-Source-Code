// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.world.biomes;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import net.minecraft.world.biome.Biome;

public class BiomeGenEldritch extends Biome
{
    public BiomeGenEldritch(final Biome.BiomeProperties p_i1990_1_) {
        super(p_i1990_1_);
        this.setRegistryName("thaumcraft", "eldritch");
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityInhabitedZombie.class, 1, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEldritchGuardian.class, 1, 1, 1));
        this.topBlock = Blocks.DIRT.getDefaultState();
        this.fillerBlock = Blocks.DIRT.getDefaultState();
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(final float p_76731_1_) {
        return 0;
    }
    
    public void decorate(final World world, final Random random, final BlockPos pos) {
    }
}
