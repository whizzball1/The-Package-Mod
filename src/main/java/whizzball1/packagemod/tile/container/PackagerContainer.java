package whizzball1.packagemod.tile.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import whizzball1.packagemod.tile.TilePackager;

public class PackagerContainer extends Container {
    private TilePackager te;
    private int numberOfItemRequirements;
    public int recipeId;
    public String recipeName;

    public PackagerContainer(IInventory playerInventory, TilePackager te, EntityPlayer player) {
        this.te = te;
        this.recipeName = te.recipeName;
        this.recipeId = te.recipeId;
        addOwnSlots();
        addPlayerSlots(playerInventory);
    }

    private void addOwnSlots() {
        int x = 9;
        int y = 6;
        IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new SlotItemHandler(itemHandler, 1, 9, 42));
    }

    private void addPlayerSlots(IInventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 9 + col * 18;
                int y = row * 18 + 70;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 9 + row * 18;
            int y = 58 + 70;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    /**
     * Here I must send a packet for every recipe requirement. The number of recipe requirements is variable and so is
     * the data of each recipe requirement. I will ensure that the recipe data is the same between the container and
     * the tile entity.
     * Variables: 1 is recipeId.
     */
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icontainerlistener = this.listeners.get(i);

            if (this.recipeId != te.recipeId) {
                icontainerlistener.sendWindowProperty(this, 1, te.recipeId);
            }


        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {

        //this.te.setField(id, data);
    }



    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.canInteractWith(playerIn);
    }
}
