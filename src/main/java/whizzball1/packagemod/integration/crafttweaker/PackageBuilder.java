package whizzball1.packagemod.integration.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import org.apache.logging.log4j.Level;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.core.ItemRequirement;
import whizzball1.packagemod.packagemod;

import java.util.ArrayList;
import java.util.List;

@ZenClass("packagemod.PackageBuilder")
@ZenRegister
public class PackageBuilder {
    private String id;
    private String name;
    private ItemRequirement result;
    private int number;
    private List<ItemRequirement> inputs = new ArrayList<>();
    private ResearchBuilder research;

    public PackageBuilder() {

    };

    @ZenMethod
    public static PackageBuilder get() {
        return new PackageBuilder();
    }

    @ZenMethod
    public void setID(String id) {
        CraftingPackage cp = CraftingPackage.getPackageGivenId(id);
        if (cp == null) {
            this.id = id;
        } else packagemod.logger.error("Crafting package \"" + id + "\" has the same ID as " + cp.id + "; not registering!");
    }

    @ZenMethod
    public void setName(String name) {
        CraftingPackage cp = CraftingPackage.getPackageGivenName(name);
        if (cp == null) {
            this.name = name;
        } else packagemod.logger.error("Crafting package \"" + name + "\" has the same ID as " + cp.name + "; not registering!");
    }

    @ZenMethod
    public void setNumber(int number) {
        this.number = number;
    }

    @ZenMethod
    public void setResult(IItemStack item) {
        result = new ItemRequirement(item.getDefinition().getId(), item.getMetadata(), item.getAmount());
    }

    @ZenMethod
    public void addRequirements(IItemStack... item) {
        for (IItemStack i:item) {
            inputs.add(new ItemRequirement(i.getDefinition().getId(), i.getMetadata(), i.getAmount()));
        }
    }

    @ZenMethod
    public void setRequirements(IItemStack... item) {
        inputs.clear();
        for (IItemStack i:item) {
            inputs.add(new ItemRequirement(i.getName(), i.getMetadata(), i.getAmount()));
        }
    }

    @ZenMethod
    public void setResearch(ResearchBuilder rb) {
        this.research = rb;
    }

    @ZenMethod
    public void debug(String... field) {
        for (String s:field) {
            boolean all = s.equals("all");
            if (s.equals("id") || all) {
                packagemod.logger.info("Builder debug: ID: " + id);
            }
            if (s.equals("name") || all) {
                packagemod.logger.info("Builder debug: name: " + name);
            }
            if (s.equals("result") || all) {
                packagemod.logger.info("Builder debug: result: " + result.toString());
            }
            if (s.equals("requirements") || all) {
                packagemod.logger.info("Builder debug: requirements: " + inputs.toString());
            }
        }
    }

    @ZenMethod
    public void create() {
        if (!(this.id == null || this.name == null || this.result == null || this.inputs.isEmpty() || this.number == 0 || this.research == null)) {
            CraftingPackage.PackageResearch pr = new CraftingPackage.PackageResearch(research.ingredientInputs,research.packInputs,research.preReqs);
            packagemod.craftingPackageList.add(new CraftingPackage(name, id, number, result, inputs, pr));
            packagemod.logger.log(Level.INFO, "Successfully registered package " + name);
        }


    }

}
