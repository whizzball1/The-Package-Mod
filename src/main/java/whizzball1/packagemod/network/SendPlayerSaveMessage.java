package whizzball1.packagemod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import whizzball1.packagemod.data.PlayerData;
import whizzball1.packagemod.tile.TileResearcher;

public class SendPlayerSaveMessage implements IMessage {
    public SendPlayerSaveMessage(){}

    private NBTTagCompound data;
    private BlockPos pos;
    private int requestId;
    public SendPlayerSaveMessage(NBTTagCompound data, BlockPos pos, int id) {
        this.data = data;
        this.pos = pos;
        this.requestId = id;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeTag(buf, data);
        buf.writeInt(requestId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        data = ByteBufUtils.readTag(buf);
        requestId = buf.readInt();
    }

    public static class SendPlayerSaveMessageHandler implements IMessageHandler<SendPlayerSaveMessage, IMessage> {
        @Override
        public IMessage onMessage(SendPlayerSaveMessage message, MessageContext ctx) {
            World currentWorld = Minecraft.getMinecraft().world;
            if (currentWorld.isBlockLoaded(message.pos)) {
                TileEntity te = currentWorld.getTileEntity(message.pos);
                if (te instanceof TileResearcher) {
                    if (message.requestId == 0) {
                        Minecraft.getMinecraft().addScheduledTask(() -> {
                            ((TileResearcher) te).setOwner(message.data);
                        });
                    } else if (message.requestId == 1) {
                        Minecraft.getMinecraft().addScheduledTask(() -> {
                            ((TileResearcher) te).receiveMadeData(message.data);
                        });
                    }
                }
            }
            return null;
        }
    }
}