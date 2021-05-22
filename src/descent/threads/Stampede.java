package descent.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import descent.champions.Champ;
import descent.champions.Knight;

public class Stampede implements Runnable{
	
	private Player player;
	private Champ champ;
	
	private long timeAtRunStart;
	
	public Stampede(Player player, Champ champ) {
		this.player = player;
		this.champ = champ;
		
		timeAtRunStart = 0;
	}

	@Override
	public void run() {
		while (Champ.getChamp(player) != null && Champ.getChamp(player) == this.champ) {
			timeAtRunStart = System.currentTimeMillis();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// squash
			}
			System.out.println(Math.abs(player.getVelocity().getX()) + Math.abs(player.getVelocity().getZ()) + "");
			while(Math.abs(player.getVelocity().getX()) + Math.abs(player.getVelocity().getZ()) > 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// squash
				}
				float walk = (System.currentTimeMillis() - timeAtRunStart) / 10000;
				Bukkit.broadcastMessage(walk + "");
				player.setWalkSpeed(player.getWalkSpeed() + (walk));
			}
			player.setWalkSpeed(Knight.MOVE_SPEED);
		}
	}
	
}
