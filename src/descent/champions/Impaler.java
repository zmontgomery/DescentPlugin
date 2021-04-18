package descent.champions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import descent.Main;

public class Impaler extends Champ{
    public static final int MAX_HEALTH = 200;
    public static final String CHAMP_NAME = "Impaler";
    public static final float MOVE_SPEED = 0.27f;
    public static final float KNIFE_COOLDOWN = 0.75f;
    public static final int KNIFE_DAMAGE = 36;
    public static final ItemStack[] ITEMS = new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)};
	public static final ItemStack[] CLOTHES = new ItemStack[]{null, null, null, new ItemStack(Material.LEATHER_HELMET)};
	public static final ItemStack LEFT_HAND = new ItemStack(Material.WOODEN_SWORD);

    public Impaler(Player player){
        super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
    }

    @Override
    public void use(Action click) {
        if(PLAYER.getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD && (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)) {
            Arrow knife1 = PLAYER.getWorld().spawnArrow(new Location(PLAYER.getWorld(), PLAYER.getLocation().getX(), PLAYER.getLocation().getY() + PLAYER.getEyeHeight(), PLAYER.getLocation().getZ()), PLAYER.getLocation().getDirection(), 3, 0);
            knife1.setCustomName(PLAYER.getName());	
            knife1.setBounce(false);
            PLAYER.playSound(PLAYER.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.5f, 1f);
            
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
                @Override
                public void run() {
                    Arrow knife2 = PLAYER.getWorld().spawnArrow(new Location(PLAYER.getWorld(), PLAYER.getLocation().getX(), PLAYER.getLocation().getY() + PLAYER.getEyeHeight(), PLAYER.getLocation().getZ()), PLAYER.getLocation().getDirection(), 3, 0);
                    knife2.setCustomName(PLAYER.getName());
                    knife2.setBounce(false);
                    PLAYER.playSound(PLAYER.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.5f, 1f);
                }
            }, 3L);
        }
    }

    @Override
    public void abilityRanged(Champ champ, Projectile projectile) {
        if(projectile instanceof Arrow){
            champ.takeDamage(KNIFE_DAMAGE);
        }
    }
}
