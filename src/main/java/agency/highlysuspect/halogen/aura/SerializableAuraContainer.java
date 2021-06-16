package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.Dirtyable;
import net.minecraft.nbt.NbtCompound;

public interface SerializableAuraContainer extends AuraContainer, Dirtyable {
	NbtCompound writeNbt();
	void readNbt(NbtCompound nbt);
}
