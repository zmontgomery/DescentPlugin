package descent.threads;

import org.bukkit.entity.Player;

import descent.champions.Champ;
import descent.champions.Generic;
import descent.champions.Knight;

public class KnightShieldRegen implements Runnable {
	private Player player;
	private Champ champ;

	public KnightShieldRegen(Player player, Champ champ) {
		this.player = player;
		this.champ = champ;

	}

	@Override
	public void run() {
		while (Champ.getChamp(player) != null && !(Champ.getChamp(player) instanceof Generic) && Champ.getChamp(player) == this.champ) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// squash
			}
			if(Champ.getChamp(player) instanceof Knight) {
				Knight champ = (Knight) Champ.getChamp(player);
				if (champ != null) {
					champ.regenShield(2);
				}
			}
		}
	}
}
