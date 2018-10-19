package whizzball1.packagemod.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.network.PackageModPacketHandler;
import whizzball1.packagemod.network.RecipeMessage;
import whizzball1.packagemod.tile.TileResearcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ResearcherSelectGui extends SelectGui {
    TileResearcher te;
    String recipeName;
    public int packsOnFirstPage;
    public List<ItemRequirement> requirementList = new ArrayList<>();
    public ConcurrentHashMap<ItemRequirement.ReqKey, ItemRequirement> itemToRequirement = new ConcurrentHashMap<>();
    public List<CraftingPackage.PackStack> packList = new ArrayList<>();

    public ResearcherSelectGui(String cp, TileResearcher te) {
        this.te = te;
        recipeName = cp;
        requirementList = CraftingPackage.getPackageGivenName(cp).research.cloneItemList();
        packList = CraftingPackage.getPackageGivenName(cp).research.clonePackList();
        for (ItemRequirement i : requirementList) {
            itemToRequirement.put(new ItemRequirement.ReqKey(i.item.getItem(), i.item.getMetadata()), i);
        }
        if (requirementList.size() < 4) {
            reqsOnPage = requirementList.size();
        } else reqsOnPage = 4;
        packsOnFirstPage = 4 - requirementList.size() % 4;
        if (packList.size() < packsOnFirstPage) packsOnFirstPage = packList.size();
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiButton leftButton = new GuiFacButton(1, guiLeft + 15, guiTop + 115, 20, 20, "<-", 1);
        leftButton.enabled = false;
        GuiButton rightButton = new GuiFacButton(2, guiLeft + 145, guiTop + 115, 20, 20, "->", 1);
        if (requirementList.size() + packList.size() <= 4) rightButton.enabled = false;
        addButton(leftButton);
        addButton(rightButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, recipeName, guiLeft + 90, guiTop + 10, 0xC6C6C6);
        RenderHelper.enableGUIStandardItemLighting();
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
        if (packList.size() > 0) {
            if (page == getPackPage()) {
                for (int i = 0; i < packsOnFirstPage; i++) {
                    CraftingPackage.PackStack p = packList.get(i);
                    drawString(fontRenderer, p.packName + " * " + Integer.toString(p.packNumber), guiLeft + 15, guiTop + 20 + (requirementList.size() % 4 + 1 + i) * 20, 0xC6C6C6);
                }
            } else if (page > getPackPage()) {
                int toIterate;
                int beginId = packsOnFirstPage + (page - getPackPage() - 1) * 4;
                if (packList.size() - beginId > 4) {
                    toIterate = 4;
                } else toIterate = packList.size() - beginId;
                for (int i = beginId; i < beginId + toIterate; i++) {
                    CraftingPackage.PackStack p = packList.get(i);
                    drawString(fontRenderer, p.packName + " * " + Integer.toString(p.packNumber), guiLeft + 15, guiTop + 20 + (i - beginId) * 20, 0xC6C6C6);
                }
            }
        }
    }

    public int getPackPage() {
        return te.requirementList.size() / 4;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == -1) {
            te.changeRecipe(recipeName);
            PackageModPacketHandler.INSTANCE.sendToServer(new RecipeMessage(recipeName, te.getPos()));
            FMLCommonHandler.instance().showGuiScreen(new ResearcherGui(te));
        } else if (button.id == -2) {
            FMLCommonHandler.instance().showGuiScreen(new ResearcherGui.ResearchSelectGui(te));
        } else if (button.id == 1) {
            page--;
            buttonList.clear();
            addButton(new GuiFacButton(-1, guiLeft + 190, guiTop + 5, 50, 20, "Select", 0));
            addButton(new GuiFacButton(-1, guiLeft + 190, guiTop + 30, 50, 20, "Back", 0));
            int pageSize = requirementList.size() - (page * 4);
            if (pageSize > 4) {
                reqsOnPage = 4;
            } else if (pageSize > 0) {
                reqsOnPage = pageSize;
            } else reqsOnPage = 0;
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
            if (pageSize > 4) {
                reqsOnPage = 4;
            } else if (pageSize > 0) {
                reqsOnPage = pageSize;
            } else reqsOnPage = 0;
            GuiButton leftButton = new GuiFacButton(1, guiLeft + 15, guiTop + 115, 20, 20, "<-", 1);
            GuiButton rightButton = new GuiFacButton(2, guiLeft + 145, guiTop + 115, 20, 20, "->", 1);
            if (te.requirementList.size() + te.packList.size() <= page * 4) {
                rightButton.enabled = false;
            }
            addButton(leftButton);
            addButton(rightButton);
        }
    }

}
