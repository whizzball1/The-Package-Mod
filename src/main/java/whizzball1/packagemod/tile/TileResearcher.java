package whizzball1.packagemod.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.data.PlayerData;
import whizzball1.packagemod.data.WorldData;
import whizzball1.packagemod.network.PackageModPacketHandler;
import whizzball1.packagemod.network.ReqChangedMessage;
import whizzball1.packagemod.network.RequestPlayerSaveMessage;
import whizzball1.packagemod.network.SendPlayerSaveMessage;
import whizzball1.packagemod.packagemod;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TileResearcher extends TileEntity implements ITickable {
    //EXTREMELY COMPLICATED AAAAAAAAA
    //Let's start by making the inventory and GUI I guess.

    private ItemStackHandler handler;
    public String recipeName;
    public List<CraftingPackage.PackStack> packList = new ArrayList<>();
    public List<ItemRequirement> requirementList = new ArrayList<>();
    public ConcurrentHashMap<ItemRequirement.ReqKey, ItemRequirement> itemToRequirement = new ConcurrentHashMap<>();
    public UUID owner;
    public PlayerData.PlayerSave ps;
    private int ticks = 0;
    private boolean isTherePS = true;
    private boolean isItComplete = false;

    public TileResearcher() {
        handler = new ResearcherItemHandler(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        //packagemod.logger.info("reading from NBT");
        //packagemod.logger.info(compound.toString());
        super.readFromNBT(compound);
        this.owner = compound.getUniqueId("UUID");
        setOwner(owner);
        String tempRecipeName = compound.getString(("recipeName"));
        if (!(tempRecipeName.equals(""))) {
            if (CraftingPackage.getPackageGivenName(tempRecipeName) != null) if (compound.getBoolean("recipeComplete") == false){
                this.changeRecipe(compound.getString("recipeName"));
                NBTTagCompound reqList = compound.getCompoundTag("requirementList");
                //packagemod.logger.info(reqList.toString());
                for (int i = 0; i < reqList.getSize(); i++) {
                    NBTTagCompound currentReq = reqList.getCompoundTag(Integer.toString(i));
                    ItemStack itemStack = new ItemStack(currentReq);
                    ItemRequirement.ReqKey itemKey = new ItemRequirement.ReqKey(itemStack.getItem(), itemStack.getMetadata());
                    int remaining = currentReq.getInteger("remaining");
                    if (CraftingPackage.hasRequirement(CraftingPackage.getPackageGivenName(recipeName), itemStack, remaining, true)) {
                        itemToRequirement.get(itemKey).remainingRequirement = remaining;
                    }
                }
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
            compound.setBoolean("recipeComplete", isItComplete);
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

    public void update() {

        if (world != null) if (!(world.isRemote)) {
            ticks++;
            if (ticks % 20 == 0) {
                for (ItemRequirement i : requirementList) {
                    PackageModPacketHandler.INSTANCE.sendToAll(new ReqChangedMessage(i, this.getPos()));
                }
            }
            if (ticks % 40 == 0) {
                ticks = 0;
                NBTTagCompound compound = new NBTTagCompound();
                if (owner != null) {
                    PlayerData.PlayerSave tempPs = PlayerData.getDataFromPlayer(world, owner);
                    tempPs.writeMadeToNBT(compound);
                    tempPs.writeResearchedToNBT(compound);
                    PackageModPacketHandler.INSTANCE.sendToAll(new SendPlayerSaveMessage(compound, this.getPos(), 1));
                } else packagemod.logger.info("oops the ps is null on the server why idk");
                updateRecipe();
            }
        }
    }

    public void updateRecipe() {
        boolean reqsComplete = true;
        for (ItemRequirement i : requirementList) {
            if (i.remainingRequirement != 0) {
                reqsComplete = false;
                break;
            }
        }
        boolean packsComplete = true;
        for (CraftingPackage.PackStack p : packList) {
            if (PlayerData.getDataFromPlayer(world, owner).packagesMade.get(p.packName) < p.packNumber) {
                packsComplete = false;
            }
        }
        if (reqsComplete && packsComplete && !(isItComplete)) {
            setResearched(this.recipeName);
            isItComplete = true;
        }
    }

    public void setResearched(String name) {
        if (world != null) if (!(world.isRemote)) {
            //packagemod.logger.info(world.provider.getDimension());
            PlayerData.PlayerSave data = PlayerData.getDataFromPlayer(world, owner);
            if (!(data.packagesResearched.contains(name))) data.packagesResearched.add(name);
            //packagemod.logger.info(data.packagesResearched.toString());
            WorldData.get(world, false).markDirty();
        }
    }

    public void changeRecipe(String cp) {
        recipeName = cp;
        requirementList = CraftingPackage.getPackageGivenName(cp).research.cloneItemList();
        packList = CraftingPackage.getPackageGivenName(cp).research.clonePackList();
        for (ItemRequirement i : requirementList) {
            itemToRequirement.put(new ItemRequirement.ReqKey(i.item.getItem(), i.item.getMetadata()), i);
        }
        this.isItComplete = false;
        this.markDirty();
    }

    public boolean isRequirement(ItemStack stack) {
        if (itemToRequirement.containsKey(new ItemRequirement.ReqKey(stack))) {
            return true;
        }
        return false;
    }

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
                this.markDirty();
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

    public boolean hasPrereqs(String s) {
        //packagemod.logger.info("does it have prereqs?");
        //packagemod.logger.info(s);
        CraftingPackage cp = CraftingPackage.getPackageGivenName(s);
        CraftingPackage.PackageResearch pr = cp.research;
        //packagemod.logger.info(pr.preReqs.toString());
        boolean hasPrereqs = true;
        for (String toCheck : pr.preReqs) {
            //packagemod.logger.info(ps.packagesResearched);
            if (!(ps.packagesResearched.contains(toCheck))) {
                hasPrereqs = false;
            }
        }
        return hasPrereqs;
    }

    public void setOwner(UUID id) {
        if (this.owner == null) this.owner = id;
        if (world != null) {
            if (world.isRemote) {
                if (this.ps == null)
                    PackageModPacketHandler.INSTANCE.sendToServer(new RequestPlayerSaveMessage(this.getPos(), id));
            }
        }
        this.markDirty();
    }

    public void setOwner(NBTTagCompound data) {
        PlayerData.PlayerSave ps = new PlayerData.PlayerSave(this.owner);
        ps.readFromNBT(data);
        this.ps = ps;
        this.markDirty();
    }

    @SideOnly(Side.CLIENT)
    public void receiveMadeData(NBTTagCompound data) {
        if (ps == null) {
            ps = new PlayerData.PlayerSave(owner);
        }
        //packagemod.logger.info("receiving Made Data");
        ps.readMadeFromNBT(data);
        ps.readResearchedFromNBT(data);
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handler);
        }
        return super.getCapability(capability, facing);
    }

    private static final class ResearcherItemHandler extends ItemStackHandler {
        private final TileResearcher tile;

        public ResearcherItemHandler(TileResearcher tile) {
            super(1);
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
    }


}
