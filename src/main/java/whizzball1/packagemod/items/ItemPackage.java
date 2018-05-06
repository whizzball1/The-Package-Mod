package whizzball1.packagemod.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import whizzball1.packagemod.client.IHasModel;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.PackageCreativeTab;
import whizzball1.packagemod.init.ModRegistry;
import whizzball1.packagemod.packagemod;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPackage extends Item implements IHasModel {
    public ItemPackage(String registryName) {
        setRegistryName(registryName);
        setUnlocalizedName(packagemod.MODID + "." + registryName);
        setCreativeTab(PackageCreativeTab.INSTANCE);
        ModRegistry.ITEMS.add(this);
    }

    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        //packagemod.logger.info("adding information");
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            if (compound.hasKey("Amount") && compound.hasKey("Name")) {
                tooltip.add(compound.getString("Name"));
                String number = Integer.toString(compound.getInteger("Amount")) + " " + CraftingPackage.getPackageGivenName(compound.getString("Name"))
                        .result.item.getDisplayName();
                tooltip.add(number);
            }
        }
    }
}
