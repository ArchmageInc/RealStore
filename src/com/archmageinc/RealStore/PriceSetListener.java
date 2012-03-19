package com.archmageinc.RealStore;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;

public class PriceSetListener implements Listener {

	private RealStore plugin;
	private Player owner;
	private Integer cost;
	private MaterialData type;
	private boolean setDefault	=	false;
	
	public PriceSetListener(RealStore instance,Player player,Integer price,MaterialData material){
		plugin		=	instance;
		owner		=	player;
		cost		=	price;
		type		=	material;
		plugin.addSetting(player);
	}
	
	public PriceSetListener(RealStore instance,Player player,Integer price){
		plugin		=	instance;
		owner		=	player;
		cost		=	price;
		plugin.addSetting(player);
	}
	
	public PriceSetListener(RealStore instance,Player player,Integer price,boolean def){
		plugin		=	instance;
		owner		=	player;
		cost		=	price;
		setDefault	=	def;
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
		
		if(!plugin.isStore(chest)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"That is not a store!");
			return;			
		}
		
		if(!plugin.getStoreOwner(chest).equals(owner)){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"That is not your store!");
			return;	
		}
		
		if(type==null && setDefault){
			if(plugin.setDefaultPrice(owner, chest, cost))
				plugin.sendPlayerMessage(owner, ChatColor.GREEN+"Setting the default price in this store to "+cost+" gold nuggets.");
			else
				plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"Unable to set the default price to "+cost+" for that chest. Try again.");
			
			plugin.removeSetting(owner);
			event.getHandlers().unregister(this);
			return;
		}
		
		if(type==null && event.getItem().getType()==null){
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"You must have the item whos price you wish to set in your hand!");
			return;	
		}else{
			type	=	event.getItem().getData();
		}
		
		
		if(plugin.setPrice(owner, chest, type, cost))
			plugin.sendPlayerMessage(owner, ChatColor.GREEN+"Setting the price of "+ChatColor.WHITE+type.toString()+ChatColor.GREEN+" to "+ChatColor.WHITE+cost+ChatColor.GREEN+" gold nuggets for that store.");
		else
			plugin.sendPlayerMessage(owner, ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"Unable to set the price of "+type.toString()+" to "+cost+" for that chest. Try again.");
		
		plugin.removeSetting(owner);
		event.getHandlers().unregister(this);
	}
}
