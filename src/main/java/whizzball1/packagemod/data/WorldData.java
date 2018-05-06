package whizzball1.packagemod.data;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import whizzball1.packagemod.packagemod;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WorldData extends WorldSavedData {
    public static final String DATA_TAG = packagemod.MODID+"data";
    public static WorldData data;
    public ConcurrentHashMap<UUID, PlayerData.PlayerSave> playerSaveData = new ConcurrentHashMap<UUID, PlayerData.PlayerSave>();

    public WorldData(String name) {
        super(name);
    }

    public static WorldData get(World world, boolean forceLoad) {
        WorldData w = getInternal(world, forceLoad);
        if(w == null) packagemod.logger.error("it is null oops");
        return w == null ? new WorldData(DATA_TAG) : w;
    }

    private static WorldData getInternal(World world, boolean forceLoad){
        if(forceLoad || data == null){
            if(!world.isRemote){
                MapStorage storage = world.getMapStorage();
                WorldData savedData = (WorldData) storage.getOrLoadData(WorldData.class, DATA_TAG);

                if(!(savedData instanceof WorldData)){
                    packagemod.logger.info("No WorldData found, creating...");

                    WorldData newData = new WorldData(DATA_TAG);
                    world.setData(DATA_TAG, newData);
                    data = newData;
                }
                else{
                    data = savedData;
                    packagemod.logger.info("Successfully loaded WorldData!");
                }
            }
            else{
                data = new WorldData(DATA_TAG);
                packagemod.logger.info("Created temporary WorldData to cache data on the client!");
            }
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        packagemod.logger.info("read occurring!");
        this.readFromNBT(compound, false);
    }

    private void readFromNBT(NBTTagCompound compound, boolean merge) {
        //packagemod.logger.info(compound.getTagList("PlayerData", 10).getCompoundTagAt(0).getUniqueId("UUID"));
        NBTTagList currentList = compound.getTagList("PlayerData", 10);
        int listSize = currentList.tagCount();
        for (int i = 0; i < listSize; i++) {
            NBTTagCompound currentPlayer = currentList.getCompoundTagAt(i);
            UUID currentUUID = currentPlayer.getUniqueId("UUID");
            NBTTagCompound data = currentPlayer.getCompoundTag("Data");

            PlayerData.PlayerSave save = new PlayerData.PlayerSave(currentUUID);
            save.readFromNBT(data);
            this.playerSaveData.put(currentUUID, save);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        packagemod.logger.info("write occurring!");
        NBTTagList playerList = new NBTTagList();
        for(PlayerData.PlayerSave save : this.playerSaveData.values()) {
            NBTTagCompound player = new NBTTagCompound();
            player.setUniqueId("UUID", save.id);

            NBTTagCompound data = new NBTTagCompound();
            save.writeToNBT(data);
            player.setTag("Data", data);

            playerList.appendTag(player);
        }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        PlayerList playerListObject = server.getPlayerList();
        List<EntityPlayerMP> playerEntityList = playerListObject.getPlayers();
        for (EntityPlayerMP player : playerEntityList) {
            //NBTTagCompound playerCompound = new NBTTagCompound();
            UUID playerId = player.getUniqueID();
            if (playerSaveData.get(playerId) == null) {
                PlayerData.PlayerSave newSave = new PlayerData.PlayerSave(playerId);
                NBTTagCompound newPlayer = new NBTTagCompound();
                newPlayer.setUniqueId("UUID", playerId);

                NBTTagCompound data = new NBTTagCompound();
                newSave.writeToNBT(data);
                newPlayer.setTag("Data", data);

                newSave.readFromNBT(data);
                playerSaveData.put(playerId, newSave);

                playerList.appendTag(newPlayer);
            }
            //playerCompound.setUniqueId("UUID", playerId);
            //NBTTagCompound data = new NBTTagCompound();
            //playerCompound.setTag("Data", data);
            //playerList.appendTag(playerCompound);
        }
        //packagemod.logger.info(playerList.getTagType());
        int playerListSize = playerList.tagCount();
        //packagemod.logger.info(compound.getTagList("PlayerData", 10).getCompoundTagAt(0).getUniqueId("UUID"));
        compound.setTag("PlayerData", playerList);
        if (compound.getTagList("PlayerData", 10) == null) {

            //compound.setTag("PlayerData", playerList);
        } else {
            NBTTagList oldList = compound.getTagList("PlayerData", 10);
            int oldListSize = oldList.tagCount();
            for (int i = 0; i < oldListSize; i++) {
                NBTTagCompound currentPlayer = oldList.getCompoundTagAt(i);
                UUID currentUUID = currentPlayer.getUniqueId("UUID");
                packagemod.logger.info(currentUUID.toString());

            }

        }

        return compound;
    }
}
