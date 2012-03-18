package com.archmageinc.RealStore;

import org.bukkit.ChatColor;

public class HelpMessage {
	@SuppressWarnings("unused")
	private RealStore plugin;
	private String command;
	
	public HelpMessage(RealStore instance,String subcommand){
		plugin	=	instance;
		command	=	subcommand==null ? null : subcommand.toLowerCase();
	}
	
	
	public String getMessage(){
		String result	=	ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"The command "+command+" was not found!";
		if(command==null){
			result	=	"/rs coffer add|/rs coffer remove|/rs store add|/rs store remove|/rs price";
			return result;
		}
		
		if(command.equals("coffer")){
			result	=	"/rs coffer add - Hit a chest to create a coffer|/rs coffer remove - Hit a chest to remove a coffer";
		}
		
		if(command.equals("store")){
			result	=	"/rs store add - Hit a chest to create a store|/rs store remove - Hit a chest to remove a store";
		}
		
		if(command.equals("price")){
			result	=	"/rs price $$ - Hit a store with an item to set that price|/rs price $$ default - Hit a store and set the default price";
		}
		
		if(command.equals("material")){
			result	=	"Not yet implemented. See a wikki somewhere for material types.";
		}
		return result;
	}
}
