package whizzball1.packagemod.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import whizzball1.packagemod.network.GenericMessage;
import whizzball1.packagemod.network.PackageModPacketHandler;
import whizzball1.packagemod.packagemod;
import whizzball1.packagemod.tile.TileUnpackager;

public class UnpackagerGui extends GuiScreen {
    public static final int WIDTH = 110;
    public static final int HEIGHT = 110;
    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;

    private static final ResourceLocation background = new ResourceLocation(packagemod.MODID, "textures/gui/packager.png");
    private static final ResourceLocation textBackground = new ResourceLocation(packagemod.MODID, "textures/gui/packagertext.png");

    protected TileUnpackager te;

    public UnpackagerGui(TileUnpackager te) {
        this.te = te;
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        addButton(new GuiButton(0, guiLeft + 10, guiTop + 5, 90, 20, "Remove Package"));
        updateButton();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        if (te.hasPackage) {
            this.itemRender.renderItemAndEffectIntoGUI(te.currentPackage, guiLeft + 47, guiTop + 57);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, te.currentPackage, guiLeft + 47, guiTop + 57, null);
            if (isMouseOverSlot(guiLeft + 47, guiTop + 57, mouseX, mouseY)) {
                this.renderToolTip(te.currentPackage, mouseX, mouseY);
            }
        }
        updateButton();
    }

    @Override
    public void drawBackground(int tint) {
        mc.getTextureManager().bindTexture(background);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        mc.getTextureManager().bindTexture(textBackground);
        drawModalRectWithCustomSizedTexture(guiLeft + 10, guiTop + 30, 0, 0, 90, 70, 90, 70);
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

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            PackageModPacketHandler.INSTANCE.sendToServer(new GenericMessage(te));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //Call during display screen
    public void updateButton() {
        if (buttonList.size() > 0) buttonList.get(0).enabled = te.hasPackage;
    }

}
