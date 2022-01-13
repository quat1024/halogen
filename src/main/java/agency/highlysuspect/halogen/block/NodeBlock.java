package agency.highlysuspect.halogen.block;

import agency.highlysuspect.halogen.aura.container.AuraContainer;
import agency.highlysuspect.halogen.block.entity.HaloBlockEntityTypes;
import agency.highlysuspect.halogen.block.entity.NodeBlockEntity;
import agency.highlysuspect.halogen.block.entity.TickerUtil;
import agency.highlysuspect.halogen.jankComponent.HasAuraContainer;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NodeBlock extends Block implements EntityBlock {
	public NodeBlock(Properties settings) {
		super(settings);
	}
	
	private static final VoxelShape NODE_SHAPE = Shapes.box(4/16d, 4/16d, 4/16d, 12/16d, 12/16d, 12/16d);
	
	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if(world.isClientSide()) return;
		if(!(world.getBlockEntity(pos) instanceof NodeBlockEntity node)) return;
		AuraContainer nodeContainer = node.getAuraContainer();
		
		if(!(entity instanceof ItemEntity itemEntity)) return;
		ItemStack stack = itemEntity.getItem();
		if(!(stack.getItem() instanceof HasAuraContainer containerHaver)) return;
		
		try(Transaction tx = new Transaction()) {
			boolean allFit = containerHaver.getAuraContainer().pourAllInto(nodeContainer, tx);
			if(allFit) {
				tx.commit();
				
				//Need a better idiom for extracting from items.
				stack.shrink(1);
				itemEntity.setItem(itemEntity.getItem()); //force an item entity sync
			} else tx.rollback();
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(!(world.getBlockEntity(pos) instanceof NodeBlockEntity node)) return InteractionResult.PASS;
		AuraContainer nodeContainer = node.getAuraContainer();
		
		ItemStack stack = player.getItemInHand(hand);
		if(!(stack.getItem() instanceof HasAuraContainer containerHaver)) return InteractionResult.PASS;
		
		if(!world.isClientSide) {
			try(Transaction tx = new Transaction()) {
				boolean allFit = containerHaver.getAuraContainer().pourAllInto(nodeContainer, tx);
				
				if(allFit) {
					tx.commit();
					//Need a better idiom for extracting from items.
					stack.shrink(1);
				} else tx.rollback();
			}
		}
		
		return InteractionResult.SUCCESS;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return HaloBlockEntityTypes.NODE.create(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickerUtil.downcastTickerMemberFunc(type, HaloBlockEntityTypes.NODE, NodeBlockEntity::tickServer);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return NODE_SHAPE;
	}
}
