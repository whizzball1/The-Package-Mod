package whizzball1.packagemod.core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class ItemRequirement implements Comparable<ItemRequirement> {

    protected int totalRequirement;
    public ItemStack item;
    public int remainingRequirement;

    public ItemRequirement(String itemID, int meta, int requirement) {
        ResourceLocation itemResourceLocation = new ResourceLocation(itemID);
        Item tempItem = ForgeRegistries.ITEMS.getValue(itemResourceLocation);
        item = new ItemStack(tempItem, 1, meta);
        totalRequirement = requirement;
        remainingRequirement = totalRequirement;
    }

    public ItemRequirement(ItemStack item, int requirement) {
        this.item = item;
        this.totalRequirement = requirement;
        this.remainingRequirement = totalRequirement;
    }

    public NBTTagCompound serialise(NBTTagCompound compound) {
        item.writeToNBT(compound);
        compound.setInteger("remaining", remainingRequirement);
        return compound;
    }

    public boolean subtractRequirement(int subtractAttempt) {
        remainingRequirement -= subtractAttempt;
        if (remainingRequirement == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean equals(Object isItEqual) {
        if (this == isItEqual) {
            return true;
        }
        else if (!(isItEqual instanceof ItemRequirement)) {
            return false;
        }
        else {
            ItemRequirement itemrequirement = (ItemRequirement) isItEqual;
            if (this.item == itemrequirement.item && this.totalRequirement == itemrequirement.totalRequirement) {
                return true;
            }
            else return false;
        }
    }

    public int compareTo(ItemRequirement compareIt) {
        if (!(compareIt.item == this.item)) {
            return 2;
        }
        else {
            if (this.totalRequirement < compareIt.totalRequirement) {
                return -1;
            }
            else if (this.totalRequirement > compareIt.totalRequirement) {
                return 1;
            }
            else return 0;
        }
    }

    public static class ReqKey {
        public final Item item;
        public final int meta;
        private int hashCode;

        public ReqKey(Item item, int meta) {
            this.item = item;
            this.meta = meta;
        }

        public ReqKey(ItemStack item) {
            this.item = item.getItem();
            this.meta = item.getMetadata();
        }

        public boolean equals(Object k) {
            if (k == this) return true;
            if (!(k instanceof ReqKey)) return false;
            ReqKey rk = (ReqKey) k;
            if (rk.item.getRegistryName().toString().equals(this.item.getRegistryName().toString()) && rk.meta == this.meta) return true;
            return false;
        }

        @Override
        public int hashCode() {
            hashCode = Objects.hash(item.getRegistryName(), meta);
            return hashCode;
        }
    }
}
