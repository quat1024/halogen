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
	
	private BlockPos readBindingPos(ItemStack stack) {
		if(!stack.hasTag()) return null;
		
		NbtCompound nbt = stack.getTag(); assert nbt != null;
		return NbtHelper.toBlockPos(nbt.getCompound(BINDING_KEY));
	}
	
	private boolean hasBindingPos(ItemStack stack) {
		if(!stack.hasTag()) return false;
		
		NbtCompound nbt = stack.getTag(); assert nbt != null;
		return nbt.contains(BINDING_KEY);
	}
	
	private void putBindingPos(ItemStack stack, BlockPos bindingPos) {
		NbtCompound nbt = stack.getOrCreateTag();
		nbt.put(BINDING_KEY, NbtHelper.fromBlockPos(bindingPos));
	}
	
	private void clearBindingPos(ItemStack stack) {
		if(!stack.hasTag()) return;
		NbtCompound nbt = stack.getTag(); assert nbt != null;
		nbt.remove(BINDING_KEY);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ItemStack stack = context.getStack();
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		
		if(world.getBlockEntity(pos) instanceof NodeBlockEntity) {
			if(hasBindingPos(stack)) {
				BlockPos bindingPos = readBindingPos(context.getStack());
				assert bindingPos != null;
				
				if(world.getBlockEntity(bindingPos) instanceof NodeBlockEntity src && src.isValidBinding(pos)) {
					if(!world.isClient()) src.addOrRemoveBinding(pos);
					clearBindingPos(stack);
					return ActionResult.SUCCESS;
				}
			} else {
				putBindingPos(stack, pos);
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.PASS;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		
		if(user.isSneaking() && hasBindingPos(stack)) {
			clearBindingPos(stack);
			return TypedActionResult.success(stack, true);
		}
		
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if(context.isAdvanced() && hasBindingPos(stack)) tooltip.add(new LiteralText("binding pos: " + readBindingPos(stack)));
	}
}
