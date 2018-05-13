package whizzball1.packagemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import whizzball1.packagemod.packagemod;

public class GuiFacButton extends GuiButton {

    private ResourceLocation button;
    private ResourceLocation buttonHovered;
    private ResourceLocation buttonDisabled;
    /**
     * Types: 0 = 50x20
     * 1 = 20x20
     * 2 = 160x20
     */

    public GuiFacButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int type)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        switch (type) {
            case 0:
                this.button = new ResourceLocation(packagemod.MODID, "textures/gui/button/50x20b.png");
                this.buttonHovered = new ResourceLocation(packagemod.MODID, "textures/gui/button/50x20h.png");
                this.buttonDisabled = new ResourceLocation(packagemod.MODID, "textures/gui/button/50x20d.png");
                break;
            case 1:
                this.button = new ResourceLocation(packagemod.MODID, "textures/gui/button/20x20b.png");
                this.buttonHovered = new ResourceLocation(packagemod.MODID, "textures/gui/button/20x20h.png");
                this.buttonDisabled = new ResourceLocation(packagemod.MODID, "textures/gui/button/20x20d.png");
                break;
            case 2:
                this.button = new ResourceLocation(packagemod.MODID, "textures/gui/button/160x20b.png");
                this.buttonHovered = new ResourceLocation(packagemod.MODID, "textures/gui/button/160x20h.png");
                this.buttonDisabled = new ResourceLocation(packagemod.MODID, "textures/gui/button/160x20d.png");
                break;
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hov = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            switch (hov) {
                case 0:
                    mc.getTextureManager().bindTexture(buttonDisabled);
                    break;
                case 1:
                    mc.getTextureManager().bindTexture(button);
                    break;
                case 2:
                    mc.getTextureManager().bindTexture(buttonHovered);
                    break;
            }
            drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 7339936;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}
