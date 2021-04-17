package descentmodel;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hunter extends Champ{
    public static final int MAX_HEALTH = 150;
    public static final String CHAMP_NAME = "Hunter";
    public static final float MOVE_SPEED = 0.25f;
    public static final ItemStack[] ITEMS = new ItemStack[]{new ItemStack(Material.BOW)};
	public static final ItemStack[] CLOTHES = new ItemStack[]{null, null, null, new ItemStack(Material.CHAINMAIL_HELMET)};
	public static final ItemStack LEFT_HAND = new ItemStack(Material.ARROW, 64);

    public Hunter(Player player){
        super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
    }

    @Override
    public void abilityRanged(Champ champ) {
        champ.takeDamage(85);
    }
}
