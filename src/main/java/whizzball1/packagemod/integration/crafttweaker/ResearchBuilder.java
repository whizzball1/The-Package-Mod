package whizzball1.packagemod.integration.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.packagemod;

import java.util.ArrayList;
import java.util.List;

@ZenClass("packagemod.ResearchBuilder")
@ZenRegister
public class ResearchBuilder {
    public List<ItemRequirement> ingredientInputs = new ArrayList<ItemRequirement>();
    public List<CraftingPackage.PackStack> packInputs = new ArrayList<CraftingPackage.PackStack>();
    public List<String> preReqs = new ArrayList<>();

    ResearchBuilder() {

    }

    @ZenMethod
    public static ResearchBuilder get() {
        return new ResearchBuilder();
    }

    @ZenMethod
    public void addIngredients(IItemStack... inputs) {
        for (IItemStack i:inputs) {
            ingredientInputs.add(new ItemRequirement(i.getDefinition().getId(), i.getMetadata(), i.getAmount()));
        }
    }

    @ZenMethod
    public void addPack(String pack, int number) {
        packInputs.add(new CraftingPackage.PackStack(pack, number));
    }

    @ZenMethod
    public void addPreReqs(String... prereqs) {
        for (String p:prereqs) {
            preReqs.add(p);
        }
    }

}
