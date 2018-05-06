package whizzball1.packagemod.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import whizzball1.packagemod.client.IHasModel;
import whizzball1.packagemod.core.PackageCreativeTab;
import whizzball1.packagemod.init.ModRegistry;
import whizzball1.packagemod.packagemod;

public class ItemPackageReader extends Item implements IHasModel {
    public ItemPackageReader(String registryName) {
        setRegistryName(registryName);
        setUnlocalizedName(packagemod.MODID + "." + registryName);
        setCreativeTab(PackageCreativeTab.INSTANCE);
        ModRegistry.ITEMS.add(this);
    }

    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
