package whizzball1.packagemod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.tile.TilePackager;
import whizzball1.packagemod.tile.TileResearcher;

public class ReqChangedMessage implements IMessage {
    //In this message, send one item requirement to a specific tile entity.
    private ItemStack req;
    private BlockPos pos;
    private int remaining;

    public ReqChangedMessage(){}

    public ReqChangedMessage(ItemRequirement r, BlockPos pos) {
        req = r.item;
        remaining = r.remainingRequirement;
        this.pos = pos;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeItemStack(buf, req);
        buf.writeInt(remaining);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        req = ByteBufUtils.readItemStack(buf);
        remaining = buf.readInt();
    }

    public static class ReqChangedMessageHandler implements IMessageHandler<ReqChangedMessage, IMessage> {
        @Override
        public IMessage onMessage(ReqChangedMessage message, MessageContext ctx) {
            World currentWorld = Minecraft.getMinecraft().world;
            if (currentWorld.isBlockLoaded(message.pos)) {
                TileEntity te = currentWorld.getTileEntity(message.pos);
                if (te instanceof TilePackager) {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        ((TilePackager) te).updateRequirement(message.req, message.remaining);
                    });
                }
                if (te instanceof TileResearcher) {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        ((TileResearcher) te).updateRequirement(message.req, message.remaining);
                    });
                }

            }
            return null;
        }
    }

}
