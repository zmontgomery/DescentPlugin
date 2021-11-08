package descent.champions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Generic extends Champ{

	public Generic(Player player) {
		super(player, "NONE", 0.2f, 2000, 0, new ItemStack[0], new ItemStack[0], null, null, 1);
	}
	
	@Override
	public boolean takeDamage(double amount) {
		return false;
	}
	
	@Override
	public void updatePlayerHealth() {
		return;
	}
}
