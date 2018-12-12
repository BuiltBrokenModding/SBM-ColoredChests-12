package its_meow.coloredchests.chest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import its_meow.coloredchests.BlockRegistry;
import its_meow.coloredchests.ColoredChestsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockColoredChest extends BlockContainer {

	public BlockColoredChest(Material materialIn) {
		super(Material.WOOD);
		this.setRegistryName("coloredchest");
		this.setUnlocalizedName("coloredchest");
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setCreativeTab(ColoredChestsMod.tab);
	}

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	protected static final AxisAlignedBB NORTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0D, 0.9375D, 0.875D, 0.9375D);
	protected static final AxisAlignedBB SOUTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 1.0D);
	protected static final AxisAlignedBB WEST_CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
	protected static final AxisAlignedBB EAST_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 1.0D, 0.875D, 0.9375D);
	protected static final AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state)
	{
		return true;
	}

	/**
	 * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
	 * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
	 */
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}



	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int i = 0; i < ItemDye.DYE_COLORS.length; i++)
		{
			ItemStack stack = new ItemStack(BlockRegistry.blockChest);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("rgb", ItemDye.DYE_COLORS[i]);
			items.add(stack);
		}
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		Color color = null;
		TileEntity tile = source.getTileEntity(pos);
		if (tile instanceof TileEntityColoredChest)
		{
			color = ((TileEntityColoredChest) tile).color;
		}
		boolean block = isMatchingChest(source, pos.north(), color);
		boolean block1 = isMatchingChest(source, pos.south(), color);
		boolean block2 = isMatchingChest(source, pos.west(), color);
		boolean block3 = isMatchingChest(source, pos.east(), color);
		if (block)
		{
			return NORTH_CHEST_AABB;
		}
		else if (block1)
		{
			return SOUTH_CHEST_AABB;
		}
		else if (block2)
		{
			return WEST_CHEST_AABB;
		}
		else if (block3)
		{
			return EAST_CHEST_AABB;
		} 
		else 
		{
			return NOT_CONNECTED_AABB;
		}
	}

	/**
	 * Called after the block is set in the Chunk data, but before the Tile Entity is set
	 */
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		this.checkForSurroundingChests(worldIn, pos, state);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileEntityColoredChest)
		{
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
			{
				BlockPos blockpos = pos.offset(enumfacing);
				IBlockState iblockstate = worldIn.getBlockState(blockpos);
				Color color = ((TileEntityColoredChest) tile).color;
				if (isMatchingChest(worldIn, blockpos, color))
				{
					this.checkForSurroundingChests(worldIn, blockpos, iblockstate);
				}
			}
		}
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place logic
	 */
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		Color color = null;
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("rgb"))
		{
			color = ColoredChestsMod.getColor(stack.getTagCompound().getInteger("rgb"));
			((TileEntityColoredChest) world.getTileEntity(pos)).color = color;
		}
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		//System.out.println("Color of placed chest " + color);
		boolean flag = isMatchingChest(world, x, y, z - 1, color);
		boolean flag1 = isMatchingChest(world, x, y, z + 1, color);
		boolean flag2 = isMatchingChest(world, x - 1, y, z, color);
		boolean flag3 = isMatchingChest(world, x + 1, y, z, color);

		EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
		state = state.withProperty(FACING, enumfacing);
		BlockPos blockpos = pos.north();
		BlockPos blockpos1 = pos.south();
		BlockPos blockpos2 = pos.west();
		BlockPos blockpos3 = pos.east();

		if (!flag && !flag1 && !flag2 && !flag3)
		{
			world.setBlockState(pos, state, 3);
		}
		else if (enumfacing.getAxis() != EnumFacing.Axis.X || !flag && !flag1)
		{
			if (enumfacing.getAxis() == EnumFacing.Axis.Z && (flag2 || flag3))
			{
				if (flag2)
				{
					world.setBlockState(blockpos2, state, 3);
				}
				else
				{
					world.setBlockState(blockpos3, state, 3);
				}

				world.setBlockState(pos, state, 3);
			}
		}
		else
		{
			if (flag)
			{
				world.setBlockState(blockpos, state, 3);
			}
			else
			{
				world.setBlockState(blockpos1, state, 3);
			}

			world.setBlockState(pos, state, 3);
		}

		if (stack.hasDisplayName())
		{
			((TileEntityColoredChest) world.getTileEntity(pos)).setCustomName(stack.getDisplayName());
		}
	}

	public boolean isMatchingChest(IBlockAccess world, BlockPos pos, Color color)
	{
		Block block = world.getBlockState(pos).getBlock();
		TileEntity tile = world.getTileEntity(pos);
		return block == this && tile instanceof TileEntityColoredChest && ColoredChestsMod.doColorsMatch(color, ((TileEntityColoredChest) tile).color);
	}

	public boolean isMatchingChest(IBlockAccess world, int x, int y, int z, Color color)
	{
		BlockPos pos = new BlockPos(x,y,z);
		return isMatchingChest(world, pos, color);
	}

	public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state)
	{
		if (worldIn.isRemote)
		{
			return state;
		}
		else
		{
			IBlockState iblockstate = worldIn.getBlockState(pos.north());
			IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
			IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
			IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

			if (iblockstate.getBlock() != this && iblockstate1.getBlock() != this)
			{
				boolean flag = iblockstate.isFullBlock();
				boolean flag1 = iblockstate1.isFullBlock();

				if (iblockstate2.getBlock() == this || iblockstate3.getBlock() == this)
				{
					BlockPos blockpos1 = iblockstate2.getBlock() == this ? pos.west() : pos.east();
					IBlockState iblockstate7 = worldIn.getBlockState(blockpos1.north());
					IBlockState iblockstate6 = worldIn.getBlockState(blockpos1.south());
					enumfacing = EnumFacing.SOUTH;
					EnumFacing enumfacing2;

					if (iblockstate2.getBlock() == this)
					{
						enumfacing2 = (EnumFacing)iblockstate2.getValue(FACING);
					}
					else
					{
						enumfacing2 = (EnumFacing)iblockstate3.getValue(FACING);
					}

					if (enumfacing2 == EnumFacing.NORTH)
					{
						enumfacing = EnumFacing.NORTH;
					}

					if ((flag || iblockstate7.isFullBlock()) && !flag1 && !iblockstate6.isFullBlock())
					{
						enumfacing = EnumFacing.SOUTH;
					}

					if ((flag1 || iblockstate6.isFullBlock()) && !flag && !iblockstate7.isFullBlock())
					{
						enumfacing = EnumFacing.NORTH;
					}
				}
			}
			else
			{
				BlockPos blockpos = iblockstate.getBlock() == this ? pos.north() : pos.south();
				IBlockState iblockstate4 = worldIn.getBlockState(blockpos.west());
				IBlockState iblockstate5 = worldIn.getBlockState(blockpos.east());
				enumfacing = EnumFacing.EAST;
				EnumFacing enumfacing1;

				if (iblockstate.getBlock() == this)
				{
					enumfacing1 = (EnumFacing)iblockstate.getValue(FACING);
				}
				else
				{
					enumfacing1 = (EnumFacing)iblockstate1.getValue(FACING);
				}

				if (enumfacing1 == EnumFacing.WEST)
				{
					enumfacing = EnumFacing.WEST;
				}

				if ((iblockstate2.isFullBlock() || iblockstate4.isFullBlock()) && !iblockstate3.isFullBlock() && !iblockstate5.isFullBlock())
				{
					enumfacing = EnumFacing.EAST;
				}

				if ((iblockstate3.isFullBlock() || iblockstate5.isFullBlock()) && !iblockstate2.isFullBlock() && !iblockstate4.isFullBlock())
				{
					enumfacing = EnumFacing.WEST;
				}
			}

			state = state.withProperty(FACING, enumfacing);
			worldIn.setBlockState(pos, state, 3);
			return state;
		}
	}

	public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state)
	{
		EnumFacing enumfacing = null;

		for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
		{
			IBlockState iblockstate = worldIn.getBlockState(pos.offset(enumfacing1));

			if (iblockstate.getBlock() == this)
			{
				return state;
			}

			if (iblockstate.isFullBlock())
			{
				if (enumfacing != null)
				{
					enumfacing = null;
					break;
				}

				enumfacing = enumfacing1;
			}
		}

		if (enumfacing != null)
		{
			return state.withProperty(FACING, enumfacing.getOpposite());
		}
		else
		{
			EnumFacing enumfacing2 = (EnumFacing)state.getValue(FACING);

			if (worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock())
			{
				enumfacing2 = enumfacing2.getOpposite();
			}

			if (worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock())
			{
				enumfacing2 = enumfacing2.rotateY();
			}

			if (worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock())
			{
				enumfacing2 = enumfacing2.getOpposite();
			}

			return state.withProperty(FACING, enumfacing2);
		}
	}


	/**
	 * Checks if this block can be placed exactly at the given position.
	 */
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		for (int b = 2; b < EnumFacing.HORIZONTALS.length; b++)
        {
			EnumFacing dir = EnumFacing.HORIZONTALS[b];
            int x = dir.getFrontOffsetX() + pos.getX();
            int y = dir.getFrontOffsetY() + pos.getY();
            int z = dir.getFrontOffsetZ() + pos.getZ();
            TileEntity tile = worldIn.getTileEntity(new BlockPos(x,y,z));
            if (worldIn.getBlockState(new BlockPos(dir.getFrontOffsetX() + x, dir.getFrontOffsetY() + y, dir.getFrontOffsetZ() + z)).getBlock() == this && tile instanceof TileEntityColoredChest)
            {
                Color color = ((TileEntityColoredChest) tile).color;
                if (isMatchingChest(worldIn, x, y, z - 1, color) || isMatchingChest(worldIn, x, y, z + 1, color) || isMatchingChest(worldIn, x - 1, y, z, color) || isMatchingChest(worldIn, x + 1, y, z, color))
                {
                    return false;
                }
            }
        }
