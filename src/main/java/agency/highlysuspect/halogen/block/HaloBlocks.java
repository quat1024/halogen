package agency.highlysuspect.halogen.block;

import agency.highlysuspect.halogen.Init;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class HaloBlocks {
	public static final NodeBlock NODE = reg("node", new NodeBlock(
		AbstractBlock.Settings.copy(Blocks.STONE)
			.blockVision((state, world, pos) -> false)
	));
	
	//todo real blocks
	public static final Block LARGE_MOONLIGHT_PRISM = reg("large_moonlight_prism", new Block(AbstractBlock.Settings.copy(Blocks.STONE)));
	public static final Block SMALL_MOONLIGHT_PRISM = reg("small_moonlight_prism", new Block(AbstractBlock.Settings.copy(Blocks.STONE)));
	
	private static <B extends Block> B reg(String idPath, B b) {
		return Registry.register(Registry.BLOCK, Init.id(idPath), b);
	}
	
	public static void onInitialize() {
		//Classload
	}
}
