package agency.highlysuspect.halogen.item;

import agency.highlysuspect.halogen.aura.AuraContainer;
import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.aura.ImmutableAuraContainer;
import agency.highlysuspect.halogen.jankComponent.HasAuraContainer;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;

public class AuraCrystalItem extends Item implements HasAuraContainer {
	public AuraCrystalItem(AuraStack stack, Settings settings) {
		super(settings);
		this.contents = new ImmutableAuraContainer(ImmutableList.of(stack));
	}
	
	private final AuraContainer contents;
	
	@Override
	public AuraContainer getAuraContainer() {
		return contents;
	}
}
