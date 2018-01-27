package me.lordsaad.modeoff.api.plot;

import com.google.common.collect.HashMultimap;
import me.lordsaad.modeoff.api.ConfigValues;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumMap;
import java.util.HashMap;

/**
 * Created by LordSaad.
 */
@SideOnly(Side.CLIENT)
public class PlotCacher {

	public EnumMap<BlockRenderLayer, HashMultimap<IBlockState, BlockPos>> blocks = new EnumMap<>(BlockRenderLayer.class);

	private HashMap<Long, IBlockState> tmp = new HashMap<>();

	public PlotCacher(IBlockAccess world, Plot plot) {
		PlotDimensions dimensions = plot.getDimensions();

		for (BlockPos pos : plot.getDimensions().getAllBlocks(3)) {
			IBlockState state = world.getBlockState(pos);

			if (state.getBlock() == Blocks.AIR) continue;

			tmp.put(pos.toLong(), state);
		}

		BlockPos min = dimensions.getCorner1();
		BlockPos max = dimensions.getCorner2();

		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		for (Long pos : tmp.keySet()) {
			IBlockState state = tmp.get(pos);

			boolean surrounded = true;
			for (EnumFacing facing : EnumFacing.VALUES) {
				blockPos.setPos(BlockPos.fromLong(pos)).offset(facing);

				if (!tmp.containsKey(blockPos.toLong())) {
					surrounded = false;
					break;
				}

				IBlockState offsetState = tmp.get(blockPos.toLong());
				if (offsetState == null) {
					surrounded = false;
					break;
				}

				if (offsetState.getBlock() == Blocks.AIR
						|| !offsetState.isFullBlock()
						|| !offsetState.isOpaqueCube()
						|| !offsetState.isBlockNormalCube()
						|| !offsetState.isNormalCube()
						|| offsetState.isTranslucent()
						|| !offsetState.getMaterial().isSolid()) {
					surrounded = false;
					break;
				}
			}

			//if (!surrounded) {
			BlockRenderLayer layer = state.getBlock().getBlockLayer();
			HashMultimap<IBlockState, BlockPos> multimap = blocks.get(layer);
			if (multimap == null) multimap = HashMultimap.create();
			multimap.put(state, BlockPos.fromLong(pos));//.subtract(new BlockPos(plot.getPlotPos().getXi(), ConfigValues.y, plot.getPlotPos().getYi())));
			blocks.put(layer, multimap);
			//}
		}
	}

	/**
	 * Will cache the area dimensions.getCorner1() the crane selected.
	 *
	 * @param world  The world object.
	 * @param origin A block in the crane.
	 */
	public PlotCacher(World world, BlockPos origin) {
		int width = ConfigValues.plotSize;
		IBlockState[][][] tempStateCache = new IBlockState[width * 2][100][width * 2];

		// FIRST ITERATION
		// Save everything. Check surroundings in second iteration.
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = -width; i < width; i++) {
			for (int j = 0; j < 100; j++) {
				for (int k = -width; k < width; k++) {
					pos.setPos(origin.getX() + i, origin.getY() + k, origin.getZ() + j);

					IBlockState state = world.getBlockState(pos);
					tempStateCache[i + width][j][k + width] = state;
				}
			}
		}

		// SECOND ITERATION
		// Check surrounding iterations
		BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
		for (int i = -width; i < width; i++) {
			for (int j = 0; j < 100; j++) {
				for (int k = -width; k < width; k++) {
					IBlockState state = tempStateCache[i + width][j][k + width];
					if (state == null) continue;

					boolean surrounded = true;
					for (EnumFacing facing : EnumFacing.VALUES) {
						offset.setPos(i, j, k).offset(facing);
						if (offset.getX() + width >= tempStateCache.length
								|| offset.getY() >= tempStateCache[0].length
								|| offset.getZ() + width >= tempStateCache.length) {
							surrounded = false;
							break;
						}

						IBlockState offsetState = tempStateCache[offset.getX() + width][offset.getY()][offset.getZ() + width];
						if (offsetState == null) {
							surrounded = false;
							break;
						}

						if (!offsetState.isFullBlock()
								|| !offsetState.isOpaqueCube()
								|| !offsetState.isBlockNormalCube()
								|| !offsetState.isNormalCube()
								|| offsetState.isTranslucent()
								|| offsetState.getMaterial().isLiquid()
								|| !offsetState.getMaterial().isSolid()) {
							surrounded = false;
							break;
						}
					}

					if (!surrounded) {
						pos.setPos(i - width, j, k - width);//.subtract(new Vec3i(width, 0, width)).add(origin);
						BlockRenderLayer layer = state.getBlock().getBlockLayer();
						HashMultimap<IBlockState, BlockPos> multimap = blocks.get(layer);
						if (multimap == null) multimap = HashMultimap.create();
						multimap.put(state, pos.toImmutable());
						blocks.put(layer, multimap);
					}
				}
			}
		}
	}
}
