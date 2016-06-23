package com.m4thg33k.tombmanygraves.blocks;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.api.state.TMGStateProps;
import com.m4thg33k.tombmanygraves.core.util.ChatHelper;
import com.m4thg33k.tombmanygraves.gui.TombManyGravesGuiHandler;
import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockDeath extends BaseBlock {

    public BlockDeath()
    {
        super(Names.DEATH_BLOCK, Material.WOOD, 100.0f, 100.0f);
        this.setDefaultState(blockState.getBaseState());
        this.setBlockUnbreakable();

        this.setRegistryName(TombManyGraves.MODID,Names.DEATH_BLOCK);

        this.setDefaultState(((IExtendedBlockState) blockState.getBaseState()).withProperty(TMGStateProps.HELD_STATE, null)
                                                                                .withProperty(TMGStateProps.HELD_WORLD, null)
                                                                                .withProperty(TMGStateProps.HELD_POS, null));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] {TMGStateProps.HELD_STATE, TMGStateProps.HELD_WORLD, TMGStateProps.HELD_POS});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Nonnull
    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = ((IExtendedBlockState) state).withProperty(TMGStateProps.HELD_WORLD, world).withProperty(TMGStateProps.HELD_POS, pos);

        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileDeathBlock)
        {
            TileDeathBlock tile = (TileDeathBlock) world.getTileEntity(pos);
            return ((IExtendedBlockState) state).withProperty(TMGStateProps.HELD_STATE, tile.getCamoState());
        } else
        {
            return state;
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDeathBlock();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote)
        {
            TileDeathBlock tileDeathBlock = (TileDeathBlock)worldIn.getTileEntity(pos);
            if (tileDeathBlock == null)
            {
                return true;
            }
            if (playerIn.isSneaking())
            {
                tileDeathBlock.toggleLock(playerIn);
            }
            else
            {
                tileDeathBlock.onRightClick(playerIn);
//                ChatHelper.sayMessage(worldIn,playerIn,"This grave belongs to: " + tileDeathBlock.getPlayerName());
//                if (tileDeathBlock.isSamePlayer(playerIn))
//                {
//                    ChatHelper.sayMessage(worldIn,playerIn,"Shift-click to lock/unlock the grave!");
//                }
            }
        }
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return super.getCollisionBoundingBox(blockState,worldIn,pos);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn) {
        TileDeathBlock tileDeathBlock = (TileDeathBlock)worldIn.getTileEntity(pos);
        if (entityIn instanceof EntityPlayer && !tileDeathBlock.isLocked() && tileDeathBlock.hasAccess((EntityPlayer)entityIn))
        {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
        }
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        TileDeathBlock tileDeathBlock = (TileDeathBlock)worldIn.getTileEntity(pos);
        if (entityIn instanceof EntityPlayer && entityIn.isEntityAlive())
        {
            if (TombManyGravesConfigs.REQUIRE_SNEAKING)
            {
                if (entityIn.isSneaking())
                {
                    tileDeathBlock.onCollision((EntityPlayer)entityIn);
                }
            }
            else {
                tileDeathBlock.onCollision((EntityPlayer) entityIn);
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileDeathBlock) {
            ((TileDeathBlock) worldIn.getTileEntity(pos)).dropAllItems();
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
//        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
        return EnumBlockRenderType.MODEL;
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.25f,0.25f,0.25f,0.75f,0.75f,0.75f);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return false;
    }

    @Override
    public int getLightOpacity(IBlockState state) {
        return 0;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return super.canRenderInLayer(state, layer);
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }
}
