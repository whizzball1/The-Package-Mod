package whizzball1.packagemod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import whizzball1.packagemod.client.IHasModel;
import whizzball1.packagemod.core.PackageCreativeTab;
import whizzball1.packagemod.gui.PackagerGui;
import whizzball1.packagemod.init.ModRegistry;
import whizzball1.packagemod.packagemod;
import whizzball1.packagemod.tile.TilePackager;

public class BlockPackager extends Block implements ITileEntityProvider, IHasModel {

    public BlockPackager(String name) {
        super(Material.ROCK);
        setRegistryName(name);
        setUnlocalizedName(packagemod.MODID + "." + name);
        setCreativeTab(PackageCreativeTab.INSTANCE);
        this.lightOpacity = 0;
        ModRegistry.BLOCKS.add(this);
        ModRegistry.ITEMS.add(new ItemBlock(this).setRegistryName(getRegistryName()));
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TilePackager();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player != null && world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TilePackager) {
                TilePackager packagerTE = (TilePackager) te;
                if (!(player instanceof FakePlayer)) {
                    packagerTE.setOwner(player.getUniqueID());
                }
                FMLCommonHandler.instance().showGuiScreen(new PackagerGui(packagerTE));
            }
            //player.openGui(packagemod.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
