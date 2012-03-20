package com.archmageinc.RealStore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpMessage {
	@SuppressWarnings("unused")
	private RealStore plugin;
	private String command;
	private Player player;
	
	public HelpMessage(RealStore instance,String subcommand){
		plugin	=	instance;
		command	=	subcommand==null ? null : subcommand.toLowerCase();
	}
	
	public HelpMessage(RealStore instance,Player p,String subcommand){
		plugin	=	instance;
		command	=	subcommand==null ? null : subcommand.toLowerCase();
		player	=	p;
	}
	
	/**
	 * Gets the message string for this help object. With line break markers as '|'
	 * 
	 * @return String the message string with 
	 */
	public String getMessage(){
		String result	=	ChatColor.DARK_RED+"Error: "+ChatColor.WHITE+"The command "+command+" was not found!";
		if(command==null){
			result	=	ChatColor.GOLD+"/rs coffer add" +
									  "|/rs coffer remove" +
									  "|/rs store add" +
									  "|/rs store remove" +
									  "|/rs price"+
									  "|/rs help <command>";
			return result;
		}
		
		if(command.equals("coffer")){
			result	=	ChatColor.GOLD+"/rs coffer add"+ChatColor.WHITE+"    - Hit a chest to create a coffer" +
					   ChatColor.GOLD+"|/rs coffer remove"+ChatColor.WHITE+" - Hit a chest to remove a coffer";
		}
		
		if(command.equals("store")){
			result	=	ChatColor.GOLD+"/rs store add"+ChatColor.WHITE+"    - Hit a chest to create a store" +
					   ChatColor.GOLD+"|/rs store remove"+ChatColor.WHITE+" - Hit a chest to remove a store";
		}
		
		if(command.equals("price")){
			result	=	ChatColor.GOLD+"/rs price $$"+ChatColor.WHITE+"         - Hit store with item to set the price" +
					   ChatColor.GOLD+"|/rs price $$ default"+ChatColor.WHITE+" - Hit a store to set the default price";
		}
		
		if(command.equals("material")){
			result	=	"Not yet implemented. See a wikki somewhere for material types.";
		}
		return result;
	}
	
	public void send(){
		player.sendMessage(getMessage().split("\\|"));
	}
}
