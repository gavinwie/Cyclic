package com.lothrazar.cyclic.block.breaker;

import com.lothrazar.cyclic.block.TileBlockEntityCyclic;
import com.lothrazar.cyclic.registry.BlockRegistry;
import com.lothrazar.cyclic.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TileBreaker extends TileBlockEntityCyclic implements MenuProvider {

  static enum Fields {
    REDSTONE, TIMER;
  }

  static final int MAX = 64000;
  public static final int TIMER_FULL = 500;

  public TileBreaker(BlockPos pos, BlockState state) {
    super(TileRegistry.BREAKER.get(), pos, state);
  }

  public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileBreaker e) {
    e.tick();
  }

  public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileBreaker e) {
    e.tick();
  }

  public void tick() {
    if (this.requiresRedstone() && !this.isPowered()) {
      setLitProperty(false);
      return;
    }
    setLitProperty(true);
    if (level.isClientSide) {
      return;
    }
    BlockPos target = worldPosition.relative(this.getCurrentFacing());
    if (this.isValid(target)) {
      //old way would pass thru here and try to mine minecraft:water 
      this.level.destroyBlock(target, true);
    }
  }

  /**
   * Avoid mining source liquid blocks and unbreakable
   */
  private boolean isValid(BlockPos target) {
    BlockState state = level.getBlockState(target);
    if (level.isEmptyBlock(target)
        && state.getDestroySpeed(level, target) >= 0) {
      return false;
    }
    if (state.getFluidState() != null && state.getFluidState().isEmpty() == false) {
      //am i a solid waterlogged state block? 
      if (state.hasProperty(BlockStateProperties.WATERLOGGED) == false) {
        //pure liquid. but this will make canHarvestBlock go true  
        return false;
      }
    }
    return true;
  }

  @Override
  public Component getDisplayName() {
    return BlockRegistry.BREAKER.get().getName();
  }

  @Override
  public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
    return new ContainerBreaker(i, level, worldPosition, playerInventory, playerEntity);
  }

  @Override
  public void setField(int field, int value) {
    switch (Fields.values()[field]) {
      case REDSTONE:
        this.needsRedstone = value % 2;
      break;
      case TIMER:
        timer = value;
      break;
    }
  }

  @Override
  public int getField(int field) {
    switch (Fields.values()[field]) {
      case REDSTONE:
        return this.needsRedstone;
      case TIMER:
        return timer;
    }
    return 0;
  }

  public int getEnergyMax() {
    return MAX;
  }
}
