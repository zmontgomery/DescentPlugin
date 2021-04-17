package descentmodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Champ{

    private static Map<Player, Champ> players = new HashMap<>();
    public static final Random RNG = new Random();

    protected final Player PLAYER;
    protected final String NAME;
    protected final String CHAMP_NAME;
    protected final int MAX_HEALTH;
    protected final float MOVE_SPEED;
    protected final ItemStack[] ITEMS;
	protected final ItemStack[] CLOTHES;
	protected final ItemStack LEFT_HAND;
    protected int currentHealth;
    
    protected Champ(Player player, String champName, float moveSpeed, int maxHealth, ItemStack[] items, ItemStack[] clothes, ItemStack leftHand){
        Champ.addChamp(player, this);
        this.PLAYER = player;
        this.NAME = player.getName();
        this.CHAMP_NAME = champName;
        this.MOVE_SPEED = moveSpeed;
        this.MAX_HEALTH = maxHealth;
        this.currentHealth = maxHealth;
        this.ITEMS = items;
        this.CLOTHES = clothes;
        this.LEFT_HAND = leftHand;
    }

    public static void addChamp(Player player, Champ champ){
        players.put(player, champ);
    }
    
    public static Champ getChamp(Player player){
        if(players.containsKey(player)){
            return players.get(player);
        } else {
            return null;
        }
    }

    public static void clearChamp(Player player){
        Champ.players.remove(player);
    }

    public int getCurrentHealth(){
        return this.currentHealth;
    }

    public String getName(){
        return this.NAME;
    }

    public String getChampName(){
        return this.CHAMP_NAME;
    }

    public Player getPlayer() {
        return this.PLAYER;
    }

    public float getMoveSpeed() {
        return MOVE_SPEED;
    }
    
    public void shoot(){
        return;
    }

    public void abilityMelee(Champ champ){
        return;
    }
    
    public void abilityRanged(Champ champ){
        return;
    }

    public void heal(int amount){
        this.currentHealth += amount;
        if(this.currentHealth > this.MAX_HEALTH){
            this.currentHealth = MAX_HEALTH;
        }
        updatePlayerHealth();
    }

    public void takeDamage(int amount){
        this.currentHealth -= amount;
        if(this.currentHealth < 0){
            this.currentHealth = 0;
        }
        updatePlayerHealth();
    }

    public void updatePlayerHealth(){
        double ratio = (double) this.currentHealth / (double) MAX_HEALTH;
        double converted = ratio * 20;
        this.PLAYER.setHealth((int)converted);
    }

    @Override
    public String toString() {
        return this.NAME + "(" + this.CHAMP_NAME + ")" + ": HEALTH=" + this.currentHealth;
    }
}
