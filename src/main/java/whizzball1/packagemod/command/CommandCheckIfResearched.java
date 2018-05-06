package whizzball1.packagemod.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import whizzball1.packagemod.core.CraftingPackage;
import whizzball1.packagemod.data.PlayerData;
import whizzball1.packagemod.data.WorldData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandCheckIfResearched extends CommandBase {
    public String getName() {
        return "packagecheckifresearched";
    }

    public String getUsage(ICommandSender sender) {
        return "commands.packagemod.packagecheckifresearched.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new WrongUsageException(getUsage(sender));
        }
        GameProfile gameprofile = server.getPlayerProfileCache().getGameProfileForUsername(args[0]);
        if (gameprofile == null) {
            throw new CommandException("commands.packagemod.packagesetresearched.playerfailed");
        }
        CraftingPackage cp = CraftingPackage.getPackageGivenId(args[1]);
        if (cp == null) {
            throw new CommandException("commands.packagemod.packagesetresearched.cpfailed");
        }
        String cpName = cp.name;
        UUID id = gameprofile.getId();
        World world = server.getEntityWorld();
        WorldData wd;

        if(world != null && !world.isRemote) {
            wd = WorldData.get(world, false);
            if (wd != null) {
                PlayerData.PlayerSave ps = wd.playerSaveData.get(id);
                if (ps != null) {
                    List<String> researchedPackages = ps.packagesResearched;
                    if (researchedPackages.contains(cpName)) {
                        sender.sendMessage(new TextComponentTranslation("commands.packagemod.true"));
                    } else sender.sendMessage(new TextComponentTranslation("commands.packagemod.false"));
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
        }
        return completionList;
    }
}
