package com.archmageinc.RealStore;

import java.util.Hashtable;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Store {
	
	private Chest chest;
	private OfflinePlayer owner;
	private Integer defaultPrice;
	private Hashtable<MaterialData,Object> prices;
	private RealStore plugin;
	private StoreType type;
	
	public Store(RealStore instance,OfflinePlayer player,Chest store){
		chest			=	store;
		owner			=	player;
		plugin			=	instance;
		prices			=	new Hashtable<MaterialData,Object>();
		defaultPrice	=	1;
		type			=	StoreType.NORMAL;
	}
	
	/**
	 * Sets the store type to an admin store
	 */
	public void setAdmin(){
		type	=	StoreType.ADMIN;
	}
	
	/**
	 * Sets the default price of the store
	 * @param price The currency value for the default price
	 * @return True if setting price worked, false otherwise
	 */
	public boolean setDefaultPrice(Integer price){
		if(price<0)
			return false;
		
		defaultPrice	=	price;
		plugin.saveStores();
		return true;
	}
	
	/**
	 * Sets the price of an item in the store to a currency value
	 * 
	 * @param data The MaterialData of the item to price
	 * @param price The currency value of the item's price
	 * @return True if setting the price was successful, false otherwise
	 */
	public boolean setPrice(MaterialData data,Integer price){
		if(data==null || price==null)
			return false;
		if(price<0)
			return false;
		
		prices.put(data, price);
		plugin.saveStores();
		return true;
	}
	
	/**
	 * Sets the price of an item in the store to a currency value
	 * 
	 * @param data The MaterialData of the item to price
	 * @param trade The the ItemStack which would be accepted for trade
	 * @return True if setting the price was successful, false otherwise
	 */
	public boolean setPrice(MaterialData data,ItemStack trade){
		if(data==null || trade==null)
			return false;
		
		prices.put(data, trade);
		plugin.saveStores();
		return true;
	}
	
	
	/**
	 * Gets the price of an item in the store
	 * @param data The MaterialData of the item's price to get
	 * @return Either returns and Integer price or ItemStack trade value
	 */
	public Object getPrice(MaterialData data){
		if(data==null)
			return null;
		
		if(prices.containsKey(data))
			return prices.get(data);
		
		return defaultPrice;
		
	}
	
	/**
	 * Gets the owner of the store
	 * @return returns the OfflinePlayer that owns the store
	 */
	public OfflinePlayer getOwner(){
		return owner;
	}
	
	/**
	 * Gets the type of the store
	 * @return returns the StoreType
	 */
	public StoreType getType(){
		if(type==null)
			return StoreType.NORMAL;
		return type;
	}
	
	/**
	 * Is the item in stock?
	 * @param data The MaterialData of the item to check
	 * @param quantity The minimum amount to check
	 * @return True if the store has the item in stock, false otherwise
	 */
	public boolean inStock(MaterialData data,Integer quantity){
		return chest.getInventory().contains(data.toItemStack(), quantity);	
	}
	
	/**
	 * Gets the chest for the store
	 * @return the Chest for the store
	 */
	public Chest getChest(){
		return chest;
	}
	
	
	/**
	 * Attempts to purchase an item from the store
	 * @param item The ItemStack to purchase
	 * @return True if the purchase was successful, false otherwise
	 */
	public boolean purchaseItem(ItemStack item){
		if(type.equals(StoreType.ADMIN))
			return true;
		
		if(!inStock(item.getData(),item.getAmount()))
			return false;
		
		chest.getInventory().removeItem(item);
		return true;
		
	}
	
	public Integer getDefaultPrice(){
		return defaultPrice;
	}
	
	public Hashtable<MaterialData,Object> getPrices(){
		return prices;
	}

}
