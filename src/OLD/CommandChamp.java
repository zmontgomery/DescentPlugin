package OLD;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandChamp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Bukkit.getPlayerExact(sender.getName()).sendMessage("Dont even try it");
		return true;
		
	}
	
//	@Override
//	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		if(label.equalsIgnoreCase("champion")) {
//			if((args.length == 0)) {
//				Bukkit.getPlayerExact(sender.getName()).sendMessage("Syntax: /champion <champ-name>");
//			} else if(args[0].equalsIgnoreCase("knife")) {
//				ChampList.setChamp(Bukkit.getPlayerExact(sender.getName()), ChampList.knife);
//			} else if(args[0].equalsIgnoreCase("sword")) {
//				ChampList.setChamp(Bukkit.getPlayerExact(sender.getName()), ChampList.sword);
//			} else if(args[0].equalsIgnoreCase("axe")) {
//				ChampList.setChamp(Bukkit.getPlayerExact(sender.getName()), ChampList.axe);
//			} else if(args[0].equalsIgnoreCase("gun")) {
//				ChampList.setChamp(Bukkit.getPlayerExact(sender.getName()), ChampList.gun);
//			} else {
//				Bukkit.getPlayerExact(sender.getName()).sendMessage("Please enter a valid champion.");
//			}
//		}
//		return true;
//	}

}
