package agency.highlysuspect.halogen.gen.data;

import agency.highlysuspect.halogen.gen.GenInit;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class RecipeGen implements DataProvider {
	@Override
	public void run(HashCache cache) throws IOException {
		Consumer<FinishedRecipe> saver = r -> {
			try {
				ResourceLocation recipeId = r.getId();
				Path recipePath = GenInit.OUT_ROOT.resolve("data/" + recipeId.getNamespace() + "/recipes/" + recipeId.getPath() + ".json");
				
				DataProvider.save(GenInit.GSON, cache, r.serializeRecipe(), recipePath);
				
				JsonObject advancementJson = r.serializeAdvancement();
				if(advancementJson != null) {
					//Minecraft's recipe provider always puts recipe advancements in its own namespace. Let's fix that.
					advancementJson.remove("parent");
					advancementJson.addProperty("parent", "halogen:recipes/root");
					
					//Recipe advancements, by default, go under a path defined by the creative tab name.
					//This doesn't make much sense outside the context of vanilla where items are categorized by genre, and when I have to namespace my tab name.
					//In dazzle the tab is named "dazzle.group" so they go under "dazzle/advancements/recipes/dazzle.group/___.json" and that's weird.
					//I'm just going to remove this extra path segment and hope everything works okay.
					String hoo = Objects.requireNonNull(r.getAdvancementId()).getPath().replace("dazzle.group/", "");
					
					Path advancementPath = GenInit.OUT_ROOT.resolve("data/" + recipeId.getNamespace() + "/advancements/" + hoo + ".json");
					DataProvider.save(GenInit.GSON, cache, advancementJson, advancementPath);
				}
			} catch (IOException e) { //grumble grumble
				throw new RuntimeException(e);
			}
		};
		
		//"root" recipe advancement, referenced above in the saver.
		JsonObject root = Advancement.Builder.advancement().addCriterion("impossible", new ImpossibleTrigger.TriggerInstance()).serializeToJson();
		DataProvider.save(GenInit.GSON, cache, root, GenInit.OUT_ROOT.resolve("data/halogen/advancements/recipes/root.json"));
		
//		DazzleBlocks.DYED_SHROOMLIGHTS.forEach((color, shroom) -> {
//			ShapelessRecipeJsonFactory recipe = ShapelessRecipeJsonFactory.create(shroom);
//			
//			inputAndCriterion(recipe, "has_shroom", Blocks.SHROOMLIGHT);
//			inputAndCriterion(recipe, "has_dye", GenUtil.dyeForColor(color));
//			
//			recipe.group("dyed_shroom");
//			
//			recipe.offerTo(saver);
//		});
	}
	
	private static void inputAndCriterion(ShapedRecipeBuilder recipe, String name, Character c, ItemLike item) {
		recipe.define(c, item);
		recipe.unlockedBy(name, cond(item));
	}
	
	private static InventoryChangeTrigger.TriggerInstance cond(ItemLike item) {
		return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new ItemPredicate[] {ItemPredicate.Builder.item().of(item).build()});
	}
	
	@Override
	public String getName() {
		return "Halogen recipes !";
	}
}
