package agency.highlysuspect.halogen.block;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.block.entity.HaloBlockEntityTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class HaloBlocks {
	public static final NodeBlock NODE = new NodeBlock(
		AbstractBlock.Settings.copy(Blocks.STONE)
			.nonOpaque()
			.blockVision((state, world, pos) -> false)
	);
	
	public static final Block LARGE_MOONLIGHT_PRISM = new MoonlightPrismBlock(
		() -> HaloBlockEntityTypes.LARGE_MOONLIGHT_PRISM,
		AbstractBlock.Settings.copy(Blocks.STONE)
			.strength(5f)
			.nonOpaque()
			.dropsNothing() // :)
	);
	
	public static final Block SMALL_MOONLIGHT_PRISM = new MoonlightPrismBlock(
		() -> HaloBlockEntityTypes.SMALL_MOONLIGHT_PRISM,
		AbstractBlock.Settings.copy(Blocks.STONE)
			.strength(4f)
			.nonOpaque()
			.dropsNothing() // :)
	);
	
	//Lol
	public static final Block MOONLIGHT_CLUSTER = new AmethystClusterBlock(7, 3, AbstractBlock.Settings.copy(Blocks.AMETHYST_CLUSTER));
	
	private static <B extends Block> B reg(String idPath, B b) {
		return Registry.register(Registry.BLOCK, Init.id(idPath), b);
	}
	
	public static void onInitialize() {
		reg("node", NODE);
		reg("large_moonlight_prism", LARGE_MOONLIGHT_PRISM);
		reg("small_moonlight_prism", SMALL_MOONLIGHT_PRISM);
		reg("moonlight_cluster", MOONLIGHT_CLUSTER);
	}
}
