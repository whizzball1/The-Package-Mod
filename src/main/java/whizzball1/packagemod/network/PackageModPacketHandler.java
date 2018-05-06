package whizzball1.packagemod.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PackageModPacketHandler {
    private static int packetId = 0;
    public static SimpleNetworkWrapper INSTANCE;

    public PackageModPacketHandler() {

    }

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("packagemod");
        INSTANCE.registerMessage(RecipeMessage.RecipeMessageHandler.class, RecipeMessage.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(ReqChangedMessage.ReqChangedMessageHandler.class, ReqChangedMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PackageChangedMessage.PackageChangedMessageHandler.class, PackageChangedMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(GenericMessage.GenericMessageHandler.class, GenericMessage.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(RequestPlayerSaveMessage.RequestPlayerSaveMessageHandler.class,
                RequestPlayerSaveMessage.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(SendPlayerSaveMessage.SendPlayerSaveMessageHandler.class,
                SendPlayerSaveMessage.class, nextID(), Side.CLIENT);
    }
}
