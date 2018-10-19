package whizzball1.packagemod.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import whizzball1.packagemod.packagemod;

public class SelectGui extends GuiScreen {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;
    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;
    public int page;
    public int reqsOnPage;

    private static final ResourceLocation background = new ResourceLocation(packagemod.MODID, "textures/gui/180x152.png");
    private static final ResourceLocation sideBackground = new ResourceLocation(packagemod.MODID, "textures/gui/60x152.png");
    private static final ResourceLocation textBackground = new ResourceLocation(packagemod.MODID, "textures/gui/160x112.png");

    public SelectGui() {
        xSize = WIDTH;
        ySize = HEIGHT;
        page = 0;
    }

    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.buttonList.clear();
        addButton(new GuiFacButton(-1, guiLeft + 190, guiTop + 5, 50, 20, "Select", 0));
        addButton(new GuiFacButton(-2, guiLeft + 190, guiTop + 30, 50, 20, "Back", 0));
        //right and left buttons should go in extensions
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawBackground(int tint) {
        mc.getTextureManager().bindTexture(background);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        mc.getTextureManager().bindTexture(sideBackground);
        drawModalRectWithCustomSizedTexture(guiLeft + 185, guiTop, 0, 0, 60, 152, 60, 152);
        mc.getTextureManager().bindTexture(textBackground);
        drawModalRectWithCustomSizedTexture(guiLeft + 10, guiTop + 30, 0, 0, 160, 112, 160, 112);
        //drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    protected boolean isMouseOverSlot(int slotX, int slotY, int mouseX, int mouseY) {
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
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
