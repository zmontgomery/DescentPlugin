package descent.gamemodes;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import descent.Main;
import descent.champions.Champ;

public class KingOfTheHill extends Gamemode {

	public static final float RESPAWN_TIME = 4.0f;
	public static final double POINTS_TO_WIN = 1000.0f;
	public static final double POINTS_FOR_CAPTURE = 100.0f;
	public static final int CONTROL_X = 0;
	public static final int CONTROL_Y = 20;
	public static final int CONTROL_Z = 100;
	public static final int CAP_TICK_RATE = 15;
	public static final int SCORE_TICK_RATE = 10;

	private String name;
	private Location redSpawn;
	private Location blueSpawn;
	private ScoreboardManager manager;
	private Scoreboard board;
	private Team red;
	private Team blue;
	private Team owner;
	private String ownerName;
	private Location controlPoint;
	private Team winner;

	private double blueProgress;
	private double redProgress;
	private double captureStatus;

	@Override
	public void start() {
		Bukkit.getScheduler().cancelTasks(Main.getPlugin(Main.class));
		blueProgress = 0.0f;
		redProgress = 0.0f;
		captureStatus = 0.0f;
		owner = null;
		winner = null;
		manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();
		name = "King of the Hill";
		World world = Bukkit.getWorld("world");
		world.getBlockAt(new Location(world, CONTROL_X, CONTROL_Y, CONTROL_Z))
				.setType(Material.BLACK_GLAZED_TERRACOTTA);
		redSpawn = new Location(world, -55.5, 23, 100.5);
		blueSpawn = new Location(world, 56.5, 23, 100.5);
		controlPoint = new Location(world, CONTROL_X, CONTROL_Y, CONTROL_Z).add(new Location(world, 0.5, 0, 0.5));
		Objective sidebar = board.registerNewObjective("cp", "dummy", "Control Point");
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
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

//		Score title = sidebar.getScore(ChatColor.YELLOW + "Reactor: " + ownerName);
//		Score blueScore = sidebar.getScore(ChatColor.BLUE + "Blue: " + (int)(blueProgress / POINTS_TO_WIN));
//		Score redScore = sidebar.getScore(ChatColor.RED + "Red: " + (int)(redProgress / POINTS_TO_WIN));
//
//		title.setScore(3);
//		blueScore.setScore(2);
//		redScore.setScore(1);

		for (Player player : Bukkit.getOnlinePlayers()) {
			Champ champ = Champ.getChamp(player);
			champ.teamSelect();
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
			@Override
			public void run() {
				// SCORE ERASE
				board.resetScores(ChatColor.YELLOW + "Reactor: " + ownerName);
				board.resetScores(ChatColor.BLUE + "Blue: " + (int) ((blueProgress / POINTS_TO_WIN) * 100) + "%");
				board.resetScores(ChatColor.RED + "Red: " + (int) ((redProgress / POINTS_TO_WIN) * 100) + "%");

				// FOR SIDEBAR SCOREBOARD ERROR
				if (owner != null) {
					ownerName = owner.getDisplayName();
				} else {
					ownerName = "neutral";
				}

				// CAP CHECKER
				int reds = 0;
				int blues = 0;
				Collection<Entity> entities = world.getNearbyEntities(controlPoint, 5, 5, 5);
				for (Entity entity : entities) {
					if (entity instanceof Player) {
						Player player = (Player) entity;
						if (board.getEntryTeam(player.getName()).equals(blue)) {
							blues++;
						} else if (board.getEntryTeam(player.getName()).equals(red)) {
							reds++;
						}
					}
				}
				
				int difference = reds - blues;
				if((owner != red && difference > 0) || (owner != blue && difference < 0) || (owner == red && captureStatus < 0) || (owner == blue && captureStatus > 0)) {
					captureStatus += difference * CAP_TICK_RATE;
				}

				// CAP RUNOUT
				if (entities.size() == 0) {
					if (captureStatus > 0) {
						captureStatus -= CAP_TICK_RATE;
					} else if (captureStatus < 0) {
						captureStatus += CAP_TICK_RATE;
					}
				}
				
				// CAPTURE EVENT
				if (captureStatus >= POINTS_FOR_CAPTURE) {
					owner = red;
					captureStatus = 0;
					Bukkit.broadcastMessage(ChatColor.RED + "Red has taken the Nether Reactor!");
					world.getBlockAt(new Location(world, CONTROL_X, CONTROL_Y, CONTROL_Z))
							.setType(Material.RED_GLAZED_TERRACOTTA);
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
					}
				} else if (captureStatus <= -POINTS_FOR_CAPTURE) {
					owner = blue;
					captureStatus = 0;
					Bukkit.broadcastMessage(ChatColor.BLUE + "Blue has taken the Nether Reactor!");
					world.getBlockAt(new Location(world, CONTROL_X, CONTROL_Y, CONTROL_Z))
							.setType(Material.BLUE_GLAZED_TERRACOTTA);
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
					}
				}

				// OVERTIME SCORE INCREASE
				if (owner == blue) {
					blueProgress += SCORE_TICK_RATE;
					if(blueProgress > POINTS_TO_WIN) {
						blueProgress = POINTS_TO_WIN;
					}
				} else if (owner == red) {
					redProgress += SCORE_TICK_RATE;
					if(redProgress > POINTS_TO_WIN) {
						redProgress = POINTS_TO_WIN;
					}
				}

				// WIN CHECKER / EXECUTER
				if (blueProgress >= POINTS_TO_WIN && captureStatus <= 0 && owner == blue && reds == 0) {
					winner = blue;
				}
				if (redProgress >= POINTS_TO_WIN && captureStatus >= 0 && owner == red && blues == 0) {
					winner = red;
				}
				if (winner != null) {
					blueProgress = 0;
					redProgress = 0;
					owner = null;
					captureStatus = 0;
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.getInventory().clear();
						player.setGameMode(GameMode.SURVIVAL);
						player.teleport(player.getWorld().getSpawnLocation());
					}
					if (winner == red) {
						Bukkit.broadcastMessage(ChatColor.RED + "Red has maintained control of the reactor!");
						Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red wins!");
					} else if (winner == blue) {
						Bukkit.broadcastMessage(ChatColor.BLUE + "Blue has maintained control of the reactor!");
						Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue wins!");
					}
					Bukkit.getScheduler().cancelTasks(Main.getPlugin(Main.class));
					winner = null;
					stop();
				}

				// SCORESET
				Score title = sidebar.getScore(ChatColor.YELLOW + "Reactor: " + ownerName);
				Score blueScore = sidebar
						.getScore(ChatColor.BLUE + "Blue: " + (int) ((blueProgress / POINTS_TO_WIN) * 100) + "%");
				Score redScore = sidebar
						.getScore(ChatColor.RED + "Red: " + (int) ((redProgress / POINTS_TO_WIN) * 100) + "%");
				title.setScore(3);
				blueScore.setScore(2);
				redScore.setScore(1);
			}
		}, 0L, 15L);

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
		} else {
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
		preInit();
		Bukkit.getScheduler().cancelTasks(Main.getPlugin(Main.class));
		blueProgress = 0.0f;
		redProgress = 0.0f;
		captureStatus = 0.0f;
		owner = null;
		winner = null;
		board.resetScores(ChatColor.YELLOW + "Reactor: " + ownerName);
		board.resetScores(ChatColor.BLUE + "Blue: " + (int) ((blueProgress / POINTS_TO_WIN) * 100) + "%");
		board.resetScores(ChatColor.RED + "Red: " + (int) ((redProgress / POINTS_TO_WIN) * 100) + "%");
	}

}
