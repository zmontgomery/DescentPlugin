package descent;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.mojang.datafixers.util.Pair;
import descent.champions.Champ;
import descent.commands.SetChamp;
import descent.commands.SetGame;
import descent.commands.SetTeam;
import descent.commands.Start;
import descent.commands.Stats;
import descent.commands.StopGame;
import descent.gamemodes.Default;
import descent.gamemodes.Gamemode;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class Main extends JavaPlugin {
	
	public static Gamemode gamemode;

	@Override
	public void onEnable() {
		gamemode = new Default();

		getServer().getPluginManager().registerEvents(new EventListener(), this);

		getCommand("start").setExecutor(new Start());
		getCommand("stats").setExecutor(new Stats());
		getCommand("setgame").setExecutor(new SetGame());
		getCommand("setchamp").setExecutor(new SetChamp());
		getCommand("setteam").setExecutor(new SetTeam());
		getCommand("stopgame").setExecutor(new StopGame());

	}

	@Override
	public void onDisable() {
		gamemode.stop();
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
