package whizzball1.packagemod.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import whizzball1.packagemod.init.ModRegistry;
import whizzball1.packagemod.packagemod;

public class PackageCreativeTab extends CreativeTabs {
    public static final PackageCreativeTab INSTANCE = new PackageCreativeTab();

    public PackageCreativeTab() {
        super(packagemod.MODID);

    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModRegistry.packageReader);
    }
}
