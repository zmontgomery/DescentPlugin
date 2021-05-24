package descent.threads;

import org.bukkit.entity.Player;

import descent.champions.Champ;
import descent.champions.Generic;
import descent.champions.Fighter;

public class LeeEnergyRegen implements Runnable {
	private Player player;
	private Champ champ;

	public LeeEnergyRegen(Player player, Champ champ) {
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
			Fighter champ = (Fighter) Champ.getChamp(player);
			if (champ != null) {
				champ.regenEnergy(1);
			}
		}
	}
}
