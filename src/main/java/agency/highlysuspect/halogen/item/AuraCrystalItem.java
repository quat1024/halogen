package agency.highlysuspect.halogen.item;

import agency.highlysuspect.halogen.aura.container.AuraContainer;
import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.aura.container.UnboundedHomogenousAuraContainer;
import agency.highlysuspect.halogen.jankComponent.HasAuraContainer;
import net.minecraft.item.Item;

public class AuraCrystalItem extends Item implements HasAuraContainer {
	public AuraCrystalItem(AuraStack<?> stack, Settings settings) {
		super(settings);
		this.stack = stack;
	}
	
	private final AuraStack<?> stack;
	
	@Override
	public AuraContainer getAuraContainer() {
		//TODO: This is VERY bad and leads to dupe bugs.
		// I probably need some special idiom for "getting a container off a stack" since it should depend on the stack's nbt.
		// I think mods like cardinal components have something for this.
		return new UnboundedHomogenousAuraContainer(stack);
	}
}
