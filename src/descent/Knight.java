package descentmodel;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Knight extends Champ{
    public static final int MAX_HEALTH = 300;
    public static final String CHAMP_NAME = "Knight";
    public static final int MAX_SHIELD_HEALTH = 300;
    public static final float MOVE_SPEED = 0.23f;
    public static final ItemStack[] ITEMS = new ItemStack[]{new ItemStack(Material.IRON_SWORD)};
	public static final ItemStack[] CLOTHES = new ItemStack[]{new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)};
	public static final ItemStack LEFT_HAND = new ItemStack(Material.SHIELD);
    public static final float MELEE_COOLDOWN = 0.4f;
    private int currentShieldHealth;

    public Knight(Player player){
        super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
        this.currentShieldHealth = Knight.MAX_SHIELD_HEALTH;
    }

    public int getCurrentShieldHealth() {
        return this.currentShieldHealth;
    }

    @Override
    public void abilityMelee(Champ champ) {
        if(PLAYER.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
				
			PLAYER.playSound(champ.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
			PLAYER.playSound(champ.getPlayer().getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			
			champ.takeDamage(45);
		}
    }
    
    @Override
    public void takeDamage(int amount) {
        if(this.currentShieldHealth > 0){
            this.currentShieldHealth -= amount;
            if(this.currentShieldHealth < 0){
                this.currentShieldHealth = 0;
            }
        } else{
            this.currentHealth -= amount;
            if(this.currentHealth < 0){
                this.currentHealth = 0;
            }
            updatePlayerHealth();
        }
        
    }
    
    @Override
    public String toString() {
        return super.toString() + " SHIELD=" + this.currentShieldHealth;
    }
}
