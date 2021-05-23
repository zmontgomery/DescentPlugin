package descent.threads;

import org.bukkit.entity.Player;

public class FoodSet implements Runnable {
	private Player player;

	public FoodSet(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		while (player.isOnline()) {
			try {
				synchronized (player) {
					player.wait();
				}
				Thread.sleep(350);
			} catch (InterruptedException e) {
				// squash
			}

			player.setFoodLevel(5);
		}
	}

}
