package com.archmageinc.RealStore;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class StoreManagementListener implements Listener {

	private RealStore plugin;
	private Player owner;
	private boolean remove	=	false;
	
	/**
	 * Constructor to add a store
	 * 
	 * @param instance The main plugin
	 * @param player The player we are listening for
	 */
	public StoreManagementListener(RealStore instance,Player player){
		plugin	=	instance;
		owner	=	player;
		plugin.addSetting(player);
	}
	
	/**
	 * Constructor to remove a store
	 * 
	 * @param instance The main plugin
	 * @param player The player we are listening for
	 * @param removal True to remove a store, false to add a store
	 */
	public StoreManagementListener(RealStore instance,Player player,boolean removal){
		plugin	=	instance;
		owner	=	player;
		remove	=	removal;
		plugin.addSetting(player);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		//We don't care about players other than the owner
		if(!event.getPlayer().equals(owner))
			return;
		//We don't care about non-chest objects
		if(!(event.getClickedBlock().getState() instanceof Chest))
			return;
		
		Player player	=	event.getPlayer();
		Chest chest		=	(Chest) event.getClickedBlock().getState();
		
		//DoubleChests are very broken right now
		if(!(chest.getInventory().getHolder() instanceof Chest)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_BLUE+"Warning: "+ChatColor.WHITE+"Double chests cannot be used currently.");
			return;
		}
		
		//If we are not removing, it cannot already be a store
		if(plugin.isStore(chest) && !remove){
			plugin.sendPlayerMessage(player,ChatColor.RED+"Error: "+ChatColor.WHITE+"That is already a store!");
			return;
		}
		
		//If we are not removing, it cannot be a coffer
		if(plugin.isCoffer(chest) && !remove){
			plugin.sendPlayerMessage(player,ChatColor.RED+"Error: "+ChatColor.WHITE+"That is a coffer and cannot be a store!");
			return;
		}
		
		//If we are removing, it cannot be a coffer
		if(plugin.isCoffer(chest) && remove){
			plugin.sendPlayerMessage(player,ChatColor.RED+"Error: "+ChatColor.WHITE+"That is a coffer not a store!");
			return;
		}
		
		//If we are removing it must be a store
		if(!plugin.isStore(chest) && remove){
			plugin.sendPlayerMessage(player,ChatColor.RED+"Error: "+ChatColor.WHITE+"That is not a store!");
			return;
		}
		
		//If we are removing we must be the owner of the store
		if(plugin.isStore(chest) && remove && !plugin.getStoreOwner(chest).equals(owner)){
			plugin.sendPlayerMessage(player,ChatColor.RED+"Error: "+ChatColor.WHITE+"That is not your store!");
			return;
		}
		
		/**
		 * Chest Removal
		 */
		if(remove){
			if(plugin.removeStore(chest))
				plugin.sendPlayerMessage(owner, ChatColor.GREEN+"Your store has been removed from the chest.");
			else
				plugin.sendPlayerMessage(owner,ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"Unable to remove store. Try again.");
			
			plugin.removeSetting(owner);
			event.getHandlers().unregister(this);
			return;
		}		
		
		/**
		 * Chest Adding
		 */
		if(plugin.addStore(player, chest))
			plugin.sendPlayerMessage(player,ChatColor.GREEN+"The chest has been setup as a store. Use "+ChatColor.WHITE+"'/rs help price'"+ChatColor.GREEN+" for help setting prices.");
		else
			plugin.sendPlayerMessage(owner,ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"Unable to add the store. Try again.");
		
		plugin.removeSetting(owner);
		event.getHandlers().unregister(this);
	}
	
}
