package fr.emphytos.adminshop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class AdminShopBlockListener implements Listener {
	
	public AdminShop plugin;
	
	public AdminShopBlockListener(AdminShop plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e)
	{
		String l1 = e.getLine(0);
		int price = Integer.parseInt(e.getLine(3));
		int tailleLots = Integer.parseInt(e.getLine(1));
		
		if(l1.equalsIgnoreCase("adminshop"))
		{
			if(this.plugin.Permissions.has(e.getPlayer(), "adminshop.admin"))
			{
				Player p = e.getPlayer();
				String playerName = p.getName();
				
				if(tailleLots == 0)
				{
					p.sendMessage(ChatColor.RED + "La taille d'un lot ne peut être égal à 0.");
					e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
					e.getBlock().setType(Material.AIR);
					return;
				}
				
				else if(tailleLots < 0)
				{
					p.sendMessage(ChatColor.RED + "La taille d'un lot ne peut être inférieur à 0.");
					e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
					e.getBlock().setType(Material.AIR);
					return;
				}
				
				else if(price <= 0)
				{
					p.sendMessage(ChatColor.RED + "Le prix ne peut être inférieur ou égal à 0.");
					e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
					e.getBlock().setType(Material.AIR);
					return;
				}
				
				else
				{
					Shop shop;
					Block b = e.getBlock();
					Location location = b.getLocation();
					
					if(e.getLine(2).length() >= 1)
					{
						int mat = Integer.parseInt(e.getLine(2));
						shop = new Shop(location, playerName, Material.getMaterial(mat), price, tailleLots, (byte) 0);
						e.setLine(2, ChatColor.AQUA + shop.getBloc().name());
					}

					else
					{
						shop = new Shop(location, playerName, price, tailleLots, (byte) 0);
						e.setLine(2, ChatColor.AQUA + "Aucun bloc");
					}

					e.setLine(0, ChatColor.AQUA + "AdminShop");
					e.setLine(1, ChatColor.GREEN + "lot de : " + shop.getTailleLots());

					e.setLine(3, ChatColor.GREEN + "" + shop.getPrix() + ChatColor.AQUA + " $/lot");
				}
			}
			
			else
			{
				e.getPlayer().sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
				e.getBlock().setType(Material.AIR);
				e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
			}
		}
		
		else
		{
			return;
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e)
	{
		Shop shop = Shop.getShop(e.getBlock().getLocation());
		Player p = e.getPlayer();
		
		if(Shop.isShop(e.getBlock().getLocation()))
		{
			if(this.plugin.Permissions.has(p, "adminshop.admin"))
			{
				p.sendMessage(ChatColor.GREEN + "Vous avez détruit votre magasin !");
				AdminShop.listshop.remove(shop);
				plugin.saveShop();
				plugin.loadShop();
			}
			
			else
			{
				p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
			}

		}
	}

}
