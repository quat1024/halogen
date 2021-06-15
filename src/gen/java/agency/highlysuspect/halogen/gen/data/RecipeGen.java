package agency.highlysuspect.halogen.gen.data;

import agency.highlysuspect.halogen.gen.GenInit;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class RecipeGen implements DataProvider {
	@Override
	public void run(DataCache cache) throws IOException {
		Consumer<RecipeJsonProvider> saver = r -> {
			try {
				Identifier recipeId = r.getRecipeId();
				Path recipePath = GenInit.OUT_ROOT.resolve("data/" + recipeId.getNamespace() + "/recipes/" + recipeId.getPath() + ".json");
				
				DataProvider.writeToPath(GenInit.GSON, cache, r.toJson(), recipePath);
				
				JsonObject advancementJson = r.toAdvancementJson();
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
					DataProvider.writeToPath(GenInit.GSON, cache, advancementJson, advancementPath);
				}
			} catch (IOException e) { //grumble grumble
				throw new RuntimeException(e);
			}
		};
		
		//"root" recipe advancement, referenced above in the saver.
		JsonObject root = Advancement.Task.create().criterion("impossible", new ImpossibleCriterion.Conditions()).toJson();
		DataProvider.writeToPath(GenInit.GSON, cache, root, GenInit.OUT_ROOT.resolve("data/halogen/advancements/recipes/root.json"));
		
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
	
	private static void inputAndCriterion(ShapedRecipeJsonFactory recipe, String name, Character c, ItemConvertible item) {
		recipe.input(c, item);
		recipe.criterion(name, cond(item));
	}
	
	private static InventoryChangedCriterion.Conditions cond(ItemConvertible item) {
		return new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[] {ItemPredicate.Builder.create().items(item).build()});
	}
	
	@Override
	public String getName() {
		return "Halogen recipes !";
	}
}
