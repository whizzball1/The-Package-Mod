package whizzball1.packagemod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import whizzball1.packagemod.tile.TileUnpackager;

public class GenericMessage implements IMessage {
    //This message can be sent to any tile entity with no data. May implement a field.
    public GenericMessage(){}

    private BlockPos pos;
    public GenericMessage(TileEntity tile) {
        pos = tile.getPos();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
    }

    public static class GenericMessageHandler implements IMessageHandler<GenericMessage, IMessage> {
        @Override
        public IMessage onMessage(GenericMessage message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            BlockPos blockPos = message.pos;
            if (serverPlayer.getEntityWorld().isBlockLoaded(blockPos)) {
                TileEntity te = serverPlayer.getEntityWorld().getTileEntity(blockPos);
                if (te instanceof TileUnpackager) {
                    serverPlayer.getServerWorld().addScheduledTask(() ->{
                        ((TileUnpackager) te).dump();
                    });
                }
            }

            return null;
        }
    }
}
