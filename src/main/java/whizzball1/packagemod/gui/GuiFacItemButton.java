package whizzball1.packagemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import whizzball1.packagemod.core.CraftingPackage;

public class GuiFacItemButton extends GuiFacButton {

    private ItemStack display;

    GuiFacItemButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int type) {
        super(buttonId, x, y, widthIn, heightIn, buttonText, type);
        CraftingPackage cp = CraftingPackage.getPackageGivenName(buttonText);
        display = new ItemStack(cp.result.item.getItem(), cp.number);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        RenderItem itemRender = mc.getRenderItem();
        GlStateManager.pushMatrix();
        GlStateManager.scale(2, 2, 1);
        itemRender.renderItemAndEffectIntoGUI(display, (this.x + 9)/2, (this.y + 9)/2);
        itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, display, (this.x + 9)/2, (this.y + 9)/2, null);
        GlStateManager.popMatrix();
    }


}
