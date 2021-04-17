package descentmodel;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Beserker extends Champ{
    public static final int MAX_HEALTH = 175;
    public static final String CHAMP_NAME = "Beserker";
    public static final float MOVE_SPEED = 0.26f;
    public static final ItemStack[] ITEMS = new ItemStack[]{new ItemStack(Material.GOLDEN_AXE)};
	public static final ItemStack[] CLOTHES = new ItemStack[]{new ItemStack(Material.CHAINMAIL_BOOTS), null, null, new ItemStack(Material.CHAINMAIL_HELMET)};
	public static final ItemStack LEFT_HAND = null;

    public Beserker(Player player){
        super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
    }

    @Override
    public void abilityMelee(Champ champ) {
        champ.takeDamage(75);
    }
}
