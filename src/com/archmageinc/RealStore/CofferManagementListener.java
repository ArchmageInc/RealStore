package com.archmageinc.RealStore;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CofferManagementListener implements Listener {

	private RealStore plugin;
	private Player owner;
	private boolean remove	=	false;
	
	public CofferManagementListener(RealStore instance,Player player){
		plugin	=	instance;
		owner	=	player;
		plugin.addSetting(player);
	}
	
	public CofferManagementListener(RealStore instance,Player player,boolean removal){
		plugin	=	instance;
		owner	=	player;
		remove	=	removal;
		plugin.addSetting(player);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(!event.getPlayer().equals(owner))
			return;
		if(!(event.getClickedBlock().getState() instanceof Chest))
			return;
		
		Chest chest	=	(Chest) event.getClickedBlock().getState();
		
		//DoubleChests are very broken right now
		if(!(chest.getInventory().getHolder() instanceof Chest)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_BLUE+"Warning: "+ChatColor.WHITE+"Double chests cannot be used currently.");
			return;
		}
		
		if(plugin.isCoffer(chest) && !plugin.getCofferOwner(chest).equals(owner)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"That is not your coffer!");
			return;		
		}
		
		if(remove && !plugin.isCoffer(chest)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"That is not a coffer!");
			return;	
		}
		
		if(!remove && plugin.isCoffer(chest)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"That is already a coffer!");
			return;	
		}
		
		if(!remove && plugin.isStore(chest)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"That is a store and cannot be a coffer!");
			return;	
		}
		
		if(remove){
			if(plugin.isLastCoffer(owner)){
				plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"This is your last coffer! It cannot be removed!");
				plugin.removeSetting(owner);
				event.getHandlers().unregister(this);
				return;
			}
			if(plugin.removeCoffer(chest))
				plugin.sendPlayerMessage(owner, ChatColor.GREEN+"The coffer has been removed");
			else
				plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"Unable to remove coffer. Try again.");
			
			plugin.removeSetting(owner);
			event.getHandlers().unregister(this);
			return;	
		}
		
		if(plugin.addCoffer(owner,chest))
			plugin.sendPlayerMessage(owner, ChatColor.GREEN+"The chest has been added to your coffers");
		else
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"Unable to add coffer. Try again.");
		
		plugin.removeSetting(owner);
		event.getHandlers().unregister(this);
		return;	
	}
	
}
