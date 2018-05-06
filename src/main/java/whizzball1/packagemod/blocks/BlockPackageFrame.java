package whizzball1.packagemod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import whizzball1.packagemod.client.IHasModel;
import whizzball1.packagemod.core.PackageCreativeTab;
import whizzball1.packagemod.init.ModRegistry;
import whizzball1.packagemod.packagemod;

public class BlockPackageFrame extends Block implements IHasModel{
    public BlockPackageFrame(String name) {
        super(Material.ROCK);
        setRegistryName(name);
        setUnlocalizedName(packagemod.MODID + "." + name);
        setCreativeTab(PackageCreativeTab.INSTANCE);
        ModRegistry.BLOCKS.add(this);
        ModRegistry.ITEMS.add(new ItemBlock(this).setRegistryName(getRegistryName()));
    }

    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
