package descent;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.mojang.datafixers.util.Pair;
import descent.champions.Champ;
import descent.commands.CommandSetTeam;
import descent.commands.CommandStats;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class Main extends JavaPlugin {

	public static int controlX;
	public static int controlY;
	public static int controlZ;

	public static int blueX;
	public static int blueY;
	public static int blueZ;

	public static int redX;
	public static int redY;
	public static int redZ;

	public static int blueDoorX1;
	public static int blueDoorY1;
	public static int blueDoorZ1;

	public static int blueDoorX2;
	public static int blueDoorY2;
	public static int blueDoorZ2;

	public static int redDoorX1;
	public static int redDoorY1;
	public static int redDoorZ1;

	public static int redDoorX2;
	public static int redDoorY2;
	public static int redDoorZ2;

	@Override
	public void onEnable() {

		getServer().getPluginManager().registerEvents(new EventListener(), this);

		getCommand("setteam").setExecutor(new CommandSetTeam());
		getCommand("stats").setExecutor(new CommandStats());

		this.saveDefaultConfig();

		FileConfiguration config = this.getConfig();
		controlX = config.getInt("descentconfig.controlpoint.x");
		controlY = config.getInt("descentconfig.controlpoint.y");
		controlZ = config.getInt("descentconfig.controlpoint.z");

		blueX = config.getInt("descentconfig.blue.x");
		blueY = config.getInt("descentconfig.blue.y");
		blueZ = config.getInt("descentconfig.blue.z");

		redX = config.getInt("descentconfig.red.x");
		redY = config.getInt("descentconfig.red.y");
		redZ = config.getInt("descentconfig.red.z");

		blueDoorX1 = config.getInt("descentconfig.bluedoor.x1");
		blueDoorY1 = config.getInt("descentconfig.bluedoor.y1");
		blueDoorZ1 = config.getInt("descentconfig.bluedoor.z1");

		blueDoorX2 = config.getInt("descentconfig.bluedoor.x2");
		blueDoorY2 = config.getInt("descentconfig.bluedoor.y2");
		blueDoorZ2 = config.getInt("descentconfig.bluedoor.z2");

		redDoorX1 = config.getInt("descentconfig.reddoor.x1");
		redDoorY1 = config.getInt("descentconfig.reddoor.y1");
		redDoorZ1 = config.getInt("descentconfig.reddoor.z1");

		redDoorX2 = config.getInt("descentconfig.reddoor.x2");
		redDoorY2 = config.getInt("descentconfig.reddoor.y2");
		redDoorZ2 = config.getInt("descentconfig.reddoor.z2");

		PlayerTeams.closeSpawnDoors();

		PlayerTeams.registerTeams();

	}

	@Override
	public void onDisable() {

		PlayerTeams.closeSpawnDoors();

		PlayerTeams.unregisterTeams();

	}
	
	public static void sendEquipmentInvisiblePacket(Player player, boolean isInvisible) {
		List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> l =
				new LinkedList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>>(){private static final long serialVersionUID = 1L;};

		ItemStack boots = Champ.getChamp(player).CLOTHES[0];
		ItemStack legs = Champ.getChamp(player).CLOTHES[1];	
		ItemStack chest = Champ.getChamp(player).CLOTHES[2];
		ItemStack helm = Champ.getChamp(player).CLOTHES[3];
		ItemStack mainhand = Champ.getChamp(player).ITEMS[0];
		
		if(isInvisible) {
			boots = null;
			legs = null;
			chest = null;
			helm = null;
			mainhand = null;
		}
				
		l.add(new Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>
		(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(boots)));
		l.add(new Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>
		(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(legs)));
		l.add(new Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>
		(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(chest)));
		l.add(new Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>
		(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(helm)));
		l.add(new Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>
		(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(mainhand)));

		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(player.getEntityId(), l);

		for (Player otherPlayer : player.getWorld().getPlayers()) {
			if(!otherPlayer.equals(player)) {
				PlayerConnection conn = ((CraftPlayer) otherPlayer).getHandle().playerConnection;
				conn.sendPacket(packet);
			}
		}
	}

}
