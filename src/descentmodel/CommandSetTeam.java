package descentmodel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSetTeam implements CommandExecutor {
	
	public static boolean gameStatus = false;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("setteam")) {
			if(args.length == 0) {
				
				Bukkit.getPlayerExact(sender.getName()).sendMessage("Syntax: /setteam <team>");
				ControlPointGamemode.startControl(Bukkit.getPlayerExact(sender.getName()));
				Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), Main.controlX, Main.controlY, Main.controlZ)).setType(Material.BLACK_GLAZED_TERRACOTTA);
				PlayerTeams.openSpawnDoors();
				
			} else {
				
				PlayerTeams.addToTeam(Bukkit.getPlayerExact(sender.getName()), args[0]);
				
			}
		}
		return true;
	}
}
