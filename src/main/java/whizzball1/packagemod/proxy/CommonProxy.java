package whizzball1.packagemod.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import whizzball1.packagemod.core.EventListeners;
import whizzball1.packagemod.core.PackageReader;
import whizzball1.packagemod.init.ModRegistry;
import whizzball1.packagemod.network.PackageModPacketHandler;
import whizzball1.packagemod.packagemod;

import java.io.File;
import java.io.IOException;

public class CommonProxy {

    File configdir;

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ModRegistry());
        MinecraftForge.EVENT_BUS.register(new EventListeners());
        PackageModPacketHandler.registerMessages();
        configdir = new File(e.getModConfigurationDirectory(), "packagemod");
        if(!configdir.exists()) {
            configdir.mkdir();
        }
        File file = new File( configdir, "packageAdder.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void init(FMLInitializationEvent e) {
        File file = new File(configdir, "packageAdder.json");
        PackageReader.init(file);
        NetworkRegistry.INSTANCE.registerGuiHandler(packagemod.INSTANCE, new GuiProxy());
    }

    public void postInit(FMLPostInitializationEvent e) {
    }
}
