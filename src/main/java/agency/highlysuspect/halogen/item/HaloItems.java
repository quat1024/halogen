package agency.highlysuspect.halogen.item;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.aura.HaloAuraTypes;
import agency.highlysuspect.halogen.block.HaloBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class HaloItems {
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(Init.id("group"), HaloItems::icon);
	
	public static final BlockItem NODE = blockItem(HaloBlocks.NODE);
	public static final BlockItem LARGE_MOONLIGHT_PRISM = blockItem(HaloBlocks.LARGE_MOONLIGHT_PRISM);
	public static final BlockItem SMALL_MOONLIGHT_PRISM = blockItem(HaloBlocks.SMALL_MOONLIGHT_PRISM);
	public static final AuraCrystalItem WHITE_AURA_CRYSTAL = reg("white_aura_crystal", new AuraCrystalItem(HaloAuraTypes.WHITE.stackOf(10), settings()));
	public static final AuraCrystalItem RED_AURA_CRYSTAL = reg("red_aura_crystal", new AuraCrystalItem(HaloAuraTypes.RED.stackOf(10), settings()));
	public static final AuraCrystalItem BLUE_AURA_CRYSTAL = reg("blue_aura_crystal", new AuraCrystalItem(HaloAuraTypes.BLUE.stackOf(100), settings()));
	public static final LinkingWandItem LINKING_WAND = reg("linking_wand", new LinkingWandItem(settings().maxCount(1)));
	
	private static BlockItem blockItem(Block b) {
		return Registry.register(Registry.ITEM, Registry.BLOCK.getId(b), new BlockItem(b, settings()));
	}
	
	private static <I extends Item> I reg(String idPath, I i) {
		return Registry.register(Registry.ITEM, Init.id(idPath), i);
	}
	
	private static Item.Settings settings() {
		return new Item.Settings().group(GROUP);
	}
	
	private static ItemStack icon() {
		return new ItemStack(NODE);
	}
	
	public static void onInitialize() {
		//Classload
	}
}
