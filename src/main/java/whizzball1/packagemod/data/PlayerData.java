package whizzball1.packagemod.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.NoteBlockEvent;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.packagemod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    public static PlayerSave getDataFromPlayer(EntityPlayer player) {
        WorldData worldData = WorldData.get(player.getEntityWorld(), false);
        ConcurrentHashMap<UUID, PlayerSave> data = worldData.playerSaveData;
        UUID id = player.getUniqueID();

        if (data.containsKey(id)) {
            PlayerSave save = data.get(id);
            if (save != null && save.id != null && save.id.equals(id)) {
                return save;
            }
        }
        PlayerSave save = new PlayerSave(id);
        data.put(id, save);
        worldData.markDirty();
        return save;
    }

    public static void setResearched(World world, UUID id, String pack) {
        List<String> pr = getDataFromPlayer(world, id).packagesResearched;
        if (!(pr.contains(pack))) pr.add(pack);
    }

    public static PlayerSave getDataFromPlayer(World world, UUID id) {
        WorldData worldData = WorldData.get(world, false);
        ConcurrentHashMap<UUID, PlayerSave> data = worldData.playerSaveData;

        if (data.containsKey(id)) {
            PlayerSave save = data.get(id);
            if (save != null && save.id != null && save.id.equals(id)) {
                return save;
            }
        }
        PlayerSave save = new PlayerSave(id);
        data.put(id, save);
        worldData.markDirty();
        return save;
    }

    public static class PlayerSave{
        public UUID id;
        public List<String> packagesResearched = new ArrayList<String>();
        public ConcurrentHashMap<String, Integer> packagesMade = new ConcurrentHashMap<String, Integer>();

        public PlayerSave(UUID id){
            this.id = id;
        }

        public void readFromNBT(NBTTagCompound compound) {
            NBTTagCompound researchData = compound.getCompoundTag("Research");
            packagesResearched.clear();
            for (CraftingPackage cp : packagemod.craftingPackageList) {
                String packageName = cp.name;
                if (researchData.getTag(packageName) == null) {
                    continue;
                }
                if (researchData.getInteger(packageName) == 1) {
                    packagesResearched.add(packageName);
                }
            }
            NBTTagCompound packageData = compound.getCompoundTag("Made");
            for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
                String packageName = packagemod.craftingPackageList.get(i).name;
                if (packageData.getTag(packageName) == null) {
                    continue;
                }
                int numberMade = packageData.getInteger(packageName);
                packagesMade.put(packageName, new Integer(numberMade));
            }
        }

        public void writeToNBT(NBTTagCompound compound){
            NBTTagCompound researchData = new NBTTagCompound();
            //packagemod.logger.info(packagesResearched.toString());
            packageloop: for (CraftingPackage cp : packagemod.craftingPackageList) {
                String packageName = cp.name;
                if (packagesResearched.contains(packageName)) {
                    researchData.setInteger(packageName, 1);
                    continue packageloop;
                }
                researchData.setInteger(packageName, 0);

            }
            compound.setTag("Research", researchData);
            NBTTagCompound packageData = new NBTTagCompound();
            for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
                String packageName = packagemod.craftingPackageList.get(i).name;
                if (packagesMade.get(packageName) == null) {
                    packagesMade.put(packageName, new Integer(0));
                }
                Integer numberMade = packagesMade.get(packageName);
                packageData.setInteger(packageName, numberMade.intValue());
            }
            compound.setTag("Made", packageData);
        }

        public void writeMadeToNBT(NBTTagCompound compound) {
            NBTTagCompound packageData = new NBTTagCompound();
            for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
                String packageName = packagemod.craftingPackageList.get(i).name;
                if (packagesMade.get(packageName) == null) {
                    packagesMade.put(packageName, new Integer(0));
                }
                Integer numberMade = packagesMade.get(packageName);
                packageData.setInteger(packageName, numberMade.intValue());
            }
            compound.setTag("Made", packageData);
        }

        public void writeResearchedToNBT(NBTTagCompound compound) {
            NBTTagCompound researchData = new NBTTagCompound();
            //packagemod.logger.info("writing researched to NBT: " + packagesResearched.toString());
            packageloop: for (CraftingPackage cp : packagemod.craftingPackageList) {
                String packageName = cp.name;
                if (packagesResearched.contains(packageName)) {
                    researchData.setInteger(packageName, 1);
                    continue packageloop;
                }
                researchData.setInteger(packageName, 0);

            }
            compound.setTag("Research", researchData);
        }

        public void readMadeFromNBT(NBTTagCompound compound) {
            NBTTagCompound packageData = compound.getCompoundTag("Made");
            for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
                String packageName = packagemod.craftingPackageList.get(i).name;
                if (packageData.getTag(packageName) == null) {
                    continue;
                }
                int numberMade = packageData.getInteger(packageName);
                packagesMade.put(packageName, new Integer(numberMade));
            }
        }

        public void readResearchedFromNBT(NBTTagCompound compound) {
            NBTTagCompound researchData = compound.getCompoundTag("Research");
            packagesResearched.clear();
            for (CraftingPackage cp : packagemod.craftingPackageList) {
                String packageName = cp.name;
                if (researchData.getTag(packageName) == null) {
                    continue;
                }
                if (researchData.getInteger(packageName) == 1) {
                    packagesResearched.add(packageName);
                    //packagemod.logger.info(packageName);
                }
            }
        }


    }

}
