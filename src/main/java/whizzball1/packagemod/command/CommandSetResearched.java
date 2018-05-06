package whizzball1.packagemod.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.Level;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.data.PlayerData;
import whizzball1.packagemod.data.WorldData;
import whizzball1.packagemod.packagemod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandSetResearched extends CommandBase {
    public String getName() {
        return "packagesetresearched";
    }

    public String getUsage(ICommandSender sender) {
        return "commands.packagemod.packagesetresearched.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean trueOrFalse;
        if (args.length != 3) {
            throw new WrongUsageException(getUsage(sender));
        }
        String playerPicked = args[0];
        GameProfile gameprofile = server.getPlayerProfileCache().getGameProfileForUsername(args[0]);

        if (gameprofile == null) {
            throw new CommandException("commands.packagemod.packagesetresearched.playerfailed");
        }
        CraftingPackage cp = CraftingPackage.getPackageGivenId(args[1]);
        packagemod.logger.log(Level.INFO, args[1]);
        if (cp == null) {
            throw new CommandException("commands.packagemod.packagesetresearched.cpfailed");
        }
        if (args[2].equals("true")) {
            trueOrFalse = true;
        } else if (args[2].equals("false")) {
            trueOrFalse = false;
        } else throw new WrongUsageException(getUsage(sender));
        String cpName = cp.name;
        UUID id = gameprofile.getId();
        World world = server.getEntityWorld();
        WorldData wd;
        if(world!=null && !world.isRemote) {
                wd = WorldData.get(world, false);
                if (wd != null) {
                    PlayerData.PlayerSave ps = wd.playerSaveData.get(id);
                    if (ps != null) {
                        List<String> researchedPackages = ps.packagesResearched;
                        if (trueOrFalse) {
                            if (researchedPackages.contains(cpName)) {
                                //then this command is useless of course
                            } else {
                                researchedPackages.add(cpName);
                                ps.packagesResearched = researchedPackages;
                                //wd.playerSaveData.put(id, ps);
                                wd.markDirty();
                                //WorldData.data = wd;
                            }
                        } else {
                            if (researchedPackages.contains(cpName)) {
                                researchedPackages.remove(cpName);
                                ps.packagesResearched = researchedPackages;
                                //wd.playerSaveData.put(id, ps);
                                wd.markDirty();
                                //WorldData.data = wd;
                            }
                        }
                    }
                }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        List<String> completionList = new ArrayList<String>();
        if (args.length == 1) {
            completionList=getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            completionList=getListOfStringsMatchingLastWord(args, CraftingPackage.getListOfIDs());
        } else if (args.length == 3) {
            completionList=getListOfStringsMatchingLastWord(args, "true", "false");
        }
        return completionList;
    }
}
