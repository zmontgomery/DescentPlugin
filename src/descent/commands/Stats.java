package descent.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import descent.champions.Champ;

public class Stats implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = Bukkit.getPlayerExact(sender.getName());
		player.sendMessage(Champ.getChamp(player).toString());
		return true;
	}
}
