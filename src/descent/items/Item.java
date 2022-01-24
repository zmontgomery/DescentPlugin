package descent.items;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import descent.champions.Champ;

public abstract class Item {
	private static Map<ItemStack, Item> itemObjects = new HashMap<>();
	
	public final String NAME;
	public final Champ OWNER;
	public final ItemStack OBJECT;
	public final short USES;
	
	protected Item(String name, Champ owner, ItemStack object, short uses) {
		addItem(object, this);
		this.NAME = name;
		this.OWNER = owner;
		this.OBJECT = object;
		this.USES = uses;
	}
	public static Item getItem(ItemStack object) {
		return itemObjects.get(object);
	}
	public static void addItem(ItemStack object, Item item) {
		itemObjects.put(object, item);
	}
	
	public abstract void use();
}
