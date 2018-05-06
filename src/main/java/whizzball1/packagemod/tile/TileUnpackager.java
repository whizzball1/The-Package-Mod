package whizzball1.packagemod.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.items.ItemPackage;
import whizzball1.packagemod.network.PackageChangedMessage;
import whizzball1.packagemod.network.PackageModPacketHandler;

import javax.annotation.Nonnull;

public class TileUnpackager extends TileEntity implements ITickable {
    /**
     * This block has a GUI with a button to dump the package and a display of the package.
     */

    private final ItemStackHandler handler;

    public boolean hasPackage = false;
    protected int ticks;
    public ItemStack currentPackage = ItemStack.EMPTY;

    public TileUnpackager() {
        this.handler = new UnpackagerItemHandler(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", this.handler.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("inventory")) {
            this.handler.deserializeNBT(compound.getCompoundTag("inventory"));
        }
    }

    public void update() {
        if (!(world.isRemote)) {
            ticks++;
            if (ticks % 20 == 0) {
                ticks = 0;
                PackageModPacketHandler.INSTANCE.sendToAll(new PackageChangedMessage(handler.getStackInSlot(0), this));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void updatePackage(ItemStack stack) {
        currentPackage = stack;
        if (currentPackage.isEmpty()) hasPackage = false;
        else hasPackage = true;
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    public void dump() {
        ItemStack toDump = ((UnpackagerItemHandler) handler).dump();
        EntityItem i = new EntityItem(world, pos.getX(), pos.getY() + 1.5D, pos.getZ(), toDump);
        i.setDefaultPickupDelay();
        world.spawnEntity(i);
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        return super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handler);
        return super.getCapability(cap, facing);
    }

    private static final class UnpackagerItemHandler extends ItemStackHandler {
        private final TileUnpackager tile;

        public UnpackagerItemHandler(TileUnpackager tile) {
            super(1);
            this.tile = tile;
        }

        public ItemStack dump() {
            if (this.stacks.get(0).isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack toReturn = this.stacks.get(0).copy();
                this.stacks.set(0, ItemStack.EMPTY);
                return toReturn;
            }
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;
            validateSlotIndex(slot);
            if (stack.getItem() instanceof ItemPackage)
                if (stack.getTagCompound() != null)
                    if (stack.getTagCompound().hasKey("Name") && stack.getTagCompound().hasKey("Amount")){
                if (this.stacks.get(slot).isEmpty()) {
                    if (stack.getCount() == 1) {
                        if (!(simulate)) {
                            this.stacks.set(slot, stack);
                        }

                        return ItemStack.EMPTY;
                    } else if (stack.getCount() > 1 ) {
                        ItemStack insertStack = stack.copy();
                        insertStack.setCount(1);
                        if (!(simulate)) {
                            this.stacks.set(slot, insertStack);
                        }
                        ItemStack itemStack = stack.copy();
                        int stackCount = itemStack.getCount();
                        stackCount--;
                        itemStack.setCount(stackCount);
                        return itemStack;
                    }
                    if (!(simulate)) {
                        tile.markDirty();
                        tile.hasPackage = true;
                    }
                }
            }
            return stack;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) return ItemStack.EMPTY;
            validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(0);
            ItemStack toExtract;
            if (!(existing.isEmpty())) if (existing.getTagCompound() != null)
                if (existing.getTagCompound().hasKey("Name") && existing.getTagCompound().hasKey("Amount"))
                {
                    toExtract = CraftingPackage.getPackageGivenName(existing.getTagCompound().getString("Name"))
                            .result.item.copy();
                    int count;
                    if (amount <= existing.getTagCompound().getInteger("Amount")) {
                        if (amount <= 64) {
                            count = amount;
                        } else {
                            count = 64;
                        }
                    } else {
                        if (existing.getTagCompound().getInteger("Amount") <= 64) {
                            count = existing.getTagCompound().getInteger("Amount");
                        } else count = 64;
                    }
                    toExtract.setCount(count);
                    if (!(simulate)) {
                        int oldAmount = existing.getTagCompound().getInteger("Amount");
                        oldAmount -= count;
                        if (oldAmount > 0) {
                            existing.getTagCompound().setInteger("Amount", oldAmount);
                        } else {
                            this.stacks.set(0, ItemStack.EMPTY);
                            tile.hasPackage = false;
                        }
                    }
                    return toExtract;
                }
            return ItemStack.EMPTY;
        }
    }
}
