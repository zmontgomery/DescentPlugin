package descent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandResetNexus implements CommandExecutor {
	
	public static int nexusHealth = 75;
	public static int inhibHealth = 30;
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.broadcastMessage("Nexus is reset.");
        nexusHealth = 75;
        inhibHealth = 30;
        return true;
    }
}