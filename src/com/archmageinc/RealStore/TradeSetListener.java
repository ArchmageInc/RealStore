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
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class TradeSetListener implements Listener {

	private RealStore plugin;
	private Player owner;
	private Boolean clear	=	false;
	
	public TradeSetListener(RealStore instance,Player player){
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
			plugin.sendPlayerMessage(player, "You must open one of "+ChatColor.GOLD+"your"+ChatColor.WHITE+" stores to set the trade value of items. Use /rs trade again.");
			unregister();
			event.setCancelled(true);
			return;
		}
		
		/**
		 * We've opened one of our stores, when it closes, stop listening
		 */
		clear	=	true;
		plugin.sendPlayerMessage(player, "Click on any of the items in your store with an item on the cursor to set the trade value.");
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
		if(!event.getWhoClicked().equals(owner))
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
		
		//Don't handle modifications to player's inventory (Except shift click as that would put stuff in the store)
		if(event.getRawSlot()>=event.getInventory().getSize() && !event.isShiftClick()) 
			return;
		
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
		
		//Cancel player inventory shift click events
		if(event.getRawSlot()>=event.getInventory().getSize() && event.isShiftClick()){
			event.setCancelled(true);
			return;
		}
		
		/**
		 * By this time, the player is the owner of the store and has clicked on an item in the store
		 */
		MaterialData data	=	event.getCurrentItem().getData();
		
		//If they didn't click with anything on the cursor
		if(event.getCursor()==null){
			plugin.sendPlayerMessage(player, "Click on an item in the store, with a stack on the cursor to set the trade value of that item.");
			event.setCancelled(true);
			return;
		}
		
		ItemStack trade		=	event.getCursor();
		plugin.setPrice(player, chest, data, trade);
		
		event.setCancelled(true);
	}
	
	private void unregister(){
		InventoryCloseEvent.getHandlerList().unregister(this);
		InventoryClickEvent.getHandlerList().unregister(this);
		InventoryOpenEvent.getHandlerList().unregister(this);
	}
}
