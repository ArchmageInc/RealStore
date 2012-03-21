package com.archmageinc.RealStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Currency {
	
	private HashMap<Material,Integer> currencyMaterials	=	new HashMap<Material,Integer>();
	private boolean enable								=	true;
	private RealStore plugin;
	
	public Currency(RealStore instance){
		plugin	=	instance;
		List<Integer> currencies	=	plugin.getConfig().getIntegerList("currency");
		if(currencies==null || currencies.size()==0){
			plugin.logWarning("Missing currency configuration! Unable to enable!");
			enable	=	false;
			return;
		}
		String ratios	=	plugin.getConfig().getString("conversion");
		if(ratios==null){
			plugin.logWarning("Invalid Currency Conversion setting the the config! Unable to enable!");
			enable	=	false;
			return;
		}
		String[] ratiosA	=	ratios.split(":");
		if(ratiosA.length<currencies.size()){
			plugin.logWarning("Currency Conversions do not match the number of currencies! Unable to enable!");
			enable	=	false;
			return;
		}
		Integer[] ratiosI	=	new Integer[ratiosA.length];
		for(int i=0;i<ratiosA.length;i++){
			String ratio	=	ratiosA[i];
			try{
				ratiosI[i]	=	Integer.parseInt(ratio);
				i++;
			}catch(NumberFormatException e){
				plugin.logWarning("Invalid Currency Conversion: "+ratio+"! Unable to enable!");
				enable	=	false;
				return;
			}
		}
		
		currencyMaterials.clear();
		Iterator<Integer> itr	=	currencies.iterator();
		int i					=	0;
		while(itr.hasNext()){
			Integer currencyId	=	itr.next();
			Integer ratio		=	ratiosI[i];
			i++;
			if(Material.getMaterial(currencyId)==null){
				plugin.logWarning("Invalid Material ID for currency: "+currencyId+". It will be ignored!");
				continue;
			}
			Material material	=	Material.getMaterial(currencyId);
			if(currencyMaterials.containsKey(material)){
				plugin.logWarning("Duplicated material "+material.toString()+" in config! It will be ignored!");
				continue;
			}
			currencyMaterials.put(material,ratio);
		}
	}
	
	public boolean allowEnable(){
		return enable;
	}
	
	/**
	 * Determines if an ItemStack is currency
	 * 
	 * @param item ItemStack the stack to check
	 * @return boolean True if it is, false if it is not
	 */
	public boolean isCurrency(ItemStack item){
		return currencyMaterials.containsKey(item.getType());
	}
	
	/**
	 * Determines if an Material is currency
	 * 
	 * @param material Material the material to check
	 * @return boolean True if it is, false if it is not
	 */
	public boolean isCurrency(Material material){
		return currencyMaterials.containsKey(material);
	}
	
	/**
	 * Gets currency change for a price
	 * 
	 * @param price int The price of an object
	 * @param currency ItemStack a stack of currency items
	 * @return HashMap<Integer,ItemStack> A map of all of the change or null if not proper
	 */
	public HashMap<Integer,ItemStack> getChange(Integer price,ItemStack currency){
		if(price==null || currency==null)
			return null;
		if(price<=0)
			return null;
		if(!isCurrency(currency))
			return null;
		
		int value	=	convertToBase(currency);
		int change	=	value-price;
		
		if(change<0)
			return null;
		
		return colorUp(change);
		
	}
	
	/**
	 * Gets the currency change for a price from a map of currency
	 * 
	 * @param price int The price of an object
	 * @param currency HashMap<Object,ItemStack> the map of currency
	 * @return HashMap<Integer,ItemStack> A map of all the change or null if not proper
	 */
	public HashMap<Integer,ItemStack> getChange(Integer price,HashMap<Integer,ItemStack> currency){
		if(price==null || currency==null)
			return null;
		if(price<=0)
			return null;
		
		int value	=	convertToBase(currency);
		int change	=	value-price;
		
		if(change<0)
			return null;
		
		return colorUp(change);
		
	}
	
	/**
	 * Converts a currency ItemStack to a base in gold nuggets
	 * 
	 * @param currency ItemStack the currency
	 * @return Integer the total gold nugget value of the currency or null if invalid
	 */
	public Integer convertToBase(ItemStack currency){
		if(currency==null)
			return null;
		if(!isCurrency(currency))
			return 0;
		
		Iterator<Material> itr	=	currencyMaterials.keySet().iterator();
		while(itr.hasNext()){
			Material material	=	itr.next();
			Integer conversion	=	currencyMaterials.get(material);
			if(currency.getType().equals(material)){
				return currency.getAmount()*conversion;
			}
		}
		
		return null;
			
	}
	
	/**
	 * Converts a Set of currency ItemStacks to a base in gold nuggets
	 * 
	 * @param currency Set<ItemStack> the set of currency
	 * @return Integer the total gold nugget value of the set of currency or null if invalid
	 */
	public Integer convertToBase(Set<ItemStack> currency){
		if(currency==null)
			return null;
		Integer	base			=	0;
		Iterator<ItemStack> itr	=	currency.iterator();
		while(itr.hasNext()){
			ItemStack item	=	itr.next();
			if(isCurrency(item))
				base			+=	convertToBase(item);
		}
		
		return base;
	}
	
	/**
	 * Converts a Map of currency ItemStacks to a base in gold nuggets
	 * 
	 * @param currency Map<Obejct,ItemStack> the map of currency
	 * @return Integer the total gold nugget value of the set of currency or null if invalid
	 */
	public Integer convertToBase(Map<Integer,ItemStack> currency){
		if(currency==null)
			return null;
		Integer	base			=	0;
		Iterator<ItemStack> itr	=	currency.values().iterator();
		while(itr.hasNext()){
			ItemStack item	=	itr.next();
			if(isCurrency(item))
				base			+=	convertToBase(item);
		}
		
		return base;
	}
	
	/**
	 * Converts an Array of currency ItemStacks to a base in gold nuggets
	 * 
	 * @param currency ItemStack[] the array of currency
	 * @return Integer the total gold nugget value of the set of currency or null if invalid
	 */
	public Integer convertToBase(ItemStack[] currency){
		if(currency==null)
			return null;
		
		Integer base		=	0;
		for(ItemStack item : currency){
			if(isCurrency(item))
				base		+=	convertToBase(item);
		}
		
		return base;
	}
	
	/**
	 * Takes a number and converts it to the least number of currency objects in a HashMap format
	 * 
	 * @param total int The total nugget value to be converted
	 * @return HashMap<Integer,ItemStack> the map of currency
	 */
	public HashMap<Integer,ItemStack> colorUp(Integer total){
		if(total==null)
			return null;
		
		HashMap<Integer,ItemStack> items	=	new HashMap<Integer,ItemStack>();
		Material[] materials				=	(Material[]) currencyMaterials.keySet().toArray();
		Integer[] ratios					=	(Integer[]) currencyMaterials.values().toArray();
		
		for(int i=materials.length-1;i>=0;i--){
			Material material	=	materials[i];
			Integer ratio		=	ratios[i];
			int itotal			=	0;
			while(total>=ratio){
				itotal++;
				total-=ratio;
			}
			items.put(items.size(), new ItemStack(material,itotal));
		}
		
		return items;
		
	}
	
	/**
	 * Takes a number and converts it to the least number of currency objects in a HashSet format
	 * 
	 * @param total int The total nugget value to be converted
	 * @return HashSet<ItemStack> the set of currency
	 */
	public HashSet<ItemStack> colorUpSet(Integer total){
		if(total==null)
			return null;
		
		HashSet<ItemStack> items	=	new HashSet<ItemStack>();
		Material[] materials				=	(Material[]) currencyMaterials.keySet().toArray();
		Integer[] ratios					=	(Integer[]) currencyMaterials.values().toArray();
		
		for(int i=materials.length-1;i>=0;i--){
			Material material	=	materials[i];
			Integer ratio		=	ratios[i];
			int itotal			=	0;
			while(total>=ratio){
				itotal++;
				total-=ratio;
			}
			items.add(new ItemStack(material,itotal));
		}
		
		return items;
		
	}
	
	/**
	 * Gets a value of a price to be output
	 * 
	 * @param price int The price to convert
	 * @return String the price as a compiled string
	 */
	public String getValueString(Integer price){
		String valueString			=	"";
		Iterator<ItemStack> itr		=	colorUp(price).values().iterator();
		while(itr.hasNext()){
			ItemStack stack			=	itr.next();
			valueString				=	valueString+stack.getAmount()+" "+getCurrencyString(stack)+", ";
		}
		valueString	=	valueString.substring(0, valueString.length()-2);
		return valueString;
	}
	
	/**
	 * Gets a string of a currency type
	 * 
	 * @param currency ItemStack the currency to be converted
	 * @return String the currency as a string
	 */
	public String getCurrencyString(ItemStack currency){
		if(currency==null)
			return null;
		if(!isCurrency(currency))
			return null;
		
		return currency.getType().toString()+(currency.getAmount()>1 ? "s" : "");
		
	}
	
	/**
	 * Returns a map of items that are currency from an Inventory
	 * 
	 * @param inventory Inventory the inventory object to serach
	 * @return HashMap<Integer,ItemStack> returns the currency items in a map
	 */
	public HashMap<Integer,ItemStack> getCurrency(Inventory inventory){
		HashMap<Integer,ItemStack> currency	=	new HashMap<Integer,ItemStack>();
		Iterator<Material> mitr				=	currencyMaterials.keySet().iterator();
		while(mitr.hasNext()){
			Material material			=	mitr.next();
			@SuppressWarnings("unchecked")
			Iterator<ItemStack> itr		=	(Iterator<ItemStack>) inventory.all(material).values().iterator();
			while(itr.hasNext()){
				ItemStack item	=	itr.next();
				currency.put(currency.size(), item);
			}
		}
		return currency;
	}
}
