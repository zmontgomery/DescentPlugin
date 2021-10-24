package descent.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import descent.champions.Champ;

public class TeamDeathmatch implements Gamemode {

	private String name;

	private Location redSpawn;
	private Location blueSpawn;

	private ScoreboardManager manager;
	private Scoreboard board;
	private Team red;
	private Team blue;

	private final float RESPAWN_TIME = 5.0f;

	@Override
	public void start() {
		manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();

		for(Team team : board.getTeams()) {
			team.unregister();
		}

		name = "Team Deathmatch";
		World world = Bukkit.getWorld("world");
		redSpawn = new Location(world, -55.5, 23, 100.5);
		blueSpawn = new Location(world, 56.5, 23, 100.5);

		blue = board.registerNewTeam("blue");
		red = board.registerNewTeam("red");
//		Team spec = board.registerNewTeam("spec");

		blue.setColor(ChatColor.BLUE);
		blue.setAllowFriendlyFire(false);
		blue.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
		blue.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
		blue.setDisplayName("Blue");
		blue.setCanSeeFriendlyInvisibles(true);

		red.setColor(ChatColor.RED);
		red.setAllowFriendlyFire(false);
		red.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
		red.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
		red.setDisplayName("Red");
		red.setCanSeeFriendlyInvisibles(true);

//		spec.setColor(ChatColor.WHITE);
//		spec.setAllowFriendlyFire(false);
//		spec.setNameTagVisibility(NameTagVisibility.NEVER);
//		spec.setDisplayName("Spectator");
//		spec.setCanSeeFriendlyInvisibles(true);

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.teleport(player.getWorld().getSpawnLocation().add(new Location(player.getWorld(), 0.5, 0, 0.5)));
			Champ champ = Champ.getChamp(player);
			champ.teamSelect();
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public Location respawnLocation(Player player) {
		Location spawnLoc = null;
		if (board.getEntryTeam(player.getName()).getName().equals("blue")) {
			spawnLoc = blueSpawn;
		} else if (board.getEntryTeam(player.getName()).getName().equals("red")) {
			spawnLoc = redSpawn;
		}
		return spawnLoc;
	}

	@Override
	public float getRespawnTime() {
		return RESPAWN_TIME;
	}

	@Override
	public void joinTeam(String team, Player player) {
		if (team.equals("red")) {
			red.addEntry(player.getName());
		} else if (team.equals("blue")) {
			blue.addEntry(player.getName());
		}
		return;
	}

	@Override
	public void stop() {
		return;
	}
	
}
