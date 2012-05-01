package fr.emphytos.adminshop;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class AdminShop extends JavaPlugin {
	
	public String prefix = "[AdminShop] ";
	public Logger logger = Logger.getLogger("Minecraft");
	
	public static ArrayList<Shop> listshop = new ArrayList<Shop>();
	public static ArrayList<String> confirm = new ArrayList<String>();
	
	public AdminShopBlockListener blockListener = new AdminShopBlockListener(this);
	public AdminShopPlayerListener PlayerListener = new AdminShopPlayerListener(this);
	
	public PermissionManager Permissions = null;
	public boolean hooked = false;
	
	public void onDisable()
	{
		logger.info(prefix + "AdminShop is now disabled !");
		saveShop();
	}
	
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		
		if(pm.isPluginEnabled("PermissionsEx") && (!this.hooked))
		{
			this.Permissions = PermissionsEx.getPermissionManager();
			pm.registerEvents(PlayerListener, this);
			pm.registerEvents(blockListener, this);
			loadShop();
			logger.info(prefix + "AdminShop is now enabled !");
			
			this.hooked = true;
		}
		
		else
		{
			logger.warning(prefix + "PermissionsEx n'est pas installe, desactivation du plugin...");
			pm.disablePlugin(this);
		}
	}
	
	public void saveShop()
	{
		if(AdminShop.listshop.size() != 0)
		{
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();


				Document doc = docBuilder.newDocument();
				Element Shops = doc.createElement("shops");
				doc.appendChild(Shops);

				for(int i = 0; i < AdminShop.listshop.size(); i++){


					Element shop = doc.createElement("shop");
					Shops.appendChild(shop);


					Element owner = doc.createElement("owner");
					owner.appendChild(doc.createTextNode(AdminShop.listshop.get(i).getOwner()));
					shop.appendChild(owner);


					Element world = doc.createElement("world");
					world.appendChild(doc.createTextNode(AdminShop.listshop.get(i).getLoc().getWorld().getName()));
					shop.appendChild(world);


					Element X = doc.createElement("locX");
					X.appendChild(doc.createTextNode((String.valueOf(AdminShop.listshop.get(i).getLoc().getBlockX()))));
					shop.appendChild(X);

					// salary elements
					Element Y = doc.createElement("locY");
					Y.appendChild(doc.createTextNode(String.valueOf(AdminShop.listshop.get(i).getLoc().getBlockY())));
					shop.appendChild(Y);

					Element Z = doc.createElement("locZ");
					Z.appendChild(doc.createTextNode(String.valueOf(AdminShop.listshop.get(i).getLoc().getBlockZ())));
					shop.appendChild(Z);

					Element P = doc.createElement("prix");
					P.appendChild(doc.createTextNode(String.valueOf(AdminShop.listshop.get(i).getPrix())));
					shop.appendChild(P);

					Element mat = doc.createElement("material");
					mat.appendChild(doc.createTextNode(String.valueOf(AdminShop.listshop.get(i).getBloc().name())));
					shop.appendChild(mat);

					Element taille = doc.createElement("taille");
					taille.appendChild(doc.createTextNode(String.valueOf(AdminShop.listshop.get(i).getTailleLots())));
					shop.appendChild(taille);

					Element data = doc.createElement("data");
					data.appendChild(doc.createTextNode(String.valueOf(AdminShop.listshop.get(i).getData())));
					shop.appendChild(data);
				}
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				File file = new File("plugins/AdminShop/shop.xml");
				try 
				{
					if(file.exists())
					{
						file.delete();
						file.createNewFile();
					}
					else
					{
						file.createNewFile();
					}
				}catch(Exception e) { }
				StreamResult result = new StreamResult(new File(file.getAbsolutePath()));

				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				System.out.println("[AdminShop] Shop File saved!");

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			}
		}
	}
	
	public void loadShop()
	{
		try {

			File dirs = new File("plugins/AdminShop/");
			File fXmlFile = new File("plugins/AdminShop/shop.xml");
			dirs.mkdirs();

			if(!fXmlFile.exists())
			{
				return;
			}
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("shop");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String owner = getTagValue("owner", eElement);
					String world = getTagValue("world", eElement);
					Double X = Double.parseDouble((getTagValue("locX", eElement)));
					Double Y = Double.parseDouble((getTagValue("locY", eElement)));
					Double Z = Double.parseDouble((getTagValue("locZ", eElement)));
					int P = Integer.parseInt((getTagValue("prix", eElement)));
					// int P = Double.parseDouble((getTagValue("prix", eElement)));
					String mat = getTagValue("material", eElement);
					int taille = Integer.parseInt(getTagValue("taille", eElement));
					Location loc = new Location(this.getServer().getWorld(world), X, Y, Z);
					Shop shop = new Shop(loc, owner, Material.getMaterial(mat), P, taille, Byte.parseByte(getTagValue("data", eElement)));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

}
