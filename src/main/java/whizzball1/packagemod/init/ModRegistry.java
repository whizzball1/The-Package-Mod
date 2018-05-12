package whizzball1.packagemod.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;
import whizzball1.packagemod.blocks.BlockPackageFrame;
import whizzball1.packagemod.blocks.BlockPackager;
import whizzball1.packagemod.blocks.BlockResearcher;
import whizzball1.packagemod.blocks.BlockUnpackager;
import whizzball1.packagemod.items.ItemPackage;
import whizzball1.packagemod.items.ItemPackageReader;
import whizzball1.packagemod.packagemod;
import whizzball1.packagemod.tile.TilePackager;
import whizzball1.packagemod.tile.TileResearcher;
import whizzball1.packagemod.tile.TileUnpackager;

import java.util.ArrayList;
import java.util.List;

public class ModRegistry {
    public static final List<Block> BLOCKS = new ArrayList<Block>();
    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final Item packageReader = new ItemPackageReader("packagereader");
    public static final Item packageItem = new ItemPackage("packageitem");

    public static final Block packageFrame = new BlockPackageFrame("packageframe");
    public static final Block packager = new BlockPackager("packager");
    public static final Block unpackager = new BlockUnpackager("unpackager");
    public static final Block researcher = new BlockResearcher("researcher");

    @SubscribeEvent
    public void onBlockRegister(Register<Block> event) {
        packagemod.logger.log(Level.INFO, "registering packageMod blocks");
        event.getRegistry().registerAll(BLOCKS.toArray(new Block[0]));
        GameRegistry.registerTileEntity(TilePackager.class, packagemod.MODID + "_packager");
        GameRegistry.registerTileEntity(TileUnpackager.class, packagemod.MODID+"_unpackager");
        GameRegistry.registerTileEntity(TileResearcher.class, packagemod.MODID+"_researcher");
    }

    @SubscribeEvent
    public void onItemRegister(Register<Item> event) {
        packagemod.logger.log(Level.INFO, "registering packageMod items");
        event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));
    }
}
