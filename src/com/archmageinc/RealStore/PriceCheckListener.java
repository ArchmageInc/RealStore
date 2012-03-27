package com.archmageinc.RealStore;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.material.MaterialData;

public class PriceCheckListener implements Listener {
	private RealStore plugin;
	private Player owner;
	private Boolean clear	=	false;
	
	public PriceCheckListener(RealStore instance,Player player){
		plugin	=	instance;
		owner	=	player;
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event){
		//We don't care about players other than the owner
		if(!event.getPlayer().equals(owner))
			return;
		//We don't care about non-chest interactions
		if(!event.getInventory().getType().equals(InventoryType.CHEST))
			return;
		if(event.getInventory().getHolder()==null)
			return;
		if(!(event.getInventory().getHolder() instanceof Chest))
			return;
		if(!(event.getPlayer() instanceof Player))
			return;
		
		Chest chest		=	(Chest) event.getInventory().getHolder();
		Player player	=	(Player) event.getPlayer();
		
		if(!plugin.isStore(chest))
			return;
		
		if(!plugin.getStoreOwner(chest).equals(player)){
			plugin.sendPlayerMessage(player, "You must open one of "+ChatColor.GOLD+"your"+ChatColor.WHITE+" stores to check the price of items. Use /rs price check again.");
			event.getHandlers().unregister(this);
			event.setCancelled(true);
			return;
		}
		
		/**
		 * We've opened one of our stores, when it closes, stop listening
		 */
		clear	=	true;
		plugin.sendPlayerMessage(player, "Click on any of the items in your store to check the price");
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		//We don't care about players other than the owner
		if(!event.getPlayer().equals(owner))
			return;
		//We don't care about non-chest interactions
		if(!event.getInventory().getType().equals(InventoryType.CHEST))
			return;
		if(event.getInventory().getHolder()==null)
			return;
		if(!(event.getInventory().getHolder() instanceof Chest))
			return;
		if(!(event.getPlayer() instanceof Player))
			return;
		
		Chest chest		=	(Chest) event.getInventory().getHolder();
		Player player	=	(Player) event.getPlayer();
		
		if(!plugin.isStore(chest))
			return;
		if(!plugin.getStoreOwner(chest).equals(player))
			return;
		
		if(clear)
			unregister();
		
	}
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		//Don't handle non-player inventory interactions
		if(!(event.getWhoClicked() instanceof Player)) 
			return;
		
		//Don't handle non-chest interactions
		if(!event.getInventory().getType().equals(InventoryType.CHEST)) 
			return;
		
		//Temporary fix to get around double chest casting errors
		if(!(event.getInventory().getHolder() instanceof Chest))
			return;
		
		Player player	=	(Player) event.getWhoClicked();
		Chest chest		=	(Chest) event.getInventory().getHolder();		
		
		//Don't handle non-store interactions
		if(!plugin.isStore(chest)) 
			return;
		
		//Don't handle non-owner interactions
		if(!plugin.getStoreOwner(chest).equals(player)) 
			return;
		
		//Cancel all player inventory manipulation
		if(event.getRawSlot()>=event.getInventory().getSize()){ 
			event.setCancelled(true);
			return;
		}
		
		//Cancel store interactions with nothing under the cursor
		if(event.getCurrentItem()==null || event.getCurrentItem().getType().equals(Material.AIR)){
			event.setCancelled(true);
			return;
		}
		
		//Cancel store interactions with the outside (i.e. dropping items)
		if(event.getSlotType().equals(SlotType.OUTSIDE)){
			event.setCancelled(true);
			return;
		}
		
		/**
		 * By this time, the player is the owner of the store and has clicked on an item in the store
		 */
		MaterialData data	=	event.getCurrentItem().getData();
		Integer price		=	plugin.getPrice(chest, data);
		
		plugin.sendPlayerMessage(player, ChatColor.DARK_GREEN+"Price: "+ChatColor.WHITE+plugin.currencyManager().getValueString(price));
		event.setCancelled(true);
	}
	
	private void unregister(){
		InventoryCloseEvent.getHandlerList().unregister(this);
		InventoryClickEvent.getHandlerList().unregister(this);
		InventoryOpenEvent.getHandlerList().unregister(this);
	}
}
