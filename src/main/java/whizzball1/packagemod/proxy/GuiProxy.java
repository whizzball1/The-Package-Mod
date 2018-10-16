package whizzball1.packagemod.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import whizzball1.packagemod.gui.PackagerGui;
import whizzball1.packagemod.tile.TilePackager;

public class GuiProxy implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        //BlockPos pos = new BlockPos(x, y, z);
        //TileEntity te = world.getTileEntity(pos);
        //if (ID == 1) {
        //    if (te instanceof TilePackager) {
        //        return new PackagerContainer(player.inventory, (TilePackager) te, player);
        //    }
        //}
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (ID == 1) {
            if (te instanceof TilePackager) {
                TilePackager packagerTE = (TilePackager) te;
                return new PackagerGui(packagerTE);
            }
        }
        return null;
    }
}
