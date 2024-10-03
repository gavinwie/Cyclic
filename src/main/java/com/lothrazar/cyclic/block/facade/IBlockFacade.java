package com.lothrazar.cyclic.block.facade;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface IBlockFacade {

  default VoxelShape getFacadeShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
    ITileFacade tile = this.getTileFacade(level, pos);
    if (tile != null && level instanceof Level realLevel) {
      BlockState tfs = tile.getFacadeState(realLevel);
      if (tfs != null) {
        return tfs.getShape(level, pos, ctx);
      }
    }
    return null;
  }

  default ITileFacade getTileFacade(BlockGetter level, BlockPos pos) {
    BlockEntity tile = level.getBlockEntity(pos);
    if (tile instanceof ITileFacade tf) {
      return tf;
    }
    return null;
  }


}
