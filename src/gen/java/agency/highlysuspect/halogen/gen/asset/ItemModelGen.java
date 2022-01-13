package agency.highlysuspect.halogen.gen.asset;

import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.gen.GenInit;
import agency.highlysuspect.halogen.item.HaloItems;
import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelGen implements DataProvider {
	private HashCache yeetCache;
	
	@Override
	public void run(HashCache cache) throws IOException {
		yeetCache = cache;
		
		itemWithBlockModelParent(HaloBlocks.LARGE_MOONLIGHT_PRISM, cache);
		itemWithBlockModelParent(HaloBlocks.SMALL_MOONLIGHT_PRISM, cache);
		
		layer0(HaloItems.WHITE_AURA_CRYSTAL, this::write);
		layer0(HaloItems.RED_AURA_CRYSTAL, this::write);
		layer0(HaloItems.BLUE_AURA_CRYSTAL, this::write);
	}
	
	private void itemWithBlockModelParent(Block b, HashCache cache) throws IOException {
		itemWithBlockModelParent(b, ModelLocationUtils.getModelLocation(b), cache);
	}
	
	private void itemWithBlockModelParent(Block b, ResourceLocation modelId, HashCache cache) throws IOException {
		ResourceLocation id = Registry.BLOCK.getKey(b);
		JsonElement modelJson = new DelegatedModel(modelId).get();
		DataProvider.save(GenInit.GSON, cache, modelJson, itemModelOutPath(id));
	}
	
	private static void layer0(ItemLike item, BiConsumer<ResourceLocation, Supplier<JsonElement>> consumer) {
		Item i = item.asItem();
		ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(i), TextureMapping.layer0(i), consumer);
	}
	
	private static void layer0(ItemLike item, ResourceLocation tex, BiConsumer<ResourceLocation, Supplier<JsonElement>> consumer) {
		Item i = item.asItem();
		ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(i), TextureMapping.layer0(tex), consumer);
	}
	
	private Path outPath(ResourceLocation id) {
		return GenInit.OUT_ROOT.resolve("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json");
	}
	
	private Path itemModelOutPath(ResourceLocation id) {
		return GenInit.OUT_ROOT.resolve("assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json");
	}
	
	//Demanded by some wacky stuff in vanilla model generators. Not great imo
	private void write(ResourceLocation id, Supplier<JsonElement> jsonSupplier) {
		try {
			DataProvider.save(GenInit.GSON, yeetCache, jsonSupplier.get(), outPath(id));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getName() {
		return "halogen item models";
	}
}
