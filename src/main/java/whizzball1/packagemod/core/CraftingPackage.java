package whizzball1.packagemod.core;

import com.google.gson.JsonArray;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;
import whizzball1.packagemod.packagemod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CraftingPackage implements Comparable<CraftingPackage> {
    public String name;
    public String id;
    public int number;
    public int intId;
    public ItemRequirement result;
    public List<ItemRequirement> inputs = new ArrayList<ItemRequirement>();
    public ConcurrentHashMap<ItemRequirement.ReqKey, ItemRequirement> stackToRequirement =  new ConcurrentHashMap<>();
    public PackageResearch research;

    public CraftingPackage(String name, String id, int number, ItemRequirement result, JsonArray inputs, JsonArray research) {
        this.name = name;
        this.id = id;
        this.intId = packagemod.craftingPackageList.size();
        this.number = number;
        this.result = result;
        for (int i=0; i < inputs.size(); i++) {
            int ingredientMeta;
            int ingredientNumber;
            JsonArray ingredientArray = inputs.get(i).getAsJsonArray();
            if (ingredientArray.size() < 3) {
                ingredientMeta = 0;
            } else {
                ingredientMeta = ingredientArray.get(2).getAsInt();
            }
            if (ingredientArray.size() < 2) {
                ingredientNumber = 1;
            } else {
                ingredientNumber = ingredientArray.get(1).getAsInt();
            }
            String ingredient = ingredientArray.get(0).getAsString();
            ItemRequirement it = new ItemRequirement(ingredient, ingredientMeta, ingredientNumber);
            this.inputs.add(it);
            this.stackToRequirement.put(new ItemRequirement.ReqKey(it.item), it);
        }
        this.research = new PackageResearch(research);
    }

    public CraftingPackage(String name, String id, int number, ItemRequirement result, List<ItemRequirement> inputs, PackageResearch research) {
        this.name = name;
        this.id = id;
        this.intId = packagemod.craftingPackageList.size();
        this.number = number;
        this.result = result;
        this.inputs = inputs;
        for (ItemRequirement i:inputs) {
            this.stackToRequirement.put(new ItemRequirement.ReqKey(i.item), i);
        }
        this.research = research;
    }

    public int compareTo(CraftingPackage c) {
        int compared = id.compareTo(c.id);
        return compared;
    }

    public boolean equals(Object o) {
        if (!(o instanceof CraftingPackage))
            return false;
        CraftingPackage c = (CraftingPackage) o;
        return c.id.equals(id);
    }

    public static NBTTagCompound createPackage(String name) {
        CraftingPackage cp = CraftingPackage.getPackageGivenName(name);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("Name", name);
        nbt.setInteger("Amount", cp.result.totalRequirement);
        //packagemod.logger.info(nbt.toString());
        return nbt;
    }

    public static boolean hasRequirement(CraftingPackage cp, ItemStack item, int remaining, boolean research) {
        ItemRequirement.ReqKey key = new ItemRequirement.ReqKey(item);
        if (!(research)) {
            if (cp.stackToRequirement.get(key) != null) {
                if (remaining > cp.stackToRequirement.get(key).totalRequirement) {
                    return false;
                } else return true;
            }
        } else {
            if (cp.research.stackToResearch.get(key) != null) {
                if (remaining > cp.research.stackToResearch.get(key).totalRequirement) {
                    return false;
                } else return true;
            }
        }
        return false;
    }

    public static CraftingPackage getPackageGivenName(String name) {
        for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
            CraftingPackage cp = packagemod.craftingPackageList.get(i);
            if (cp.name.equals(name)) {
                return cp;
            }
        }
        return null;
    }

    public static CraftingPackage getPackageGivenId(String id) {
        for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
            CraftingPackage cp = packagemod.craftingPackageList.get(i);
            if (cp.id.equals(id)) {
                return cp;
            }
        }
        return null;
    }

    public static CraftingPackage getPackageGivenIntId(int id) {
        for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
            CraftingPackage cp = packagemod.craftingPackageList.get(i);
            if (cp.intId == id) {
                return cp;
            }
        }
        return null;
    }

    public static List<String> getListOfIDs() {
        List<String> idList = new ArrayList<String>();
        for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
            idList.add(packagemod.craftingPackageList.get(i).id);
        }
        return idList;
    }

    public static List<String> getListOfNames() {
        List<String> nameList = new ArrayList<String>();
        for (int i = 0; i < packagemod.craftingPackageList.size(); i++) {
            nameList.add(packagemod.craftingPackageList.get(i).name);
        }
        Collections.sort(nameList);
        return nameList;
    }

    public List<ItemRequirement> cloneList() {
        List<ItemRequirement> o = new ArrayList<>();
        for (ItemRequirement i : inputs) {
            o.add(new ItemRequirement(i.item, i.totalRequirement));
        }
        return o;
    }

    public boolean isRequirement(ItemStack item) {
        for (ItemRequirement i : inputs) {
            if (item.isItemEqual(i.item)); {
                return true;
            }
        }
        return false;
    }

    public static class PackStack {
        public String packName;
        public int packNumber;
        public PackStack(String name, int number) {
            this.packName = name;
            this.packNumber = number;
        }
    }

    public static class PackageResearch {
        public List<ItemRequirement> ingredientInputs = new ArrayList<ItemRequirement>();
        public List<PackStack> packInputs = new ArrayList<PackStack>();
        public List<String> preReqs = new ArrayList<>();
        public ConcurrentHashMap<ItemRequirement.ReqKey, ItemRequirement> stackToResearch =  new ConcurrentHashMap<>();


        PackageResearch(JsonArray researchArray) {
            for (int i=0; i < researchArray.size(); i++) {
                JsonArray tentativeArray = researchArray.get(i).getAsJsonArray();
                if (tentativeArray.get(0).getAsString().equals("pack")) {
                    packInputs.add(new PackStack(tentativeArray.get(1).getAsString(), tentativeArray.get(2).getAsInt()));
                } else if (tentativeArray.get(0).getAsString().equals("prereq")) {
                    preReqs.add(tentativeArray.get(1).getAsString());
                } else
                    {
                    int ingredientMeta;
                    int ingredientNumber;
                    String ingredient = tentativeArray.get(1).getAsString();
                    if (tentativeArray.size() < 4) {
                        ingredientMeta = 0;
                    } else {
                        ingredientMeta = tentativeArray.get(3).getAsInt();
                    }
                    if (tentativeArray.size() < 3) {
                        ingredientNumber = 1;
                    } else {
                        ingredientNumber = tentativeArray.get(2).getAsInt();
                    }
                    ItemRequirement it = new ItemRequirement(ingredient, ingredientMeta, ingredientNumber);
                    ingredientInputs.add(it);
                    stackToResearch.put(new ItemRequirement.ReqKey(it.item), it);

                }
            }
        }

        public PackageResearch(List<ItemRequirement> ingredients, List<PackStack> packs, List<String> preReqs) {
            ingredientInputs = ingredients;
            for (ItemRequirement i:ingredientInputs) {
                stackToResearch.put(new ItemRequirement.ReqKey(i.item), i);
            }
            packInputs = packs;
            this.preReqs = preReqs;
        }

        public List<ItemRequirement> cloneItemList() {
            List<ItemRequirement> o = new ArrayList<>();
            for (ItemRequirement i : ingredientInputs) {
                o.add(new ItemRequirement(i.item, i.totalRequirement));
            }
            return o;
        }

        public List<PackStack> clonePackList() {
            List<PackStack> o = new ArrayList<>();
            for (PackStack p : packInputs) {
                o.add(new PackStack(p.packName, p.packNumber));
            }
            return o;
        }
    }
}