return true;
	}

	private boolean isDoubleChest(World worldIn, BlockPos pos)
	{
		if (worldIn.getBlockState(pos).getBlock() != this)
		{
			return false;
		}
		else
		{
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
			{
				if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() == this)
				{
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
	 * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
	 * block, etc.
	 */
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityColoredChest)
		{
			tileentity.updateContainingBlockInfo();
		}
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
		{
			return true;
		}
		else
		{
			ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);

			if (ilockablecontainer != null)
			{
				playerIn.displayGUIChest(ilockablecontainer);
				playerIn.addStat(StatList.CHEST_OPENED);
			}

			return true;
		}
	}

	@Nullable
	public ILockableContainer getLockableContainer(World worldIn, BlockPos pos)
	{
		return this.getContainer(worldIn, pos, false);
	}

	@Nullable
	public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (!(tileentity instanceof TileEntityColoredChest))
		{
			return null;
		}
		else
		{
			ILockableContainer ilockablecontainer = (TileEntityColoredChest)tileentity;

			if (!allowBlocking && this.isBlocked(worldIn, pos))
			{
				return null;
			}
			else
			{
				for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
				{
					BlockPos blockpos = pos.offset(enumfacing);
					Block block = worldIn.getBlockState(blockpos).getBlock();

					if (block == this)
					{
						if (!allowBlocking && this.isBlocked(worldIn, blockpos)) // Forge: fix MC-99321
						{
							return null;
						}

						TileEntity tileentity1 = worldIn.getTileEntity(blockpos);

						if (tileentity1 instanceof TileEntityColoredChest)
						{
							if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH)
							{
								ilockablecontainer = new InventoryLargeChest("container.chestDouble", ilockablecontainer, (TileEntityColoredChest)tileentity1);
							}
							else
							{
								ilockablecontainer = new InventoryLargeChest("container.chestDouble", (TileEntityColoredChest)tileentity1, ilockablecontainer);
							}
						}
					}
				}

				return ilockablecontainer;
			}
		}
	}

	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		if (!blockState.canProvidePower())
		{
			return 0;
		}
		else
		{
			int i = 0;
			TileEntity tileentity = blockAccess.getTileEntity(pos);

			if (tileentity instanceof TileEntityColoredChest)
			{
				i = ((TileEntityColoredChest)tileentity).numPlayersUsing;
			}

			return MathHelper.clamp(i, 0, 15);
		}
	}

	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return side == EnumFacing.UP ? blockState.getWeakPower(blockAccess, pos, side) : 0;
	}

	private boolean isBlocked(World worldIn, BlockPos pos)
	{
		return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
	}

	private boolean isBelowSolidBlock(World worldIn, BlockPos pos)
	{
		return worldIn.getBlockState(pos.up()).doesSideBlockChestOpening(worldIn, pos.up(), EnumFacing.DOWN);
	}

	private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos)
	{
		for (Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))))
		{
			EntityOcelot entityocelot = (EntityOcelot)entity;

			if (entityocelot.isSitting())
			{
				return true;
			}
		}

		return false;
	}

	Color colorTempCache;

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntityColoredChest tile = (TileEntityColoredChest) world.getTileEntity(pos);

		if (tile != null)
		{

			if (tile instanceof IInventory)
			{
				InventoryHelper.dropInventoryItems(world, pos, (IInventory)tile);
				world.updateComparatorOutputLevel(pos, this);
			}

			colorTempCache = tile.color;
		}

		super.breakBlock(world, pos, state);
	}



	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(new ItemStack(this, 1, this.getMetaFromState(state)));
		if (colorTempCache != null)
		{
			ret.get(0).setTagCompound(new NBTTagCompound());
			ret.get(0).getTagCompound().setInteger("rgb", colorTempCache.getRGB());
		}
		return ret;
	}


	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		ItemStack stack = new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)));
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityColoredChest && ((TileEntityColoredChest) tile).color != null)
		{
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("rgb", ((TileEntityColoredChest) tile).color.getRGB());
		}
		return stack;
	}

	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	/**
	 * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
	 * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
	 * <p>
	 * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
	 * does not fit the other descriptions and will generally cause other things not to connect to the face.
	 * 
	 * @return an approximation of the form of the given face
	 */
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}

	public static enum Type
	{
		BASIC,
		TRAP;
	}

	/* ======================================== FORGE START =====================================*/
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
	{
		return !isDoubleChest(world, pos) && super.rotateBlock(world, pos, axis);
	}


	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityColoredChest();
	}

}
