package agency.highlysuspect.halogen.gen.data;

import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.gen.GenInit;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

public class BlockDropGen implements DataProvider{
	@Override
	public void run(DataCache cache) throws IOException {
		writeSelfDrop(cache,
			ImmutableList.of(HaloBlocks.NODE)
		);
		
		writeNoDrop(cache,
			ImmutableList.of(
				HaloBlocks.LARGE_MOONLIGHT_PRISM,
				HaloBlocks.SMALL_MOONLIGHT_PRISM
			)
		);
	}
	
	@SafeVarargs
	private void writeSelfDrop(DataCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		iterateWrite(this::drops, cache, listOfLists);
	}
	
	@SafeVarargs
	private void writeNoDrop(DataCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		iterateWrite((__) -> dropsNothing(), cache, listOfLists);
	}
	
	@SafeVarargs
	private static void iterateWrite(Function<Block, LootTable.Builder> funny, DataCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		for(Iterable<? extends Block> list : listOfLists) for(Block b : list) write(cache, b, funny.apply(b));
	}
	
	private static void write(DataCache cache, Block b, LootTable.Builder builder) throws IOException {
		Identifier id = Registry.BLOCK.getId(b);
		DataProvider.writeToPath(GenInit.GSON, cache, LootManager.toJson(builder.type(LootContextTypes.BLOCK).build()), outPath(id));
	}
	
	private static Path outPath(Identifier id) {
		return GenInit.OUT_ROOT.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}
	
	////
	
	private LootTable.Builder drops(ItemConvertible drop) {
		return LootTable.builder().pool(
			LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
				.with(ItemEntry.builder(drop))
				.conditionally(SurvivesExplosionLootCondition.builder()));
	}
	
	private LootTable.Builder dropsNothing() {
		return LootTable.builder();
	}
	
	@Override
	public String getName() {
		return "halo gen block drops!!";
	}
}
