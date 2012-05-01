package fr.emphytos.adminshop;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.iCo6.system.Account;

public class AdminShopPlayerListener implements Listener {

	public AdminShop plugin;

	public AdminShopPlayerListener(AdminShop plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Account seller = new Account(e.getPlayer().getName());

		if(e.hasBlock())
		{
			if(e.getClickedBlock().getState() instanceof Sign)
			{
				Sign s = (Sign)e.getClickedBlock().getState();

				if(Shop.isShop(e.getClickedBlock().getLocation()))
				{
					Shop shop = Shop.getShop(e.getClickedBlock().getLocation());

					if(e.getAction() == Action.LEFT_CLICK_BLOCK)
					{
						if(this.plugin.Permissions.has(e.getPlayer(), "adminshop.admin"))
						{

							if(shop.hasBloc())
							{
								return;
							}

							else
							{
								shop.setBloc(e.getItem().getType());
								shop.setData(e.getItem().getData().getData());

								Shop.updateShop(e.getClickedBlock().getLocation());
								
								e.getPlayer().sendMessage(ChatColor.GREEN + "Vous vendez désormais du " + ChatColor.AQUA + shop.getBloc());
								plugin.saveShop();
							}
						}

						else
						{
							return;
						}
					}

					if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
					{
						e.setCancelled(true);
						Player p = e.getPlayer();
						ItemStack is = new ItemStack(shop.getBloc(), shop.getTailleLots(), shop.getData());

						if((e.getPlayer().getItemInHand().getType() == shop.getBloc()) && e.getPlayer().getItemInHand().getData().getData() == shop.getData())
						{
							if(e.getItem().getDurability() == 0 && e.getPlayer().getItemInHand().getEnchantments() != null && e.getPlayer().getItemInHand().getEnchantments().size() == 0)
							{
								//	if(Shop.hasEnoughBlock(p, is))
								if(this.plugin.confirm.contains(p.getName()))
								{

									e.getPlayer().getInventory().removeItem(new ItemStack[] { is });
									e.getPlayer().updateInventory();

									p.sendMessage(ChatColor.GREEN + "Vous avez vendu " + shop.getTailleLots() + " " + ChatColor.AQUA + shop.getBloc().name() + ChatColor.GREEN + " pour " + shop.getPrix() + " $ !");
									seller.getHoldings().add(shop.getPrix());
								}

								else
								{
									this.plugin.confirm.add(p.getName());
									p.sendMessage(ChatColor.AQUA + "[AdminShop]" + ChatColor.GREEN + "Faites un deuxième clic droit pour confirmer votre vente.");
								}

							}

							else
							{
								return;
							}
						}

						else
						{
							return;
						}

					}

				}
			}

		}

		else
		{
			return;
		}
	}

}
