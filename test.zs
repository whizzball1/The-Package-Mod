import packagemod.PackageBuilder;
import packagemod.ResearchBuilder;

//Creates an object that lets you build a package.
val test = PackageBuilder.get();
//Sets name. Don't duplicate! Minecraft won't choke, but you'll lose one of your packages.
test.setName("Ender Chest Pack 2");
//Sets ID. Don't duplicate.
test.setID("endchest_pack_2");
//Takes an IItemStack. No OreDict or Ingredient right now.
test.setResult(<minecraft:ender_chest> * 32);
//Required for GUI. Number is the "tier" of the pack, like Ender Chest Pack 2 in this case.
//If the pack isn't tiered just say 1.
test.setNumber(2);
//You can run this multiple times to add IItemStacks. No OreDicts or Ingredients.
test.addRequirements(<minecraft:ender_pearl> * 4, <minecraft:blaze_powder> * 4);
//This clear all requirements currently set and replaces them with this array and only this one.
//test.setRequirements(<minecraft:ender_pearl> * 4, <minecraft:blaze_powder> * 4);


val research = ResearchBuilder.get();
research.addIngredients(<minecraft:ender_chest> * 8);
//One at a time for the next two methods.
research.addPack("Ender Chest Pack 1", 8);
research.addPreReqs("Ender Chest Pack 1");

//Adds research to the PackageBuilder.
test.setResearch(research);

//Prints name, ID, result, and requirements. Mostly for my own sake.
test.debug("all");

//Requires name, ID, result, number, requirements, and research. If you don't see "Successfully registered name"
//you can trust you've done something wrong—either you have a duplicate name or ID or you didn't add one of
//the required items to create a package.
test.create();