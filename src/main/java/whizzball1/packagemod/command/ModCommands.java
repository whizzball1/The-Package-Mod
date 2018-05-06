package whizzball1.packagemod.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ModCommands {

    public static void registerCommands(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandSetResearched());
        e.registerServerCommand(new CommandCheckIfResearched());
        e.registerServerCommand(new CommandCheckResearched());
    }
}
