package whizzball1.packagemod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.packagemod;
import whizzball1.packagemod.tile.TilePackager;
import whizzball1.packagemod.tile.TileResearcher;

public class RecipeMessage implements IMessage {
    public RecipeMessage(){}

    private int recipeId;
    private BlockPos blockPos;
    public RecipeMessage(String recipeName, BlockPos blockPos) {
        this.recipeId = CraftingPackage.getPackageGivenName(recipeName).intId;
        this.blockPos = blockPos;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(recipeId);
        buf.writeLong(blockPos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        recipeId = buf.readInt();
        blockPos = BlockPos.fromLong(buf.readLong());
    }

    public static class RecipeMessageHandler implements IMessageHandler<RecipeMessage, IMessage> {
        @Override
        public IMessage onMessage(RecipeMessage message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            int intId = message.recipeId;
            String cp = CraftingPackage.getPackageGivenIntId(intId).name;
            BlockPos blockPos = message.blockPos;
            if (serverPlayer.getEntityWorld().isBlockLoaded(blockPos)) {
                TileEntity te = serverPlayer.getEntityWorld().getTileEntity(blockPos);
                if (te instanceof TilePackager) {
                    //packagemod.logger.info("packet received, changing recipe");
                    serverPlayer.getServerWorld().addScheduledTask(() -> {
                        ((TilePackager) te).changeRecipe(cp);
                    });
                    serverPlayer.getServerWorld().addScheduledTask(() -> {
                        ((TilePackager) te).setOwner(serverPlayer.getUniqueID());
                    });

                } else if (te instanceof TileResearcher) {
                    serverPlayer.getServerWorld().addScheduledTask(() -> {
                        ((TileResearcher) te).changeRecipe(cp);
                    });
                    serverPlayer.getServerWorld().addScheduledTask(() -> {
                        ((TileResearcher) te).setOwner(serverPlayer.getUniqueID());
                    });
                }
            }
            return null;
        }
    }
}
