package com.untamedears.realisticbiomes.growth;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;

import com.untamedears.realisticbiomes.model.Plant;

import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

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
		
		
		CraftBlock cBlock = (CraftBlock) block;
		BlockPos pos = cBlock.getPosition();
		ServerLevel spWorld = cBlock.getCraftWorld().getHandle();
		BlockState spBlock = cBlock.getNMS();

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
		if(isMega) return false; // fail tree growth  here if it's a mega tree and it hasn't returned true above
		ConfiguredFeature<?, ?> feature;
		ConfiguredFeature<?, ?> featureSmall;
		//feature holds the default Minecraft tree and featureSmall holds a version with randomHeight set to 0 so that it will grow at minimum height
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
		case DARK_OAK_SAPLING: //completly unecessary only left this in for the sake of completion tbh
		default: 
			return false;
		}
		 world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
         if (feature.place(world, chunkGenerator, random, pos)) { //attempt to place the normal tree feature
             return true;
         } else if(featureSmall.place(world, chunkGenerator, random, pos)) { //attempt to place the small/short version if the normal fails
        	 return true;
         } else {
             world.setBlock(pos, state, 4); //replace the sappling if both fail
             return false;
         }
	}
	
	private boolean placeMegaTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
		ConfiguredFeature<?, ?> feature;
		ConfiguredFeature<?, ?> featureSmall;
		//feature holds the default minecraft tree and featuresmall holds a version with randomHeight set to 0 so that it will grow at minimum height
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
                	isMega = true; //set ismega so that function above can know when a tree is 2x2 and has failed growth (admitedly this is probs not the best solution)
                	BlockState air = Blocks.AIR.defaultBlockState();
                	 world.setBlock(pos.offset(i, 0, j), air, 4); //remove saplings
                     world.setBlock(pos.offset(i + 1, 0, j), air, 4);
                     world.setBlock(pos.offset(i, 0, j + 1), air, 4);
                     world.setBlock(pos.offset(i + 1, 0, j + 1), air, 4);
                     if (feature.place(world, chunkGenerator, random, pos.offset(i, 0, j))) {
                         return true;
                     } else if(featureSmall.place(world, chunkGenerator, random, pos.offset(i, 0, j))){ //try placing short tree if normal tree fails
                    	 return true;
                     } else {
	                     world.setBlock(pos.offset(i, 0, j), state, 4);  //replace saplings if both fail
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

	private boolean hasFlowers(LevelAccessor world, BlockPos pos) { //straight up pulled from nms code 
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
	

}
