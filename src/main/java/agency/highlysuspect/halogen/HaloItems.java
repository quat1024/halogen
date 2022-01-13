package agency.highlysuspect.halogen;

import agency.highlysuspect.halogen.mechanics.aura.AuraCrystalItem;
import agency.highlysuspect.halogen.mechanics.aura.HaloAuraTypes;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class HaloItems {
	public static final CreativeModeTab GROUP = FabricItemGroupBuilder.build(Init.id("group"), HaloItems::icon);
	
	public static final AuraCrystalItem WHITE_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.WHITE, settings());
	public static final AuraCrystalItem RED_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.RED, settings());
	public static final AuraCrystalItem BLUE_AURA_CRYSTAL = new AuraCrystalItem(HaloAuraTypes.BLUE, settings());
	
	public static final Item MOONLIGHT_SHARD = new Item(settings()); 
	public static final Item INERT_AURA = new Item(settings());
	
	private static void reg(String idPath, Item i) {
		Registry.register(Registry.ITEM, Init.id(idPath), i);
	}
	
	private static void regBlockItem(Block b) {
		Registry.register(Registry.ITEM, Registry.BLOCK.getKey(b), new BlockItem(b, settings()));
	}
	
	public static void onInitialize() {
		regBlockItem(HaloBlocks.LARGE_MOONLIGHT_PRISM);
		regBlockItem(HaloBlocks.SMALL_MOONLIGHT_PRISM);
		
		reg("white_aura_crystal", WHITE_AURA_CRYSTAL);
		reg("red_aura_crystal", RED_AURA_CRYSTAL);
		reg("blue_aura_crystal", BLUE_AURA_CRYSTAL);
		
		reg("moonlight_shard", MOONLIGHT_SHARD);
		reg("inert_aura", INERT_AURA);
	}
	
	private static Item.Properties settings() {
		return new Item.Properties().tab(GROUP);
	}
	
	private static ItemStack icon() {
		return new ItemStack(WHITE_AURA_CRYSTAL);
	}
}
