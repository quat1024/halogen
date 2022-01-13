package agency.highlysuspect.halogen.mechanics.aura;

import net.minecraft.world.item.Item;

public class AuraCrystalItem extends Item {
	public AuraCrystalItem(AuraType type, Properties settings) {
		super(settings);
		this.type = type;
	}
	
	private final AuraType type;
}
