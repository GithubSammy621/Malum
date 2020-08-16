package com.kittykitcatcat.malum.blocks.machines.mirror;

import com.kittykitcatcat.malum.MalumHelper;
import com.kittykitcatcat.malum.blocks.utility.BasicTileEntity;
import com.kittykitcatcat.malum.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static net.minecraft.state.properties.AttachFace.CEILING;
import static net.minecraft.state.properties.AttachFace.FLOOR;
import static net.minecraft.state.properties.BlockStateProperties.*;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class BasicMirrorTileEntity extends BasicTileEntity
{
    public BasicMirrorTileEntity(TileEntityType type)
    {
        super(type);
    }
    
    public static boolean transferItem(TileEntity itemSender, TileEntity itemReceiver, int transferAmount)
    {
        IItemHandler senderInventory = itemSender.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        for (int i = 0; i < senderInventory.getSlots(); i++)
        {
            ItemStack stack = senderInventory.getStackInSlot(i);
            for (int j = 0; j < transferAmount; j++)
            {
                boolean success = MalumHelper.inputStackIntoTE(itemReceiver, stack.split(1));
                if (success)
                {
                    itemSender.getWorld().notifyBlockUpdate(itemSender.getPos(), itemSender.getBlockState(), itemSender.getBlockState(), 3);
                    itemReceiver.getWorld().notifyBlockUpdate(itemReceiver.getPos(), itemReceiver.getBlockState(), itemReceiver.getBlockState(), 3);
                }
                else
                {
                    stack.grow(1);
                    return true;
                }
            }
        }
        return false;
    }
    public static TileEntity getAttachedTileEntity(World world, BlockPos pos)
    {
        Direction direction = world.getBlockState(pos).get(HORIZONTAL_FACING);
        Vec3i directionVector = direction.getDirectionVec();
        if (world.getBlockState(pos).get(FACE).equals(CEILING))
        {
            directionVector = new Vec3i(0, -1, 0);
        }
        if (world.getBlockState(pos).get(FACE).equals(FLOOR))
        {
            directionVector = new Vec3i(0, 1, 0);
        }
        if (!(world.getTileEntity(pos.subtract(directionVector)) instanceof BasicMirrorTileEntity))
        {
            return world.getTileEntity(pos.subtract(directionVector));
        }
        return null;
    }
    int[] transferAmounts = new int[]{1, 2, 4, 8, 16, 32, 64};
    public int transferAmount;
    public int transferCooldown;
    public ItemStackHandler inventory = new ItemStackHandler(1)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            return 64;
        }
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return true;
        }
        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return ItemStack.EMPTY;
        }
        @Override
        protected void onContentsChanged(int slot)
        {
            BasicMirrorTileEntity.this.markDirty();
            if (!world.isRemote)
            {
                updateContainingBlockInfo();
                BlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    };
    public final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> inventory);
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return lazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.putInt("transferAmount", transferAmount);
        compound.put("inventory", inventory.serializeNBT());
        return compound;
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        transferAmount = compound.getInt("transferAmount");
        inventory.deserializeNBT((CompoundNBT) Objects.requireNonNull(compound.get("inventory")));
    }
    
}