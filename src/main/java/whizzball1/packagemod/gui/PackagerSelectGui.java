package whizzball1.packagemod.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.network.PackageModPacketHandler;
import whizzball1.packagemod.network.RecipeMessage;
import whizzball1.packagemod.tile.TilePackager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PackagerSelectGui extends SelectGui {

    TilePackager te;
    String recipeName;
    public List<ItemRequirement> requirementList;
    public ConcurrentHashMap<ItemRequirement.ReqKey, ItemRequirement> itemToRequirement = new ConcurrentHashMap<>();


    public PackagerSelectGui(TilePackager te, String cp) {
        this.te = te;
        recipeName = cp;
        requirementList = CraftingPackage.getPackageGivenName(cp).cloneList();
        for (ItemRequirement i : requirementList) {
            itemToRequirement.put(new ItemRequirement.ReqKey(i.item.getItem(), i.item.getMetadata()), i);
        }
        if (requirementList.size() < 4) {
            reqsOnPage = requirementList.size();
        } else reqsOnPage = 4;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiButton leftButton = new GuiFacButton(1, guiLeft + 15, guiTop + 115, 20, 20, "<-", 1);
        leftButton.enabled = false;
        GuiButton rightButton = new GuiFacButton(2, guiLeft + 145, guiTop + 115, 20, 20, "->", 1);
        if (requirementList.size() <= 4) rightButton.enabled = false;
        addButton(leftButton);
        addButton(rightButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, recipeName, guiLeft + 90, guiTop + 10, 0xC6C6C6);
        RenderHelper.enableGUIStandardItemLighting();
        drawCenteredString(fontRenderer, "Result: ", guiLeft + 215, guiTop + 55, 0xC6C6C6);
        ItemRequirement result = CraftingPackage.getPackageGivenName(recipeName).result;
        this.itemRender.renderItemAndEffectIntoGUI(result.item, guiLeft + 190, guiTop + 65);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, result.item, guiLeft + 190, guiTop + 65, null);
        drawString(fontRenderer, "* " + result.remainingRequirement, guiLeft + 210, guiTop + 70, 0xC6C6C6);
        if (isMouseOverSlot(guiLeft + 190, guiTop + 65, mouseX, mouseY)) {
            this.renderToolTip(result.item, mouseX, mouseY);
        }
        if (reqsOnPage > 0) {
            ItemRequirement requirement = requirementList.get(page * 4);
            ItemStack itemstack = requirement.item;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, guiLeft + 15, guiTop + 35);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, guiLeft + 15, guiTop + 35, null);
            drawString(fontRenderer, "* " + requirement.remainingRequirement, guiLeft + 35, guiTop + 40, 0xC6C6C6);
            if (isMouseOverSlot(guiLeft + 15, guiTop + 35, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
        }
        if (reqsOnPage > 1) {
            ItemRequirement requirement = requirementList.get(page * 4 + 1);
            ItemStack itemstack = requirement.item;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, guiLeft + 15, guiTop + 55);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, guiLeft + 15, guiTop + 55, null);
            drawString(fontRenderer, "* " + requirement.remainingRequirement, guiLeft + 35, guiTop + 60, 0xC6C6C6);
            if (isMouseOverSlot(guiLeft + 15, guiTop + 55, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
        }
        if (reqsOnPage > 2) {
            ItemRequirement requirement = requirementList.get(page * 4 + 2);
            ItemStack itemstack = requirement.item;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, guiLeft + 15, guiTop + 75);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, guiLeft + 15, guiTop + 75, null);
            drawString(fontRenderer, "* " + requirement.remainingRequirement, guiLeft + 35, guiTop + 80, 0xC6C6C6);
            if (isMouseOverSlot(guiLeft + 15, guiTop + 75, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
        }
        if (reqsOnPage > 3) {
            ItemRequirement requirement = requirementList.get(page * 4 + 3);
            ItemStack itemstack = requirement.item;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, guiLeft + 15, guiTop + 95);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, guiLeft + 15, guiTop + 95, null);
            drawString(fontRenderer, "* " + requirement.remainingRequirement, guiLeft + 35, guiTop + 100, 0xC6C6C6);
            if (isMouseOverSlot(guiLeft + 15, guiTop + 95, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == -1) {
            te.changeRecipe(recipeName);
            PackageModPacketHandler.INSTANCE.sendToServer(new RecipeMessage(recipeName, te.getPos()));
            FMLCommonHandler.instance().showGuiScreen(new PackagerGui(te));
        } else if (button.id == -2) {
            FMLCommonHandler.instance().showGuiScreen(new PackagerGui.PackageSelectGui(te));
        } else if (button.id == 1) {
            page--;
            buttonList.clear();
            addButton(new GuiFacButton(-1, guiLeft + 190, guiTop + 5, 50, 20, "Select", 0));
            addButton(new GuiFacButton(-1, guiLeft + 190, guiTop + 30, 50, 20, "Back", 0));
            int pageSize = requirementList.size() - (page * 4);
            if (pageSize < 4) {
                reqsOnPage = pageSize;
            } else reqsOnPage = 4;
            GuiButton leftButton = new GuiFacButton(1, guiLeft + 15, guiTop + 115, 20, 20, "<-", 1);
            GuiButton rightButton = new GuiFacButton(2, guiLeft + 145, guiTop + 115, 20, 20, "->", 1);
            if (page == 0) leftButton.enabled = false;
            addButton(leftButton);
            addButton(rightButton);
        } else if (button.id == 2) {
            page++;
            buttonList.clear();
            addButton(new GuiFacButton(-1, guiLeft + 190, guiTop + 5, 50, 20, "Select", 0));
            addButton(new GuiFacButton(-1, guiLeft + 190, guiTop + 30, 50, 20, "Back", 0));
            int pageSize = requirementList.size() - (page * 4);
            if (pageSize < 4) {
                reqsOnPage = pageSize;
            } else reqsOnPage = 4;
            GuiButton leftButton = new GuiFacButton(1, guiLeft + 15, guiTop + 115, 20, 20, "<-", 1);
            GuiButton rightButton = new GuiFacButton(2, guiLeft + 145, guiTop + 115, 20, 20, "->", 1);
            if (pageSize <= 4) rightButton.enabled = false;
            addButton(leftButton);
            addButton(rightButton);
        }
    }




}
