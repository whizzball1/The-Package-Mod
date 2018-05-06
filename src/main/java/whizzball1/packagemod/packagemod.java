package whizzball1.packagemod;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import whizzball1.packagemod.command.ModCommands;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.data.WorldData;
import whizzball1.packagemod.init.ModRegistry;
import whizzball1.packagemod.proxy.CommonProxy;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = packagemod.MODID, name = packagemod.NAME, version = packagemod.VERSION)
public class packagemod {

    public static final String MODID = "packagemod";
    public static final String NAME = "The Package Mod";
    public static final String VERSION = "0.0.1";

    public static Logger logger;
    public static Gson gson = new Gson();
    public static JsonParser parser = new JsonParser();

    public static List<CraftingPackage> craftingPackageList = new ArrayList<CraftingPackage>();

    @Mod.Instance
    public static packagemod INSTANCE;

    @SidedProxy(clientSide = "whizzball1.packagemod.proxy.ClientProxy", serverSide = "whizzball1.packagemod.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        logger.log(Level.INFO, "preInit for PackageMod");
        PROXY.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

        PROXY.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        ModCommands.registerCommands(e);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent e) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server != null) {
            World world = server.getEntityWorld();
            if(world!=null && !world.isRemote) {
                WorldData.get(world, true).markDirty();
            }
        }
    }
}
