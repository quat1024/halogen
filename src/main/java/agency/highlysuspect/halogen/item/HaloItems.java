package agency.highlysuspect.halogen.item;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.aura.HaloAuraTypes;
import agency.highlysuspect.halogen.block.HaloBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class HaloItems {
	public static final CreativeModeTab GROUP = FabricItemGroupBuilder.build(Init.id("group"), HaloItems::icon);
	
	public static final AuraCrystalItem WHITE_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.WHITE.stackOf(10), settings());
	public static final AuraCrystalItem RED_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.RED.stackOf(10), settings());
	public static final AuraCrystalItem BLUE_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.BLUE.stackOf(100), settings());
	public static final LinkingWandItem LINKING_WAND = new LinkingWandItem(settings().stacksTo(1));
	
	public static final Item MOONLIGHT_SHARD = new Item(settings()); 
	
	private static void reg(String idPath, Item i) {
		Registry.register(Registry.ITEM, Init.id(idPath), i);
	}
	
	private static void regBlockItem(Block b) {
		Registry.register(Registry.ITEM, Registry.BLOCK.getKey(b), new BlockItem(b, settings()));
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
	
	private static Item.Properties settings() {
		return new Item.Properties().tab(GROUP);
	}
	
	private static ItemStack icon() {
		return new ItemStack(HaloBlocks.NODE);
	}
}
