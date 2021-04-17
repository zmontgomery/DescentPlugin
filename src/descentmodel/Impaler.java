package descentmodel;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Impaler extends Champ{
    public static final int MAX_HEALTH = 200;
    public static final String CHAMP_NAME = "Impaler";
    public static final int DODGE_CHANCE = 25;
    public static final float MOVE_SPEED = 0.27f;
    public static final ItemStack[] ITEMS = new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)};
	public static final ItemStack[] CLOTHES = new ItemStack[]{null, null, null, new ItemStack(Material.LEATHER_HELMET)};
	public static final ItemStack LEFT_HAND = new ItemStack(Material.WOODEN_SWORD);
    //private long knifeCooldownTimer = System.currentTimeMillis();

    public Impaler(Player player){
        super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
    }

    @Override
    public void shoot() {
        //shoot whatever projectile this champ should shoot
        //sets shooter as well

    }

    @Override
    public void abilityRanged(Champ champ) {
        champ.takeDamage(20);
    }

    @Override
    public void takeDamage(int amount) {
        int randomInt = Champ.RNG.nextInt(100);
        if(randomInt > DODGE_CHANCE){
            currentHealth -= amount;
            updatePlayerHealth();
        } else {
            System.out.println("DODGED!");
        }
    }
}
