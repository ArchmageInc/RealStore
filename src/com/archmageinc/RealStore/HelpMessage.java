package com.archmageinc.RealStore;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpMessage {
	@SuppressWarnings("unused")
	private RealStore plugin;
	private String command;
	private CommandSender player;
	
	public HelpMessage(RealStore instance,String subcommand){
		plugin	=	instance;
		command	=	subcommand==null ? null : subcommand.toLowerCase();
	}
	
	public HelpMessage(RealStore instance,CommandSender p,String subcommand){
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
			result	=	ChatColor.GOLD+"/rs coffer add"+ChatColor.WHITE+"      - Hit a chest to create a coffer" +
					"|"+ChatColor.GOLD+"/rs coffer remove"+ChatColor.WHITE+"   - Hit a chest to remove a coffer" +
					"|"+ChatColor.GOLD+"/rs store add"+ChatColor.WHITE+"       - Hit a chest to create a store" +
					"|"+ChatColor.GOLD+"/rs store remove"+ChatColor.WHITE+"    - Hit a chest to remove a store" +
					"|"+ChatColor.GOLD+"/rs price"+ChatColor.WHITE+"           - Set store prices"+
					"|"+ChatColor.GOLD+"/rs help <command>"+ChatColor.WHITE+"  - Get more help on a command";
			return result;
		}
		
		if(command.equals("coffer")){
			result	=	ChatColor.GOLD+"/rs coffer add"+ChatColor.WHITE+"    - Hit a chest to create a coffer" +
					"|"+ChatColor.GOLD+"/rs coffer remove"+ChatColor.WHITE+" - Hit a chest to remove a coffer";
		}
		
		if(command.equals("store")){
			result	=	ChatColor.GOLD+"/rs store add"+ChatColor.WHITE+"    - Hit a chest to create a store" +
					"|"+ChatColor.GOLD+"/rs store remove"+ChatColor.WHITE+" - Hit a chest to remove a store";
		}
		
		if(command.equals("price")){
			result	=	ChatColor.GOLD+"/rs price $$"+ChatColor.WHITE+"         - Hit store with item to set the price" +
					"|"+ChatColor.GOLD+"/rs price $$ default"+ChatColor.WHITE+" - Hit a store to set the default price";
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
