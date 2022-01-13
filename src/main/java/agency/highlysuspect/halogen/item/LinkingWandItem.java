package agency.highlysuspect.halogen.item;

import agency.highlysuspect.halogen.block.entity.NodeBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class LinkingWandItem extends Item {
	public LinkingWandItem(Properties settings) {
		super(settings);
	}
	
	private static final String BINDING_KEY = "halogen-binding";
	
	private BlockPos getBindingSource(ItemStack stack) {
		if(!stack.hasTag()) return null;
		
		CompoundTag nbt = stack.getTag(); assert nbt != null;
		return NbtUtils.readBlockPos(nbt.getCompound(BINDING_KEY));
	}
	
	private boolean hasBindingSource(ItemStack stack) {
		if(!stack.hasTag()) return false;
		
		CompoundTag nbt = stack.getTag(); assert nbt != null;
		return nbt.contains(BINDING_KEY);
	}
	
	private void putBindingSource(ItemStack stack, BlockPos bindingPos) {
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.put(BINDING_KEY, NbtUtils.writeBlockPos(bindingPos));
	}
	
	private void clearBindingSource(ItemStack stack) {
		if(!stack.hasTag()) return;
		CompoundTag nbt = stack.getTag(); assert nbt != null;
		nbt.remove(BINDING_KEY);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		
		if(world.getBlockEntity(pos) instanceof NodeBlockEntity) {
			if(hasBindingSource(stack)) {
				BlockPos bindingSource = getBindingSource(context.getItemInHand()); assert bindingSource != null;
				
				if(world.getBlockEntity(bindingSource) instanceof NodeBlockEntity src && src.onLinkingWand(pos)) {
					clearBindingSource(stack);
					return InteractionResult.SUCCESS;
				}
			} else {
				putBindingSource(stack, pos);
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		
		if(user.isShiftKeyDown() && hasBindingSource(stack)) {
			clearBindingSource(stack);
			return InteractionResultHolder.sidedSuccess(stack, true);
		}
		
		return InteractionResultHolder.pass(stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		if(context.isAdvanced() && hasBindingSource(stack)) tooltip.add(new TextComponent("binding pos: " + getBindingSource(stack)));
	}
}
