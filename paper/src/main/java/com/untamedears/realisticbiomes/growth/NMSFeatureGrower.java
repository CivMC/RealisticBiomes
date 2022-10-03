package com.untamedears.realisticbiomes.growth;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R2.block.data.type.CraftSapling;

import com.google.common.collect.ImmutableList;
import com.untamedears.realisticbiomes.PlantManager;
import com.untamedears.realisticbiomes.RealisticBiomes;
import com.untamedears.realisticbiomes.model.Plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.MegaJungleTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.TreeFeatures;

public class NMSFeatureGrower extends AgeableGrower {

	public NMSFeatureGrower(Material material) {
		super(material, 1, 1);
	}

	private boolean isMega;




	@Override
	public int getStage(Plant plant) {
		Block block = plant.getLocation().getBlock();
		if (block.getType() != this.material) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean setStage(Plant plant, int stage) {
		if (stage < 1) {
			return true;
		}
		Block block = plant.getLocation().getBlock();
		// Re-Read the block data to make sure it is up to date
		if (!(block.getBlockData() instanceof Sapling)) {
			return true;
		} 
		
		
		//Sapling sapling =
		//(Sapling) block.getBlockData();
		//sapling.setStage(3);
		//plant.getLocation().getWorld().setBlockData(plant.getLocation(), sapling);
		CraftBlock cBlock = (CraftBlock) block;
		BlockPos pos = cBlock.getPosition();
		ServerLevel spWorld = cBlock.getCraftWorld().getHandle();
		BlockState spBlock = cBlock.getNMS();
		//TODO basically copy AbstractTressGrower.growTree
		this.growTreeFeature(spWorld, spWorld.getChunkSource().getGenerator(), pos, spBlock, new Random());
		return true;
	}
	
	public boolean growTreeFeature(
			ServerLevel world, 
			ChunkGenerator chunkGenerator, 
			BlockPos pos, 
			BlockState state, 
			Random random) {
		isMega = false;
		if(placeMegaTree(world, chunkGenerator, pos, state, random)) {return true;};
		if(isMega) return false;
		ConfiguredFeature<?, ?> feature;
		ConfiguredFeature<?, ?> featureSmall;
		switch(state.getBukkitMaterial()) {
		case JUNGLE_SAPLING:
			feature = TreeFeatures.JUNGLE_TREE_NO_VINE.value();
			featureSmall = RBFeatures.SHORT_JUNGLE.value();
			break;
		case SPRUCE_SAPLING:
			feature = TreeFeatures.SPRUCE.value();
			featureSmall = RBFeatures.SHORT_SPRUCE.value();
			break;
		case OAK_SAPLING:
			feature = this.hasFlowers(world, pos) ? TreeFeatures.OAK_BEES_005.value() : TreeFeatures.OAK.value();
			featureSmall = this.hasFlowers(world, pos) ? RBFeatures.SHORT_OAK_BEES.value() : RBFeatures.SHORT_OAK.value();
			break;
		case BIRCH_SAPLING:
			feature = this.hasFlowers(world, pos) ? TreeFeatures.BIRCH_BEES_005.value() : TreeFeatures.BIRCH.value();
			featureSmall = this.hasFlowers(world, pos) ? RBFeatures.SHORT_BIRCH_BEES.value() : RBFeatures.SHORT_BIRCH.value();
			break;
		case ACACIA_SAPLING:
			feature = TreeFeatures.ACACIA.value();
			featureSmall = RBFeatures.SHORT_ACACIA.value();
			break;
		case DARK_OAK_SAPLING:
		default:
			return false;
		}
		 world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
         if (feature.place(world, chunkGenerator, random, pos)) {
             return true;
         } else if(featureSmall.place(world, chunkGenerator, random, pos)) {
        	 return true;
         } else {
             world.setBlock(pos, state, 4);
             return false;
         }
	}
	
	private boolean placeMegaTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
		ConfiguredFeature<?, ?> feature;
		ConfiguredFeature<?, ?> featureSmall;
		switch(state.getBukkitMaterial()) {	
		case JUNGLE_SAPLING:
			feature = TreeFeatures.MEGA_JUNGLE_TREE.value();
			featureSmall = RBFeatures.SHORT_MEGA_JUNGLE.value();
			break;
		case SPRUCE_SAPLING:
			feature = TreeFeatures.MEGA_SPRUCE.value();
			featureSmall = RBFeatures.SHORT_MEGA_SPRUCE.value();
			break;
		case DARK_OAK_SAPLING:
			feature = TreeFeatures.DARK_OAK.value();
			featureSmall = RBFeatures.SHORT_DARK_OAK.value();
			break;
		default:
			return false;
		}
		for (int i = 0; i >= -1; --i) {
            for (int j = 0; j >= -1; --j) {
                if (AbstractMegaTreeGrower.isTwoByTwoSapling(state, world, pos, i, j)) {
                	isMega = true;
                	BlockState air = Blocks.AIR.defaultBlockState();
                	 world.setBlock(pos.offset(i, 0, j), air, 4);
                     world.setBlock(pos.offset(i + 1, 0, j), air, 4);
                     world.setBlock(pos.offset(i, 0, j + 1), air, 4);
                     world.setBlock(pos.offset(i + 1, 0, j + 1), air, 4);
                     if (feature.place(world, chunkGenerator, random, pos.offset(i, 0, j))) {
                         return true;
                     } else if(featureSmall.place(world, chunkGenerator, random, pos.offset(i, 0, j))){
                    	 return true;
                     } else {
	                     world.setBlock(pos.offset(i, 0, j), state, 4);
	                     world.setBlock(pos.offset(i + 1, 0, j), state, 4);
	                     world.setBlock(pos.offset(i, 0, j + 1), state, 4);
	                     world.setBlock(pos.offset(i + 1, 0, j + 1), state, 4);
	                     return false ;
                     }
                }
            }
        }
		return false;
	}

	private boolean hasFlowers(LevelAccessor world, BlockPos pos) {
        Iterator iterator = BlockPos.MutableBlockPos.betweenClosed(pos.below().north(2).west(2), pos.above().south(2).east(2)).iterator();

        BlockPos blockposition1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            blockposition1 = (BlockPos) iterator.next();
        } while (!world.getBlockState(blockposition1).is(BlockTags.FLOWERS));

        return true;
    }




	@Override
	public boolean deleteOnFullGrowth() {
		return true;
	}

	
	//this class will be the one growing the trees for us
	//private class InternalNMSTreeGrower extends AbstractTreeGrower {
	//turns out there's not rly any need for this class
	//most of what was here has been moved to RBFeatures

	

}
