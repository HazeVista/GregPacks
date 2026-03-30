package com.astrogreg.gregpacks.block;

import com.astrogreg.gregpacks.item.OmniPackTier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public class OmniPackBlock extends BaseEntityBlock {

    private final OmniPackTier tier;

    public OmniPackBlock(OmniPackTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public OmniPackTier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OmniPackBlockEntity(pos, state, tier);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            if (level.getBlockEntity(pos) instanceof OmniPackBlockEntity be) {
                ItemStack packItem = be.toItemStack();
                be.setPickedUp(true);
                level.removeBlock(pos, false);

                // Try to find an empty slot and place directly there
                boolean added = false;
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    if (player.getInventory().getItem(i).isEmpty()) {
                        player.getInventory().setItem(i, packItem);
                        player.getInventory().setChanged();
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    player.drop(packItem, false);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Regular right-click → open GUI
        if (level.getBlockEntity(pos) instanceof OmniPackBlockEntity be) {
            NetworkHooks.openScreen(serverPlayer, be,
                    buf -> {
                        buf.writeByte(tier.ordinal());
                        buf.writeByte(tier.ordinal());
                        buf.writeByte(tier.ordinal());
                    });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof OmniPackBlockEntity be && !be.isPickedUp()) {
                ItemStack packItem = be.toItemStack();
                net.minecraft.world.Containers.dropItemStack(level,
                        pos.getX(), pos.getY(), pos.getZ(), packItem);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}