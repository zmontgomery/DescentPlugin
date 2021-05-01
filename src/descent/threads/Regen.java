package descent.threads;

import org.bukkit.entity.Player;

import descent.champions.Champ;

public class Regen implements Runnable {
	private Player player;

	public Regen(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		while (player.isOnline()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// squash
			}
			Champ champ = Champ.getChamp(player);
			if(champ != null) {
				champ.heal(3.0);
			}
		}
	}

}
