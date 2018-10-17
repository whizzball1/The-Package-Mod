package whizzball1.packagemod.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.data.PlayerData;
import whizzball1.packagemod.data.WorldData;
import whizzball1.packagemod.network.*;
import whizzball1.packagemod.packagemod;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class TilePackager extends TileEntity implements ITickable {

    private final ItemStackHandler handler;
    public int numberOfItemRequirements;
    public int recipeId;
    protected int ticks;
    public String recipeName;
    public boolean recipeComplete = false;
    public List<ItemRequirement> requirementList = new ArrayList<>();
    public ConcurrentHashMap<ItemRequirement.ReqKey, ItemRequirement> itemToRequirement = new ConcurrentHashMap<>();
    public UUID owner;
    public PlayerData.PlayerSave ps;

    public TilePackager() {
        handler = new PackagerItemHandler(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        //packagemod.logger.info(compound.toString());
        super.readFromNBT(compound);
        String tempRecipeName = compound.getString("recipeName");
        if (!(tempRecipeName.equals(""))) {
            if (CraftingPackage.getPackageGivenName(tempRecipeName) != null) {
                this.changeRecipe(compound.getString("recipeName"));
                NBTTagCompound reqList = compound.getCompoundTag("requirementList");
                //packagemod.logger.info(reqList.toString());
                for (int i = 0; i < reqList.getSize(); i++) {
                    NBTTagCompound currentReq = reqList.getCompoundTag(Integer.toString(i));
                    ItemStack itemStack = new ItemStack(currentReq);
                    ItemRequirement.ReqKey itemKey = new ItemRequirement.ReqKey(itemStack.getItem(), itemStack.getMetadata());
                    int remaining = currentReq.getInteger("remaining");
                    if (CraftingPackage.hasRequirement(CraftingPackage.getPackageGivenName(recipeName), itemStack, remaining, false)) {
                        itemToRequirement.get(itemKey).remainingRequirement = remaining;
                    }
                }
                if (compound.getBoolean("recipeComplete") == true) recipeComplete = true;
            }
        }
        if (compound.hasUniqueId("UUID")) {
            setOwner(compound.getUniqueId("UUID"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        //packagemod.logger.info("packager write occuring");
        super.writeToNBT(compound);
        if (recipeName != null) {
            compound.setString("recipeName", recipeName);
            compound.setBoolean("recipeComplete", recipeComplete);
            NBTTagCompound requirementCompound = new NBTTagCompound();
            for (int i = 0; i < requirementList.size(); i++) {
                ItemRequirement req = requirementList.get(i);
                NBTTagCompound reqCompound = new NBTTagCompound();
                req.serialise(reqCompound);
                requirementCompound.setTag(Integer.toString(i), reqCompound);
            }
            compound.setTag("requirementList", requirementCompound);
        }
        if (owner != null) {
            compound.setUniqueId("UUID", owner);
        }
        //packagemod.logger.info(compound.toString());
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        //packagemod.logger.info("Getting update tag for client");
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    //one day, check for changes and only send item requirements that have changed
    public void update() {
        if (!(world.isRemote)) {
            ticks++;
            if (ticks % 20 == 0) {
                for (ItemRequirement i : requirementList) {
                    PackageModPacketHandler.INSTANCE.sendToAll(new ReqChangedMessage(i, this.getPos()));
                }
            }
            if (ticks % 100 == 0) {
                NBTTagCompound compound = new NBTTagCompound();
                if (owner != null) {
                    PlayerData.PlayerSave tempPs = PlayerData.getDataFromPlayer(world, owner);
                    tempPs.writeResearchedToNBT(compound);
                    PackageModPacketHandler.INSTANCE.sendToAll(new SendPlayerSaveMessage(compound, this.getPos(), 1));
                } // else packagemod.logger.info("oops the ps is null on the server why idk");
                ticks = 0;
            }
        }
    }

    public void changeRecipe(String cp) {
        //packagemod.logger.info("Received " + cp + ", changing recipe");
        recipeName = cp;
        //packagemod.logger.info(recipeName);
        requirementList = CraftingPackage.getPackageGivenName(cp).cloneList();
        //packagemod.logger.info(requirementList.size());
        for (ItemRequirement i : requirementList) {
            itemToRequirement.put(new ItemRequirement.ReqKey(i.item.getItem(), i.item.getMetadata()), i);
        }
        this.recipeComplete = false;
        this.markDirty();
    }

    public void updateRecipe() {
        boolean isItComplete = true;
        for (ItemRequirement i : requirementList) {
            if (i.remainingRequirement != 0) {
                isItComplete = false;
                break;
            }
        }
        if (isItComplete) {
            this.recipeComplete = true;
            //packagemod.logger.info("it's complete!");
            setMade(this.recipeName);
        }
    }

    public boolean isRequirement(ItemStack stack) {
        if (itemToRequirement.containsKey(new ItemRequirement.ReqKey(stack))) {
            return true;
        }
        return false;
    }

    //Only call if isRequirement is true!
    public int subtractRequirement(ItemStack stack, boolean simulate) {
        //packagemod.logger.info("trying to subtract requirement");
        ItemRequirement req = itemToRequirement.get(new ItemRequirement.ReqKey(stack));
        //packagemod.logger.info(req.item.getUnlocalizedName());
        int stackAmt = stack.getCount();
        int toReturn;
        if (req.remainingRequirement <= stackAmt) {
            toReturn = stackAmt - req.remainingRequirement;
            if (!(simulate)) {
                req.remainingRequirement = 0;
                updateRecipe();
            }
        } else if (req.remainingRequirement > stackAmt) {
            toReturn = 0;
            if (!(simulate)) req.remainingRequirement -= stackAmt;
        } else toReturn = stackAmt;
        //packagemod.logger.info(Integer.toString(req.remainingRequirement));
        return toReturn;
    }

    @SideOnly(Side.CLIENT)
    public void updateRequirement(ItemStack stack, int remaining) {
        ItemRequirement.ReqKey key = new ItemRequirement.ReqKey(stack);
        itemToRequirement.get(key).remainingRequirement = remaining;
        this.markDirty();
    }

    public void setOwner(UUID id) {
        if (this.owner == null) {this.owner = id;}
        if (world != null) {
            if (world.isRemote) {
                if (this.ps == null)
                    PackageModPacketHandler.INSTANCE.sendToServer(new RequestPlayerSaveMessage(this.getPos(), id));
            }
        }
        this.markDirty();
        //packagemod.logger.info(owner.toString());
    }

    public void setOwner(NBTTagCompound data) {
        PlayerData.PlayerSave ps = new PlayerData.PlayerSave(this.owner);
        ps.readFromNBT(data);
        this.ps = ps;
        this.markDirty();
    }

    public void setMade(String name) {
        //packagemod.logger.info("setting made");
        if (world != null) if (!(world.isRemote)) {
            PlayerData.PlayerSave data = PlayerData.getDataFromPlayer(world, owner);
            int madeInt = data.packagesMade.get(name);
            madeInt++;
            data.packagesMade.put(name, madeInt);
            WorldData.get(world, false).markDirty();
        }
    }

    @SideOnly(Side.CLIENT)
    public void receiveMadeData(NBTTagCompound data) {
        if (ps == null) {
            ps = new PlayerData.PlayerSave(owner);
        }
        //packagemod.logger.info("receiving Made Data");
        ps.readResearchedFromNBT(data);
    }

    public boolean hasResearch(String s) {
        //packagemod.logger.info("does it have prereqs?");
        //packagemod.logger.info(s);
        if (!(ps.packagesResearched.contains(s))) return false;
        return true;
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

    private static final class PackagerItemHandler extends ItemStackHandler {
        private final TilePackager tile;

        public PackagerItemHandler(TilePackager tile) {
            super(2);
            this.tile = tile;
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            //packagemod.logger.info("trying to insert item");
            if (stack.isEmpty()) return ItemStack.EMPTY;
            validateSlotIndex(slot);
            if (!(tile.isRequirement(stack))) return stack;
            int newCount = tile.subtractRequirement(stack, simulate);
            //packagemod.logger.info(stack.getCount());
            return new ItemStack(stack.getItem(), newCount, stack.getMetadata());
        }

        //during extractItem, check if recipeComplete and extract the package with the proper NBT data
        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) return ItemStack.EMPTY;
            validateSlotIndex(slot);
            if (tile.recipeComplete == true) {
                if (!(simulate)) {
                    tile.recipeComplete = false;
                    tile.changeRecipe(tile.recipeName);
                }
                ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("packagemod:packageitem")), 1, 0);
                result.setTagCompound(CraftingPackage.createPackage(tile.recipeName));
                return result;
            } else return ItemStack.EMPTY;
        }
    }
}
