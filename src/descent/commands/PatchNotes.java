package descent.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PatchNotes implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("patchnotes")) {
			Player player = Bukkit.getPlayerExact(sender.getName());
			player.sendMessage("RELEASE v0.1.1: +Buff  -Nerf  xRework");
			player.sendMessage("-Knight");
			player.sendMessage("xNinja");
			player.sendMessage("+Monkey");
			player.sendMessage("-Beserker");
			player.sendMessage("+xTrainer");
			player.sendMessage("-xFighter");
		}
		return true;
	}
}