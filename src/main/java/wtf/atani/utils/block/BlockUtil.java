package wtf.atani.utils.block;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BlockUtil {

	public static final List<Block> invalid = Arrays.asList(
			Blocks.air,
			Blocks.water,
			Blocks.lava,
			Blocks.flowing_water,
			Blocks.flowing_lava,
			Blocks.command_block,
			Blocks.chest,
			Blocks.crafting_table,
			Blocks.enchanting_table,
			Blocks.furnace,
			Blocks.noteblock,
			Blocks.torch,
			Blocks.redstone_torch,
			Blocks.tallgrass,
			Blocks.cocoa,
			Blocks.brewing_stand,
			Blocks.activator_rail,
			Blocks.rail,
			Blocks.detector_rail,
			Blocks.golden_rail
	);

	
}
