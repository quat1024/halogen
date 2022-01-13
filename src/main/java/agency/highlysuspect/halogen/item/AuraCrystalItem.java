package agency.highlysuspect.halogen.item;

import agency.highlysuspect.halogen.aura.AuraType;
import net.minecraft.world.item.Item;

public class AuraCrystalItem extends Item {
	public AuraCrystalItem(AuraType type, Properties settings) {
		super(settings);
		this.type = type;
	}
	
	private final AuraType type;
}
