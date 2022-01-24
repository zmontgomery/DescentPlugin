package descent;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.mojang.datafixers.util.Pair;
import descent.champions.Champ;
import descent.commands.GetItem;
import descent.commands.PatchNotes;
import descent.commands.SetChamp;
import descent.commands.SetGame;
import descent.commands.SetTeam;
import descent.commands.Start;
import descent.commands.Stats;
import descent.commands.StopGame;
import descent.gamemodes.Default;
import descent.gamemodes.Gamemode;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EnumItemSlot;

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
		getCommand("getitem").setExecutor(new GetItem());
		getCommand("patchnotes").setExecutor(new PatchNotes());

	}

	@Override
	public void onDisable() {
		gamemode.stop();
	}
	
	public static void sendEquipmentInvisiblePacket(Player player, boolean isInvisible) {
		List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> l =
				new LinkedList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>(){private static final long serialVersionUID = 1L;};

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
				
		l.add(new Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>
		(EnumItemSlot.a, CraftItemStack.asNMSCopy(mainhand)));
		l.add(new Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>
		(EnumItemSlot.c, CraftItemStack.asNMSCopy(boots)));
		l.add(new Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>
		(EnumItemSlot.d, CraftItemStack.asNMSCopy(legs)));
		l.add(new Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>
		(EnumItemSlot.e, CraftItemStack.asNMSCopy(chest)));
		l.add(new Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>
		(EnumItemSlot.f, CraftItemStack.asNMSCopy(helm)));

		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(player.getEntityId(), l);

		for (Player otherPlayer : player.getWorld().getPlayers()) {
			if(!otherPlayer.equals(player)) {
				PlayerConnection conn = ((CraftPlayer) otherPlayer).getHandle().b;
				conn.sendPacket(packet);
			}
		}
	}

}
