package descentmodel;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Deputy extends Champ{
    public static final int MAX_HEALTH = 200;
    public static final String CHAMP_NAME = "Deputy";
    public static final float MOVE_SPEED = 0.25f;
    public static final ItemStack[] ITEMS = new ItemStack[]{new ItemStack(Material.NETHERITE_HOE)};
	public static final ItemStack[] CLOTHES = new ItemStack[]{null, new ItemStack(Material.CHAINMAIL_LEGGINGS), null, null};
	public static final ItemStack LEFT_HAND = null;

    public Deputy(Player player){
        super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
    }

    @Override
    public void abilityRanged(Champ champ) {
        champ.heal(50);
    }
}
