package agency.highlysuspect.halogen.aura;

import net.minecraft.nbt.NbtCompound;

public interface SerializableAuraContainer extends AuraContainer {
	NbtCompound writeNbt();
	void readNbt(NbtCompound nbt);
}
