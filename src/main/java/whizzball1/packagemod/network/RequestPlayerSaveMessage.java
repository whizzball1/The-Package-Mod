package whizzball1.packagemod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import whizzball1.packagemod.data.PlayerData;

import java.util.UUID;

public class RequestPlayerSaveMessage implements IMessage {
    public RequestPlayerSaveMessage(){};

    private BlockPos pos;
    private UUID id;
    public RequestPlayerSaveMessage(BlockPos pos, UUID uuid) {
        this.pos = pos;
        this.id = uuid;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, id.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    public static class RequestPlayerSaveMessageHandler implements IMessageHandler<RequestPlayerSaveMessage, IMessage> {
        @Override
        public IMessage onMessage(RequestPlayerSaveMessage message, MessageContext ctx) {
            NBTTagCompound data = new NBTTagCompound();
            PlayerData.getDataFromPlayer(ctx.getServerHandler().player.getServerWorld(), message.id).writeToNBT(data);
            PackageModPacketHandler.INSTANCE.sendTo(new SendPlayerSaveMessage(data, message.pos, 0), ctx.getServerHandler().player);
            return null;
        }
    }
}
