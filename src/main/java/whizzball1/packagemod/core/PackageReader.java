package whizzball1.packagemod.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Level;
import whizzball1.packagemod.packagemod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PackageReader {
    private static JsonArray data;
    public static JsonArray packages;
    private static File dataFile;

    public static void init(File file) {
        dataFile = file;
        try {
            load();
        }
        catch (IOException e) {
            packagemod.logger.log(Level.ERROR, "Problem accessing Package Adder", e);
            e.printStackTrace();;
        }
        for (int i=0; i<packages.size(); i++) {
            deserialise(packages.get(i).getAsJsonObject());
        }
    }

    public static void load() throws IOException {
        data = packagemod.gson.fromJson(new BufferedReader(new FileReader(dataFile)), JsonArray.class).getAsJsonArray();
        //System.out.println(data.get(0).toString());
        //System.out.println(data.get(0).getAsJsonObject().get("packages").toString());
        packages = data.get(0).getAsJsonObject().get("packages").getAsJsonArray();
    }

    public static void deserialise(JsonObject packageJson) {
        String packageName = packageJson.get("name").getAsString();
        String packageId = packageJson.get("id").getAsString();
        int packageNumber = packageJson.get("packageNumber").getAsInt();
        JsonArray resultArray = packageJson.get("resultItem").getAsJsonArray();
        String resultItem = resultArray.get(0).getAsString();
        int resultMeta;
        int resultNumber;
        if (resultArray.size() < 3) {
            resultMeta = 0;
        } else {
            resultMeta = resultArray.get(2).getAsInt();
        }
        if (resultArray.size() < 2) {
            resultNumber = 1;
        } else {
            resultNumber = resultArray.get(1).getAsInt();
        }
        ItemRequirement packageResult = new ItemRequirement(resultItem, resultMeta, resultNumber);
        JsonArray ingredientArray = packageJson.get("ingredients").getAsJsonArray();
        JsonArray researchArray = packageJson.get("research").getAsJsonArray();
        if (ingredientArray.size() > 9) {
            packagemod.logger.log(Level.ERROR, packageName + " has more than 9 ingredients. Did not register package.");
        } else {
            packagemod.craftingPackageList.add(new CraftingPackage(packageName, packageId, packageNumber, packageResult, ingredientArray, researchArray));
            packagemod.logger.log(Level.INFO, "Successfully registered package " + packageName);
        }

    }
}
