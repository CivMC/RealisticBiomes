package com.untamedears.realisticbiomes.growth;

import java.util.List;
import java.util.OptionalInt;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.MegaJungleTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

//manage NMS Configured features for RB
public class RBFeatures {


	private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(
			net.minecraft.world.level.block.Block log, net.minecraft.world.level.block.Block leaves, 
			int baseHeight, int radius) {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(log), new StraightTrunkPlacer(baseHeight, 0, 0), BlockStateProvider.simple(leaves), new BlobFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
    }
	//tree features:
	
	//beehive with 5% chance of generating
	private static final BeehiveDecorator BEEHIVE_005 = new BeehiveDecorator(0.05F);
	//Short oak tree feature, short meaning the tree will grow at the smallest size vanilla does
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_OAK = 
			FeatureUtils.register("short_oak", Feature.TREE, createStraightBlobTree
					(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2).build());
	//Short birch tree feature
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_BIRCH = 
			FeatureUtils.register("short_birch", Feature.TREE, createStraightBlobTree
					(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2).build());
	//Short oak tree with bees feature, the chance for a beehive is vanilla's 5%, but might be worth adding an adjust to config.yaml maybe ?
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_OAK_BEES = 
			FeatureUtils.register("short_oak_bees", Feature.TREE, createStraightBlobTree
					(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2).decorators(List.of(BEEHIVE_005)).build());
	//Short birch tree with bees feature
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_BIRCH_BEES = 
			FeatureUtils.register("short_birch_bees", Feature.TREE, createStraightBlobTree
					(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2).decorators(List.of(BEEHIVE_005)).build());
	//short jungle tree feature
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_JUNGLE = 
			FeatureUtils.register("short_jungle", Feature.TREE, createStraightBlobTree
					(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, 4, 2).build());
	//short spruce, this one is a fair bit longer lmao
	//might also be worth adding options to configure type of grower for each tree instead of hard coding ?
	//idk, I am also lazy and don't wanna wrap my head around how the config parser woks
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_SPRUCE = 
			FeatureUtils.register(
					"short_spruce", Feature.TREE, (
						new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), 
						new StraightTrunkPlacer(5, 0, 0), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), 
						new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)), 
						new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());
	//short acacia
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_ACACIA = 
			FeatureUtils.register(
					"short_acacia", Feature.TREE, (
						new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.ACACIA_LOG), 
						new ForkingTrunkPlacer(5, 0 ,0), BlockStateProvider.simple(Blocks.ACACIA_LEAVES), 
						new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)), 
						new TwoLayersFeatureSize(1, 0, 2))).ignoreVines().build());
	//short 2x2 jungle tree
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_MEGA_JUNGLE = 
			FeatureUtils.register(
					"short_mega_jungle_tree", Feature.TREE, (
						new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.JUNGLE_LOG), 
						new MegaJungleTrunkPlacer(10, 0, 0), BlockStateProvider.simple(Blocks.JUNGLE_LEAVES), 
						new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2), 
						new TwoLayersFeatureSize(1, 1, 2))).
					decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, LeaveVineDecorator.INSTANCE)).build());
	//short 2x2 spruce tree
	//should 2x2 pine tree also be included ?
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_MEGA_SPRUCE = 
			FeatureUtils.register(
					"short_mega_spruce", Feature.TREE, (
						new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), 
						new GiantTrunkPlacer(13, 0, 0), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), 
						new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),  //oh maybe this uniformint is why sometimes it still fails to grow with height restriction even with the other things
						new TwoLayersFeatureSize(1, 1, 2)))
					.decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)))).build());
	//short 2x2 ofc because that's the only way it grows (normally) dark oak tree
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_DARK_OAK = 
			FeatureUtils.register(
					"short_dark_oak", Feature.TREE, (
						new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.DARK_OAK_LOG), 
						new DarkOakTrunkPlacer(6, 0, 0), BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES), 
						new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)), 
						new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty()))).ignoreVines().build());
   //azalea
	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SHORT_AZALEA_TREE = 
			FeatureUtils.register(
					"short_azalea_tree", Feature.TREE, (
						new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG), 
						new BendingTrunkPlacer(4, 0, 0, 3, UniformInt.of(1, 2)), 
						new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
								.add(Blocks.AZALEA_LEAVES.defaultBlockState(), 3).add(Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 1)), 
						new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 50), 
						new TwoLayersFeatureSize(1, 0, 1))).dirt(BlockStateProvider.simple(Blocks.ROOTED_DIRT)).forceDirt().build());


}

