package agency.highlysuspect.halogen.item;

import agency.highlysuspect.halogen.block.entity.NodeBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LinkingWandItem extends Item {
	public LinkingWandItem(Settings settings) {
		super(settings);
	}
	
	private static final String BINDING_KEY = "halogen-binding";
	
	private BlockPos getBindingSource(ItemStack stack) {
		if(!stack.hasNbt()) return null;
		
		NbtCompound nbt = stack.getNbt(); assert nbt != null;
		return NbtHelper.toBlockPos(nbt.getCompound(BINDING_KEY));
	}
	
	private boolean hasBindingSource(ItemStack stack) {
		if(!stack.hasNbt()) return false;
		
		NbtCompound nbt = stack.getNbt(); assert nbt != null;
		return nbt.contains(BINDING_KEY);
	}
	
	private void putBindingSource(ItemStack stack, BlockPos bindingPos) {
		NbtCompound nbt = stack.getOrCreateNbt();
		nbt.put(BINDING_KEY, NbtHelper.fromBlockPos(bindingPos));
	}
	
	private void clearBindingSource(ItemStack stack) {
		if(!stack.hasNbt()) return;
		NbtCompound nbt = stack.getNbt(); assert nbt != null;
		nbt.remove(BINDING_KEY);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ItemStack stack = context.getStack();
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		
		if(world.getBlockEntity(pos) instanceof NodeBlockEntity) {
			if(hasBindingSource(stack)) {
				BlockPos bindingSource = getBindingSource(context.getStack()); assert bindingSource != null;
				
				if(world.getBlockEntity(bindingSource) instanceof NodeBlockEntity src && src.onLinkingWand(pos)) {
					clearBindingSource(stack);
					return ActionResult.SUCCESS;
				}
			} else {
				putBindingSource(stack, pos);
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.PASS;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		
		if(user.isSneaking() && hasBindingSource(stack)) {
			clearBindingSource(stack);
			return TypedActionResult.success(stack, true);
		}
		
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if(context.isAdvanced() && hasBindingSource(stack)) tooltip.add(new LiteralText("binding pos: " + getBindingSource(stack)));
	}
}
