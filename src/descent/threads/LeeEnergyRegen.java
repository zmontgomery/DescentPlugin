package descent.threads;

import org.bukkit.entity.Player;

import descent.champions.Champ;
import descent.champions.ShaneLee;

public class LeeEnergyRegen implements Runnable {
	private Player player;

	public LeeEnergyRegen(Player player) {
		this.player = player;
		
	}

	@Override
	public void run() {
		while (Champ.getChamp(player) instanceof ShaneLee) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// squash
			}
			ShaneLee champ = (ShaneLee)Champ.getChamp(player);
			if(champ != null) {
				champ.regenEnergy(20);
			}
		}
	}

}
