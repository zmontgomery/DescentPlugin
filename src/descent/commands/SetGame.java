package descent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import descent.Main;
import descent.gamemodes.Deathmatch;
import descent.gamemodes.KingOfTheHill;
import descent.gamemodes.Testing;

public class SetGame implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("setgame")) {
			if (args.length == 1) {
				if(args[0].equals("deathmatch")) {
					Main.gamemode = new Deathmatch();
				}
				else if(args[0].equals("koth")) {
					Main.gamemode = new KingOfTheHill();
				} else if(args[0].equals("testing")) {
					Main.gamemode = new Testing();
				}
			} 
		}
		return true;
	}
}
