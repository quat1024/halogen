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

public class BlockDropGen implements DataProvider{
	@Override
	public void run(DataCache cache) throws IOException {
		doItAll(cache,
			ImmutableList.of(HaloBlocks.BASIC_NODE)
		);
	}
	
	@SafeVarargs
	private void doItAll(DataCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		for(Iterable<? extends Block> list : listOfLists) {
			for(Block b : list) {
				Identifier id = Registry.BLOCK.getId(b);
				LootTable dropTable = drops(b);
				DataProvider.writeToPath(GenInit.GSON, cache, LootManager.toJson(dropTable), outPath(id));
			}
		}
	}
	
	private Path outPath(Identifier id) {
		return GenInit.OUT_ROOT.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}
	
	private LootTable drops(ItemConvertible drop) {
		//Copy and paste of private "drops(ItemConvertible drop)" from BlockLootTableGenerator.
		//Also from there, private method addSurvivesExplosionCondition(ItemConvertible, LootConditionConsumingBuilder<T>) pasted into this class and inlined with intellij.
		//Im sorry
		return LootTable.builder().pool(
			LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
				.with(ItemEntry.builder(drop))
				.conditionally(SurvivesExplosionLootCondition.builder()))
			.type(LootContextTypes.BLOCK)
			.build();
	}
	
	@Override
	public String getName() {
		return "halo gen block drops!!";
	}
}
