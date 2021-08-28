package agency.highlysuspect.halogen.block;

import agency.highlysuspect.halogen.aura.container.AuraContainer;
import agency.highlysuspect.halogen.block.entity.HaloBlockEntityTypes;
import agency.highlysuspect.halogen.block.entity.NodeBlockEntity;
import agency.highlysuspect.halogen.block.entity.TickerUtil;
import agency.highlysuspect.halogen.jankComponent.HasAuraContainer;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NodeBlock extends Block implements BlockEntityProvider {
	public NodeBlock(Settings settings) {
		super(settings);
	}
	
	private static final VoxelShape NODE_SHAPE = VoxelShapes.cuboid(4/16d, 4/16d, 4/16d, 12/16d, 12/16d, 12/16d);
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if(world.isClient()) return;
		if(!(world.getBlockEntity(pos) instanceof NodeBlockEntity node)) return;
		AuraContainer nodeContainer = node.getAuraContainer();
		
		if(!(entity instanceof ItemEntity itemEntity)) return;
		ItemStack stack = itemEntity.getStack();
		if(!(stack.getItem() instanceof HasAuraContainer containerHaver)) return;
		
		try(Transaction tx = new Transaction()) {
			boolean allFit = containerHaver.getAuraContainer().pourAllInto(nodeContainer, tx);
			if(allFit) {
				tx.commit();
				
				//Need a better idiom for extracting from items.
				stack.decrement(1);
				itemEntity.setStack(itemEntity.getStack()); //force an item entity sync
			} else tx.rollback();
		}
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!(world.getBlockEntity(pos) instanceof NodeBlockEntity node)) return ActionResult.PASS;
		AuraContainer nodeContainer = node.getAuraContainer();
		
		ItemStack stack = player.getStackInHand(hand);
		if(!(stack.getItem() instanceof HasAuraContainer containerHaver)) return ActionResult.PASS;
		
		if(!world.isClient) {
			try(Transaction tx = new Transaction()) {
				boolean allFit = containerHaver.getAuraContainer().pourAllInto(nodeContainer, tx);
				
				if(allFit) {
					tx.commit();
					//Need a better idiom for extracting from items.
					stack.decrement(1);
				} else tx.rollback();
			}
		}
		
		return ActionResult.SUCCESS;
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return HaloBlockEntityTypes.NODE.instantiate(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return TickerUtil.downcastTickerMemberFunc(type, HaloBlockEntityTypes.NODE, NodeBlockEntity::tickServer);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return NODE_SHAPE;
	}
}
