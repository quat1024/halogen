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
	
	public static final AuraCrystalItem WHITE_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.WHITE.stackOf(10), settings());
	public static final AuraCrystalItem RED_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.RED.stackOf(10), settings());
	public static final AuraCrystalItem BLUE_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.BLUE.stackOf(100), settings());
	public static final LinkingWandItem LINKING_WAND = new LinkingWandItem(settings().maxCount(1));
	
	public static final Item MOONLIGHT_SHARD = new Item(settings()); 
	
	private static void reg(String idPath, Item i) {
		Registry.register(Registry.ITEM, Init.id(idPath), i);
	}
	
	private static void regBlockItem(Block b) {
		Registry.register(Registry.ITEM, Registry.BLOCK.getId(b), new BlockItem(b, settings()));
	}
	
	public static void onInitialize() {
		regBlockItem(HaloBlocks.NODE);
		regBlockItem(HaloBlocks.LARGE_MOONLIGHT_PRISM);
		regBlockItem(HaloBlocks.SMALL_MOONLIGHT_PRISM);
		
		reg("white_aura_crystal", WHITE_AURA_CRYSTAL);
		reg("red_aura_crystal", RED_AURA_CRYSTAL);
		reg("blue_aura_crystal", BLUE_AURA_CRYSTAL);
		
		reg("linking_wand", LINKING_WAND);
		reg("moonlight_shard", MOONLIGHT_SHARD);
	}
	
	private static Item.Settings settings() {
		return new Item.Settings().group(GROUP);
	}
	
	private static ItemStack icon() {
		return new ItemStack(HaloBlocks.NODE);
	}
}
