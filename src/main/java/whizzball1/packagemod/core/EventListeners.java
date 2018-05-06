package whizzball1.packagemod.core;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import whizzball1.packagemod.data.WorldData;

public class EventListeners {
    @SubscribeEvent
    public void onPlayerLoggedIn (PlayerEvent.PlayerLoggedInEvent e) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server != null) {
            World world = server.getEntityWorld();
            if(world!=null && !world.isRemote) {
                WorldData.get(world, false).markDirty();
            }
        }
    }
}
