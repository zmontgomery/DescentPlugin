package descent.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import descent.champions.Champ;

public class SetTeam implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("setteam")) {
			Player player = Bukkit.getPlayerExact(sender.getName());
			Champ champ = Champ.getChamp(player);
			champ.teamSelect();
		}
		return true;
	}
}