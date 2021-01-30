package descent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_16_R3.DoubleBlockFinder.BlockType;

import java.util.HashMap;

public class DescentListener implements Listener{
	
	public HashMap<Player, Long> arrowCooldown = new HashMap<Player, Long>();
	public HashMap<Player, Long> jumpCooldown = new HashMap<Player, Long>();
	public HashMap<Player, Long> hitCooldown = new HashMap<Player, Long>();
	
	public Inventory inv;
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
		event.setJoinMessage("Welcome, " + event.getPlayer().getName() + ", to the Descent Project!");
		arrowCooldown.put(event.getPlayer(), System.currentTimeMillis());
		jumpCooldown.put(event.getPlayer(), System.currentTimeMillis());
		hitCooldown.put(event.getPlayer(), System.currentTimeMillis());
		event.getPlayer().setMaximumNoDamageTicks(0);
		event.getPlayer().setLevel(200);
		event.getPlayer().setFoodLevel(5);
		
		inv = Bukkit.createInventory(null, 9, "Choose your kit");
		
		inv.addItem(new ItemStack(Material.ARROW));
		inv.addItem(new ItemStack(Material.IRON_SWORD));
		
		event.getPlayer().openInventory(inv);
    }
	@EventHandler
	
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		event.setQuitMessage("Goodbye, " + event.getPlayer().getName() + "...");
		arrowCooldown.remove(event.getPlayer());
		jumpCooldown.remove(event.getPlayer());
		hitCooldown.remove(event.getPlayer());
    }
	@EventHandler
	public void ArrowShoot(PlayerInteractEvent event) {
		
		if (event.getItem() != null && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getItem().getType() == Material.ARROW) {
			
			long currentTime = System.currentTimeMillis();
			long timeOfSwing = arrowCooldown.get(event.getPlayer());
			
			if(currentTime - timeOfSwing > 250) {
				//ABILITY CODE GOES HERE
				Location pl = new Location(event.getPlayer().getWorld(), event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + event.getPlayer().getEyeHeight(), event.getPlayer().getLocation().getZ());
				event.getPlayer().getWorld().spawnArrow(pl, event.getPlayer().getLocation().getDirection(), 4, 0);
				arrowCooldown.replace(event.getPlayer(), currentTime);
			}
		}
	}
	@EventHandler
	public void SwordDoubleJump(PlayerInteractEvent event) {

		if(event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem().getType() == Material.IRON_SWORD) {
			
			long currentTime = System.currentTimeMillis();
			long timeOfJump = jumpCooldown.get(event.getPlayer());
			
			if(currentTime - timeOfJump > 6000) {
				event.getPlayer().setVelocity(new Vector(event.getPlayer().getVelocity().getX() + event.getPlayer().getLocation().getDirection().getX()*1.3, event.getPlayer().getVelocity().getY() + event.getPlayer().getLocation().getDirection().getY()*1.3, event.getPlayer().getVelocity().getZ() + event.getPlayer().getLocation().getDirection().getZ()*1.3));
				jumpCooldown.replace(event.getPlayer(), currentTime);
			} else {
				event.getPlayer().sendMessage(Double.toString(6 - ((currentTime - timeOfJump) * 1.0) / 1000));
			}
		}
	}
	@EventHandler
	public void PlayerDamage(EntityDamageEvent event) {
		
		if(event.getEntity() instanceof Player) {
			
			Player pld = (Player)event.getEntity();
			//Player pla = (Player)event.getDamager();
			
			event.setCancelled(true);

			if(event.getCause() == DamageCause.PROJECTILE) {
				
				event.setCancelled(true);
				
				if(pld.getLevel() < 24) {
					pld.setHealth(0);
					pld.setLevel(200);
				} else {
					pld.setLevel(pld.getLevel() - 24);
					pld.setHealth(pld.getLevel() / 10);
					pld.playSound(pld.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
				}
			}
		}
	}
	@EventHandler
	public void PlayerDamagePlayer(EntityDamageByEntityEvent event) {
		
		if(event.getEntity() instanceof Player) {
			
			Player pld = (Player)event.getEntity();
			Player pla = (Player)event.getDamager();
			
			event.setCancelled(true);

			if(event.getCause() == DamageCause.ENTITY_ATTACK && pla.getItemInHand().getType() == Material.IRON_SWORD) {
				
				long currentTime = System.currentTimeMillis();
				long timeOfHit = hitCooldown.get(pld);
				
				if(pld.getLevel() < 40) {
					pld.setHealth(0);
					pld.setLevel(200);
				}else if(currentTime - timeOfHit > 500) {
					pld.setLevel(pld.getLevel() - (int)(40 + (20.0 * (Math.abs(pla.getVelocity().getX()) + Math.abs(pla.getVelocity().getY()) + Math.abs(pla.getVelocity().getZ())))));
					pld.setHealth(pld.getLevel() / 10);
					pla.playSound(pld.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 1);
					pld.playSound(pld.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
					hitCooldown.replace(pld, currentTime);
				}
				
			}
		}
	}
	@EventHandler
	public void HungerEvent(FoodLevelChangeEvent event) {
		event.setCancelled(true);
		event.setFoodLevel(5);
	}
	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent event) {
		
		Main.respawnHunger(event.getPlayer());
		
	}
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		if(event.getInventory() == inv) {
			event.setCancelled(true);
			if(event.getCurrentItem().getType() == Material.ARROW) {
				Player pl = (Player)event.getWhoClicked();
				pl.getInventory().clear();
				pl.getInventory().addItem(new ItemStack(Material.ARROW));
				pl.closeInventory();
				pl.setWalkSpeed(0.24f);
			}
			if(event.getCurrentItem().getType() == Material.IRON_SWORD) {
				Player pl = (Player)event.getWhoClicked();
				pl.getInventory().clear();
				pl.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
				pl.closeInventory();
				pl.setWalkSpeed(0.24f);
			}
		}
	}
	@EventHandler
	public void BreakBlock(BlockBreakEvent event) {
		
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			
			event.setCancelled(true);
			
			if(event.getBlock().getType() == Material.RED_STAINED_GLASS && CommandHello.inhibHealth <= 0) {
				
				CommandHello.nexusHealth = CommandHello.nexusHealth - 1;
				
				if(CommandHello.nexusHealth >= 1) {
					
					Bukkit.broadcastMessage("Nexus health is " + CommandHello.nexusHealth + "/75!");
					
				}
				if(CommandHello.nexusHealth <= 0) {
					
					Bukkit.broadcastMessage("NEXUS DESTROYED! ATTACKERS WIN!");
					
				}
				
			} else if(event.getBlock().getType() == Material.RED_WOOL){
				
				CommandHello.inhibHealth = CommandHello.inhibHealth - 1;
				
				if(CommandHello.inhibHealth >= 1) {
					
					Bukkit.broadcastMessage("Inhibitor health is " + CommandHello.inhibHealth + "/30!");
					
				}
				if(CommandHello.inhibHealth <= 0) {
					
					Bukkit.broadcastMessage("INHIBITOR DESTROYED!");
					
				}
				
			} else {
				event.getPlayer().sendMessage("dont break block stoopid");
			}
		}

	}
}

