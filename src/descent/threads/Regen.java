package descent.threads;

import org.bukkit.entity.Player;

import descent.champions.Champ;

public class Regen implements Runnable {
	private Player player;
	private Champ champ;

	public Regen(Player player) {
		this.player = player;
		this.champ = Champ.getChamp(player);

	}

	@Override
	public void run() {
		while (player.isOnline()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// squash
			}
			if(champ != null) {
				champ.heal(3);
			}
		}
	}

}
