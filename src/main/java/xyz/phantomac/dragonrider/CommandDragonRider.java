package xyz.phantomac.dragonrider;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandDragonRider extends CommandBase {

    private final DragonRider mod;

    public CommandDragonRider(DragonRider mod) {
        this.mod = mod;
    }

    @Override
    public String getCommandName() {
        return "dragonrider";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dragonrider [circle|stop] - toggles or changes dragon behavior.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            mod.toggle();
        } else if ("circle".equalsIgnoreCase(args[0])) {
            mod.startCircling();
        } else if ("stop".equalsIgnoreCase(args[0])) {
            mod.stopCircling();
        } else {
            sender.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "Usage: /dragonrider [circle|stop]"
            ));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
