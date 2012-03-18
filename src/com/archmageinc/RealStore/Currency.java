package com.archmageinc.RealStore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Currency {
	
	public static final HashSet<Material> currencyMaterials	=	new HashSet<Material>(Arrays.asList(Material.GOLD_NUGGET,Material.GOLD_INGOT,Material.GOLD_BLOCK,Material.DIAMOND,Material.DIAMOND_BLOCK));
		
	/**
	 * Determines if an ItemStack is currency
	 * 
	 * @param item ItemStack the stack to check
	 * @return boolean True if it is, false if it is not
	 */
	public static boolean isCurrency(ItemStack item){
		return currencyMaterials.contains(item.getType());
	}
	
	/**
	 * Determines if an Material is currency
	 * 
	 * @param material Material the material to check
	 * @return boolean True if it is, false if it is not
	 */
	public static boolean isCurrency(Material material){
		return currencyMaterials.contains(material);
	}
	
	/**
	 * Gets currency change for a price
	 * 
	 * @param price int The price of an object
	 * @param currency ItemStack a stack of currency items
	 * @param inDiamonds boolean True if change should include diamonds, false if gold only
	 * @return HashMap<Integer,ItemStack> A map of all of the change or null if not proper
	 */
	public static HashMap<Integer,ItemStack> getChange(Integer price,ItemStack currency,boolean inDiamonds){
		if(price==null || currency==null)
			return null;
		if(price<=0)
			return null;
		if(!Currency.isCurrency(currency))
			return null;
		
		int value	=	Currency.convertToBase(currency);
		int change	=	value-price;
		
		if(change<0)
			return null;
		
		return Currency.colorUp(change,inDiamonds);
		
	}
	
	/**
	 * Gets the currency change for a price from a map of currency
	 * 
	 * @param price int The price of an object
	 * @param currency HashMap<Object,ItemStack> the map of currency
	 * @param inDiamonds boolean True if change should include diamonds, false if gold only
	 * @return HashMap<Integer,ItemStack> A map of all the change or null if not proper
	 */
	public static HashMap<Integer,ItemStack> getChange(Integer price,HashMap<Integer,ItemStack> currency,boolean inDiamonds){
		if(price==null || currency==null)
			return null;
		if(price<=0)
			return null;
		
		int value	=	Currency.convertToBase(currency);
		int change	=	value-price;
		
		if(change<0)
			return null;
		
		return Currency.colorUp(change,inDiamonds);
		
	}
	
	/**
	 * Converts a currency ItemStack to a base in gold nuggets
	 * 
	 * @param currency ItemStack the currency
	 * @return Integer the total gold nugget value of the currency or null if invalid
	 */
	public static Integer convertToBase(ItemStack currency){
		if(currency==null)
			return null;
		if(!Currency.isCurrency(currency))
			return 0;
		if(currency.getType().equals(Material.GOLD_NUGGET))
			return currency.getAmount();
		if(currency.getType().equals(Material.GOLD_INGOT))
			return currency.getAmount()*9;
		if(currency.getType().equals(Material.GOLD_BLOCK) || currency.getType().equals(Material.DIAMOND))
			return currency.getAmount()*9*9;
		if(currency.getType().equals(Material.DIAMOND_BLOCK))
			return currency.getAmount()*9*9*9;
		
		return null;
			
	}
	
	/**
	 * Converts a Set of currency ItemStacks to a base in gold nuggets
	 * 
	 * @param currency Set<ItemStack> the set of currency
	 * @return Integer the total gold nugget value of the set of currency or null if invalid
	 */
	public static Integer convertToBase(Set<ItemStack> currency){
		if(currency==null)
			return null;
		Integer	base			=	0;
		Iterator<ItemStack> itr	=	currency.iterator();
		while(itr.hasNext()){
			ItemStack item	=	itr.next();
			if(Currency.isCurrency(item))
				base			+=	Currency.convertToBase(item);
		}
		
		return base;
	}
	
	/**
	 * Converts a Map of currency ItemStacks to a base in gold nuggets
	 * 
	 * @param currency Map<Obejct,ItemStack> the map of currency
	 * @return Integer the total gold nugget value of the set of currency or null if invalid
	 */
	public static Integer convertToBase(Map<Integer,ItemStack> currency){
		if(currency==null)
			return null;
		Integer	base			=	0;
		Iterator<ItemStack> itr	=	currency.values().iterator();
		while(itr.hasNext()){
			ItemStack item	=	itr.next();
			if(Currency.isCurrency(item))
				base			+=	Currency.convertToBase(item);
		}
		
		return base;
	}
	
	/**
	 * Converts an Array of currency ItemStacks to a base in gold nuggets
	 * 
	 * @param currency ItemStack[] the array of currency
	 * @return Integer the total gold nugget value of the set of currency or null if invalid
	 */
	public static Integer convertToBase(ItemStack[] currency){
		if(currency==null)
			return null;
		
		Integer base		=	0;
		for(ItemStack item : currency){
			if(Currency.isCurrency(item))
				base		+=	Currency.convertToBase(item);
		}
		
		return base;
	}
	
	/**
	 * Takes a number and converts it to the least number of currency objects
	 * 
	 * @param total int The total nugget value to be converted
	 * @param includeDiamonds boolean True if the return should include diamond currency
	 * @return HashMap<Integer,ItemStack> the map of currency
	 */
	public static HashMap<Integer,ItemStack> colorUp(Integer total,boolean includeDiamonds){
		if(total==null)
			return null;
		
		HashMap<Integer,ItemStack> items	=	new HashMap<Integer,ItemStack>();
		
		if(includeDiamonds && total>=9*9*9){
			int dblocks	=	0;
			while(total>=9*9*9){
				dblocks++;
				total-=9*9*9;
			}
			items.put(items.size(),new ItemStack(Material.DIAMOND_BLOCK,dblocks));
		}
		
		if(total>=9*9){
			int diamonds	=	0;
			while(total>=9*9){
				diamonds++;
				total-=9*9;
			}
			if(includeDiamonds)
				items.put(items.size(),new ItemStack(Material.DIAMOND,diamonds));
			else
				items.put(items.size(),new ItemStack(Material.GOLD_BLOCK,diamonds));
		}
		
		if(total>=9){
			int gold		=	0;
			while(total>=9){
				gold++;
				total-=9;
			}
			items.put(items.size(),new ItemStack(Material.GOLD_INGOT,gold));
		}
		
		if(total>=1){
			items.put(items.size(),new ItemStack(Material.GOLD_NUGGET,total));
			total-=total;
		}
		
		return items;
		
	}
	
	public static HashSet<ItemStack> colorUpSet(Integer total,boolean includeDiamonds){
		if(total==null)
			return null;
		
		HashSet<ItemStack> items	=	new HashSet<ItemStack>();
		
		if(includeDiamonds && total>=9*9*9){
			int dblocks	=	0;
			while(total>=9*9*9){
				dblocks++;
				total-=9*9*9;
			}
			items.add(new ItemStack(Material.DIAMOND_BLOCK,dblocks));
		}
		
		if(total>=9*9){
			int diamonds	=	0;
			while(total>=9*9){
				diamonds++;
				total-=9*9;
			}
			if(includeDiamonds)
				items.add(new ItemStack(Material.DIAMOND,diamonds));
			else
				items.add(new ItemStack(Material.GOLD_BLOCK,diamonds));
		}
		
		if(total>=9){
			int gold		=	0;
			while(total>=9){
				gold++;
				total-=9;
			}
			items.add(new ItemStack(Material.GOLD_INGOT,gold));
		}
		
		if(total>=1){
			items.add(new ItemStack(Material.GOLD_NUGGET,total));
			total-=total;
		}
		
		return items;
		
	}
	
	/**
	 * Gets a value of a price to be output
	 * 
	 * @param price int The price to convert
	 * @param inDiamonds boolean should the string include diamond conversions
	 * @return String the price as a compiled string
	 */
	public static String getValueString(Integer price,boolean inDiamonds){
		String valueString			=	"";
		Iterator<ItemStack> itr		=	Currency.colorUp(price,inDiamonds).values().iterator();
		while(itr.hasNext()){
			ItemStack stack			=	itr.next();
			valueString				=	valueString+stack.getAmount()+" "+Currency.getCurrencyString(stack)+", ";
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
	public static String getCurrencyString(ItemStack currency){
		if(currency==null)
			return null;
		if(!Currency.isCurrency(currency))
			return null;
		
		if(currency.getType().equals(Material.DIAMOND_BLOCK))
			return "diamond block"+(currency.getAmount()>1 ? "s" : "");
		if(currency.getType().equals(Material.DIAMOND))
			return "diamond"+(currency.getAmount()>1 ? "s" : "");
		if(currency.getType().equals(Material.GOLD_BLOCK))
			return "gold block"+(currency.getAmount()>1 ? "s" : "");
		if(currency.getType().equals(Material.GOLD_INGOT))
			return "gold ingot"+(currency.getAmount()>1 ? "s" : "");
		if(currency.getType().equals(Material.GOLD_NUGGET))
			return "nugget"+(currency.getAmount()>1 ? "s" : "");
		return null;
	}
	
	/**
	 * Returns a map of items that are currency from an Inventory
	 * 
	 * @param inventory Inventory the inventory object to serach
	 * @return HashMap<Integer,ItemStack> returns the currency items in a map
	 */
	public static HashMap<Integer,ItemStack> getCurrency(Inventory inventory){
		HashMap<Integer,ItemStack> currency	=	new HashMap<Integer,ItemStack>();
		Iterator<Material> mitr				=	Currency.currencyMaterials.iterator();
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

	public static boolean isDiamond(ItemStack item){
		return (item.getType().equals(Material.DIAMOND) || item.getType().equals(Material.DIAMOND_BLOCK));
	}
	
	public static boolean hasDiamond(HashMap<Integer,ItemStack> currency){
		Iterator<ItemStack> itr	=	currency.values().iterator();
		while(itr.hasNext()){
			ItemStack item	=	itr.next();
			if(item.getType().equals(Material.DIAMOND) || item.getType().equals(Material.DIAMOND_BLOCK))
				return true;
		}
		
		return false;
	}
}
