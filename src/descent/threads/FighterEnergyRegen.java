package descent.threads;

import org.bukkit.entity.Player;

import descent.champions.Champ;
import descent.champions.Generic;
import descent.champions.Fighter;

public class FighterEnergyRegen implements Runnable {
	private Player player;
	private Champ champ;

	public FighterEnergyRegen(Player player, Champ champ) {
		this.player = player;
		this.champ = champ;

	}

	@Override
	public void run() {
		while (Champ.getChamp(player) != null && !(Champ.getChamp(player) instanceof Generic) && Champ.getChamp(player) == this.champ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// squash
			}
			if(Champ.getChamp(player) instanceof Fighter) {
				Fighter champ = (Fighter) Champ.getChamp(player);
				if (champ != null) {
					champ.regenEnergy(1);
				}
			}
		}
	}
}
