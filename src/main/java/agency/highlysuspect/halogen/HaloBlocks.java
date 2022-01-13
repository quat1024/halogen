package agency.highlysuspect.halogen;

import agency.highlysuspect.halogen.mechanics.prism.MoonlightPrismBlock;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class HaloBlocks {
	public static final Block LARGE_MOONLIGHT_PRISM = new MoonlightPrismBlock(
		() -> HaloBlockEntityTypes.LARGE_MOONLIGHT_PRISM,
		BlockBehaviour.Properties.copy(Blocks.STONE)
			.strength(5f)
			.noOcclusion()
			.noDrops() // :)
	);
	
	public static final Block SMALL_MOONLIGHT_PRISM = new MoonlightPrismBlock(
		() -> HaloBlockEntityTypes.SMALL_MOONLIGHT_PRISM,
		BlockBehaviour.Properties.copy(Blocks.STONE)
			.strength(4f)
			.noOcclusion()
			.noDrops() // :)
	);
	
	//Lol
	public static final Block MOONLIGHT_CLUSTER = new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER));
	
	private static <B extends Block> B reg(String idPath, B b) {
		return Registry.register(Registry.BLOCK, Init.id(idPath), b);
	}
	
	public static void onInitialize() {
		reg("large_moonlight_prism", LARGE_MOONLIGHT_PRISM);
		reg("small_moonlight_prism", SMALL_MOONLIGHT_PRISM);
		reg("moonlight_cluster", MOONLIGHT_CLUSTER);
	}
}
