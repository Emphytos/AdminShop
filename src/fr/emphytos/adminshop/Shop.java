package fr.emphytos.adminshop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Shop 
{
	private Location loc;
	private String owner;
	private Material bloc;
	private Integer prix;
	private int tailleLots;
	private Byte data;
	
	public Shop(Location loc, String owner, Material bloc, Integer prix, int tailleLots, Byte data) {
		this.loc = loc;
		this.owner = owner;
		this.bloc = bloc;
		this.prix = prix;
		this.tailleLots = tailleLots;
		this.data = data;
		AdminShop.listshop.add(this);
	}
	
	public Shop(Location loc, String owner, Integer prix, int tailleLots, Byte data) {
		this.loc = loc;
		this.owner = owner;
		this.prix = prix;
		this.tailleLots = tailleLots;
		this.data = data;
		AdminShop.listshop.add(this);
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Material getBloc() {
		return bloc;
	}

	public void setBloc(Material bloc) {
		this.bloc = bloc;
		Shop.updateShop(this.getLoc());
	}

	public Integer getPrix() {
		return prix;
	}

	public void setPrix(Integer prix) {
		this.prix = prix;
	}

	public int getTailleLots() {
		return tailleLots;
	}

	public void setTailleLots(int tailleLots) {
		this.tailleLots = tailleLots;
	}

	public Byte getData() {
		return data;
	}

	public void setData(Byte data) {
		this.data = data;
	}
	
	public boolean hasBloc()
    {
        if(this.getBloc() != null)
        {
            return true;
        }
       return false;
    }
	
	public static Shop getShop(Location loc)
	{
		for(int i = 0; i < AdminShop.listshop.size(); i++)
		{
			if(AdminShop.listshop.get(i).getLoc().equals(loc))
			{
				return AdminShop.listshop.get(i);
			}
		}

		return null;
	}
	
	public static void updateShop(Location loc)
	{
		Sign sign = (Sign)loc.getBlock().getState();
		Shop shop = Shop.getShop(loc);
		sign.setLine(0, ChatColor.AQUA + "AdminShop");
		sign.setLine(1, ChatColor.GREEN + "lot de : " + shop.getTailleLots());
		if(shop.hasBloc())
		{
			if(shop.getData() == 0)
			{
				sign.setLine(2, ChatColor.AQUA + shop.getBloc().name());
			}
			
			else
			{
				sign.setLine(2, ChatColor.AQUA + shop.getBloc().name() + ":" + String.valueOf(shop.getData()));
			}
		}
		else
		{
			sign.setLine(2, ChatColor.AQUA + "Aucun bloc");
		}
		sign.setLine(3, ChatColor.GREEN + "" + shop.getPrix() + ChatColor.AQUA + " Imps/lot");
		sign.update();
	}
	
	public static boolean hasEnoughBlock(Player player, ItemStack is)
	{
		for(int i = 0; i < player.getInventory().getSize(); i++)
		{
			if(player.getInventory().getItem(i).getType() == is.getType())
			{
				if(player.getInventory().getItem(i).getAmount() >= is.getAmount())
				{
					return true;
				}
			}

		}

		return false;
	}
	
	public static boolean isShop(Location location)
	{
		for(int i = 0; i < AdminShop.listshop.size(); i++)
		{
			if(AdminShop.listshop.get(i).getLoc().equals(location))
			{
				return true;
			}
		}

		return false;
	}

}
