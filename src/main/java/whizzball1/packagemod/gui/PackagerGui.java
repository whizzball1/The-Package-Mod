package whizzball1.packagemod.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.network.PackageModPacketHandler;
import whizzball1.packagemod.network.RecipeMessage;
import whizzball1.packagemod.packagemod;
import whizzball1.packagemod.tile.TilePackager;
import whizzball1.packagemod.tile.container.PackagerContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PackagerGui extends GuiScreen {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;
    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;
    public int page;
    public int reqsOnPage;

    private static final ResourceLocation background = new ResourceLocation(packagemod.MODID, "textures/gui/180x152.png");
    private static final ResourceLocation textBackground = new ResourceLocation(packagemod.MODID, "textures/gui/160x112.png");
    private static final ResourceLocation slotTexture = new ResourceLocation(packagemod.MODID, "textures/gui/slot.png");

    protected TilePackager te;


    public PackagerGui(TilePackager te) {
        //addButton(new GuiButton(1, 30, 6, "test"));
        this.te = te;

        xSize = WIDTH;
        ySize = HEIGHT;
        page = 0;
        if (te.requirementList.size() < 4) {
            reqsOnPage = te.requirementList.size();
        } else reqsOnPage = 4;
        //packagemod.logger.info(reqsOnPage);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.buttonList.clear();
        addButton(new GuiFacButton(-1, guiLeft + 65, guiTop + 5, 50, 20, "Select", 0));
        GuiButton leftButton = new GuiFacButton(1, guiLeft + 15, guiTop + 115, 20, 20, "<-", 1);
        leftButton.enabled = false;
        GuiButton rightButton = new GuiFacButton(2, guiLeft + 145, guiTop + 115, 20, 20, "->", 1);
        if (te.requirementList.size() <= 4) rightButton.enabled = false;
        addButton(leftButton);
        addButton(rightButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        if (reqsOnPage > 0) {
            ItemRequirement requirement = te.requirementList.get(page * 4);
            ItemStack itemstack = requirement.item;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, guiLeft + 15, guiTop + 35);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, guiLeft + 15, guiTop + 35, null);
            drawString(fontRenderer, "* " + requirement.remainingRequirement, guiLeft + 35, guiTop + 40, 0xC6C6C6);
            if (isMouseOverSlot(guiLeft + 15, guiTop + 35, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
        }
        if (reqsOnPage > 1) {
            ItemRequirement requirement = te.requirementList.get(page * 4 + 1);
            ItemStack itemstack = requirement.item;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, guiLeft + 15, guiTop + 55);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, guiLeft + 15, guiTop + 55, null);
            drawString(fontRenderer, "* " + requirement.remainingRequirement, guiLeft + 35, guiTop + 60, 0xC6C6C6);
            if (isMouseOverSlot(guiLeft + 15, guiTop + 55, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
        }
        if (reqsOnPage > 2) {
            ItemRequirement requirement = te.requirementList.get(page * 4 + 2);
            ItemStack itemstack = requirement.item;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, guiLeft + 15, guiTop + 75);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, guiLeft + 15, guiTop + 75, null);
            drawString(fontRenderer, "* " + requirement.remainingRequirement, guiLeft + 35, guiTop + 80, 0xC6C6C6);
            if (isMouseOverSlot(guiLeft + 15, guiTop + 75, mouseX, mouseY)) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
        }
        if (reqsOnPage > 3) {
            ItemRequirement requirement = te.requirementList.get(page * 4 + 3);
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
            mc.displayGuiScreen(new PackageSelectGui(this.te));
        } else if (button.id == 1) {
            page--;
            buttonList.clear();
            addButton(new GuiFacButton(-1, guiLeft + 65, guiTop + 5, 50, 20, "Select", 0));
            int pageSize = te.requirementList.size() - (page * 4);
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
            addButton(new GuiFacButton(-1, guiLeft + 65, guiTop + 5, 50, 20, "Select", 0));
            int pageSize = te.requirementList.size() - (page * 4);
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

    @Override
    public void drawBackground(int tint) {
        mc.getTextureManager().bindTexture(background);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        mc.getTextureManager().bindTexture(textBackground);
        drawModalRectWithCustomSizedTexture(guiLeft + 10, guiTop + 30, 0, 0, 160, 112, 160, 112);
        //drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    private boolean isMouseOverSlot(int slotX, int slotY, int mouseX, int mouseY) {
        return this.isPointInRegion(slotX, slotY, 16, 16, mouseX, mouseY);
    }

    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY)
    {
        //int i = this.guiLeft;
        //int j = this.guiTop;
        //pointX = pointX - i;
        //pointY = pointY - j;
        return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
    }

    protected void drawSlot(int x, int y) {
        mc.getTextureManager().bindTexture(slotTexture);
        drawModalRectWithCustomSizedTexture(guiLeft + x, guiTop + y, 0, 0, 18, 18, 18, 18);
        //drawTexturedModalRect(guiLeft + x, guiTop + y, 0, 0, 18, 18);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public static class PackageSelectGui extends GuiScreen {
        public static final int WIDTH = 180;
        public static final int HEIGHT = 152;

        protected int xSize;
        protected int ySize;
        protected int guiLeft;
        protected int guiTop;
        protected int numberOfPages;
        protected int currentPage = 1;
        protected TilePackager te;
        protected BlockPos teBlockPos;
        protected PackagerContainer container;

        protected List<String> listOfNames = new ArrayList<String>();
        protected ConcurrentHashMap<Integer, String> packageToId = new ConcurrentHashMap<Integer, String>();
        protected ConcurrentHashMap<Integer, GuiButton> idToButton = new ConcurrentHashMap<Integer, GuiButton>();

        private static final ResourceLocation background = new ResourceLocation(packagemod.MODID, "textures/gui/180x152.png");
        public PackageSelectGui(TilePackager te) {
            xSize = WIDTH;
            ySize = HEIGHT;
            int cpId = 1;
            this.listOfNames = CraftingPackage.getListOfNames();
            //packagemod.logger.info("list of names mod = " + listOfNames.size() % 4);
            for (String cp : listOfNames) {
                packageToId.put(cpId, cp);
                idToButton.put(cpId, new GuiFacButton(cpId, 10, 0, 160, 20, cp, 2));
                cpId++;
            }
            this.te = te;
            this.teBlockPos = te.getPos();
        }

        @Override
        public void actionPerformed(GuiButton button) {
            if (button.id == -1) {
                buttonList.clear();
                currentPage--;
                for (int i = currentPage * 4 - 3; i <= currentPage * 4; i++) {
                    if (idToButton.get(i) != null) {
                        GuiButton buttonToAdd = idToButton.get(i);
                        buttonToAdd.x = 10 + guiLeft;
                        buttonToAdd.y = guiTop + 10 + 30 * (i - (currentPage - 1) * 4 - 1);
                        if (!(te.hasResearch(packageToId.get(i)))) buttonToAdd.enabled = false;
                        addButton(buttonToAdd);
                    }
                }
                GuiButton leftButton = new GuiFacButton(-1, guiLeft + 10, guiTop + 125, 20, 20, "<-", 1);
                if (currentPage > 1) {
                    leftButton.enabled = true;
                } else leftButton.enabled = false;
                GuiButton rightButton = new GuiFacButton(-2, guiLeft + 150, guiTop + 125, 20, 20, "->", 1);
                addButton(leftButton);
                addButton(rightButton);

            } else if (button.id == -2) {
                buttonList.clear();
                currentPage++;
                for (int i = currentPage * 4 - 3; i <= currentPage * 4; i++) {
                    if (idToButton.get(i) != null) {
                        GuiButton buttonToAdd = idToButton.get(i);
                        buttonToAdd.x = 10 + guiLeft;
                        buttonToAdd.y = guiTop + 10 + 30 * (i - (currentPage - 1) * 4 - 1);
                        if (!(te.hasResearch(packageToId.get(i)))) buttonToAdd.enabled = false;
                        addButton(buttonToAdd);
                    }
                }
                GuiButton leftButton = new GuiFacButton(-1, guiLeft + 10, guiTop + 125, 20, 20, "<-", 1);
                GuiButton rightButton = new GuiFacButton(-2, guiLeft + 150, guiTop + 125, 20, 20, "->", 1);
                if (listOfNames.size() > currentPage * 4) {
                    rightButton.enabled = true;
                } else rightButton.enabled = false;
                addButton(leftButton);
                addButton(rightButton);
            } else if (button.id > 0) {
                String packageName = packageToId.get(button.id);
                te.changeRecipe(packageName);
                PackageModPacketHandler.INSTANCE.sendToServer(new RecipeMessage(packageName, teBlockPos));
                FMLCommonHandler.instance().showGuiScreen(new PackagerGui(te));
            }
        }

        @Override
        public void initGui() {
            packagemod.logger.info("Initiating GUI");
            this.guiLeft = (this.width - this.xSize) / 2;
            this.guiTop = (this.height - this.ySize) / 2;
            for (int i = 1; i <= 4; i++) {
                if (idToButton.get(i) != null) {
                    GuiButton button = idToButton.get(i);
                    button.x = 10 + guiLeft;
                    button.y = guiTop + 10 + 30 * (i - 1);
                    if (!(te.hasResearch(packageToId.get(i)))) button.enabled = false;
                    addButton(button);
                }
            }
            GuiButton leftButton = new GuiFacButton(-1, guiLeft + 10, guiTop + 125, 20, 20, "<-", 1);
            leftButton.enabled = false;
            GuiButton rightButton = new GuiFacButton(-2, guiLeft + 150, guiTop + 125, 20, 20, "->", 1);
            if (listOfNames.size() > 4) {
                rightButton.enabled = true;
            } else rightButton.enabled = false;
            addButton(leftButton);
            addButton(rightButton);
            switch (listOfNames.size() % 4) {
                case 0:
                    numberOfPages = listOfNames.size()/4;
                    break;
                case 1: case 2: case 3:
                    numberOfPages = (listOfNames.size() - listOfNames.size() % 4)/4 + 1;
                    break;
                default:
                    numberOfPages = 0;
            }
            //packagemod.logger.info(new TextComponentTranslation("packagemod.page", currentPage,
            //        numberOfPages).getUnformattedComponentText());

        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawBackground(0);
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(mc.fontRenderer, new TextComponentTranslation("packagemod.page", currentPage,
                    numberOfPages).getUnformattedComponentText(), guiLeft + 90, guiTop + 130, 0x6e6e6e);
        }

        @Override
        public void drawBackground(int tint) {
            //super.drawBackground(tint);
            mc.getTextureManager().bindTexture(background);
            drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, WIDTH, HEIGHT);
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }
    }

}
