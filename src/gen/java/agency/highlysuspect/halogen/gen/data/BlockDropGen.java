package agency.highlysuspect.halogen.gen.data;

import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.gen.GenInit;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

public class BlockDropGen implements DataProvider{
	@Override
	public void run(HashCache cache) throws IOException {
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
	private void writeSelfDrop(HashCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		iterateWrite(this::drops, cache, listOfLists);
	}
	
	@SafeVarargs
	private void writeNoDrop(HashCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		iterateWrite((__) -> dropsNothing(), cache, listOfLists);
	}
	
	@SafeVarargs
	private static void iterateWrite(Function<Block, LootTable.Builder> funny, HashCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		for(Iterable<? extends Block> list : listOfLists) for(Block b : list) write(cache, b, funny.apply(b));
	}
	
	private static void write(HashCache cache, Block b, LootTable.Builder builder) throws IOException {
		ResourceLocation id = Registry.BLOCK.getKey(b);
		DataProvider.save(GenInit.GSON, cache, LootTables.serialize(builder.setParamSet(LootContextParamSets.BLOCK).build()), outPath(id));
	}
	
	private static Path outPath(ResourceLocation id) {
		return GenInit.OUT_ROOT.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}
	
	////
	
	private LootTable.Builder drops(ItemLike drop) {
		return LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(drop))
				.when(ExplosionCondition.survivesExplosion()));
	}
	
	private LootTable.Builder dropsNothing() {
		return LootTable.lootTable();
	}
	
	@Override
	public String getName() {
		return "halo gen block drops!!";
	}
}
