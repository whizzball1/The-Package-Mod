package whizzball1.packagemod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import whizzball1.packagemod.tile.TileUnpackager;

public class PackageChangedMessage implements IMessage {
    private ItemStack toUnpackage;
    private BlockPos pos;

    public PackageChangedMessage(){}

    public PackageChangedMessage(ItemStack stack, TileUnpackager tile) {
        this.toUnpackage = stack;
        this.pos = tile.getPos();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeItemStack(buf, toUnpackage);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        toUnpackage = ByteBufUtils.readItemStack(buf);
    }

    public static class PackageChangedMessageHandler implements IMessageHandler<PackageChangedMessage, IMessage> {
        @Override
        public IMessage onMessage(PackageChangedMessage message, MessageContext ctx) {
            World currentWorld = Minecraft.getMinecraft().world;
            if (currentWorld.isBlockLoaded(message.pos)) {
                TileEntity te = currentWorld.getTileEntity(message.pos);
                if (te instanceof TileUnpackager) {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        ((TileUnpackager) te).updatePackage(message.toUnpackage);
                    });
                }

            }
            return null;
        }
    }
}
