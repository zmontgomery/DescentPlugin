package descent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandChamp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("champion")) {
			if((args.length == 0)) {
				Bukkit.broadcastMessage("Syntax: /champion <champ-name>");
			} else if(args[0].equalsIgnoreCase("knife")) {
				ChampList.setChamp(Bukkit.getPlayerExact(sender.getName()), ChampList.knife);
			} else if(args[0].equalsIgnoreCase("sword")) {
				ChampList.setChamp(Bukkit.getPlayerExact(sender.getName()), ChampList.sword);
			} else if(args[0].equalsIgnoreCase("axe")) {
				ChampList.setChamp(Bukkit.getPlayerExact(sender.getName()), ChampList.axe);
			} else {
				Bukkit.broadcastMessage("Please enter a champion.");
			}
		}
		return true;
	}

}
