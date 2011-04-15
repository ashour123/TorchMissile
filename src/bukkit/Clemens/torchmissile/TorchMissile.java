package bukkit.Clemens.torchmissile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class TorchMissile extends JavaPlugin
{
	static Server server;
	static Plugin plugin;
	static int size = 5;
	static boolean WO = true;
	static List<Player> Clicks = new ArrayList<Player>();
	PropertiesFile TorchConfig;
	
	private final TorchPlayerListener playerListener = new TorchPlayerListener();
	
	public void onEnable()
    {
		PluginDescriptionFile pdfFile = this.getDescription();
	    System.out.println("[ TorchMissile ] " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	    loadSettings();
		registerEvents();
		server = this.getServer();
		plugin = this;
    }
	public void onDisable()
    {
		System.out.println("[ TorchMissile ]  Thanks for trying the TorchMissile plugin!");
    }
	private void registerEvents()
    {
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
    }
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
		if (sender instanceof Player)
        {
			String commandName = command.getName().toLowerCase();;
            if ((commandName.equals("torchmissile")) || (commandName.equals("tm")))
            {
            	if (args.length >= 1)
            	{
            		if (args[0].equalsIgnoreCase("help"))
    	       	 	{
            			
    	       	 	}
            		return true;
            	}
            	else
                	return false;
            }
            else
            	return false;
        }
		else
			return false;
    }
	public static void AddClick(final Player player)
	{
		if (Clicks.contains(player))
		{
			Clicks.remove(player);
			ShootTorchMissile(player);
			RemoveItem(player, 50);
		}
		else
		{
			Clicks.add(player);
			server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
	    	{
				public void run()
				{
					Clicks.remove(player);
				}
	    	}, 1L);
		}
	}
	private static void ShootTorchMissile(Player player)
	{
		List<Block> Blocks_tmp = new ArrayList<Block>();
		Blocks_tmp = player.getLineOfSight(null, 150);
		final List<Block> Blocks = Blocks_tmp;
		int time = 0;
		for (int i = 3; i < Blocks.size(); i++)
		{
			final Block bl = Blocks.get(i);
			final int id = bl.getTypeId();
			server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
	    	{
				public void run()
				{
					bl.setTypeId(89);
				}
	    	}, 0L + time);
			server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
	    	{
				public void run()
				{
					bl.setTypeId(id);
					if (Blocks.indexOf(bl) == Blocks.size() - 1)
						Torch_Explosions(bl);
				}
	    	}, 2L + time);
			time = time + 2;
		}
		
		/**
		Block target = player.getTargetBlock(null, 100);
		Location player_eyes = player.getEyeLocation();
		Vector stuz = player_eyes.toVector();
		Vector direct = player_eyes.getDirection();
		direct.normalize();
		stuz = stuz.add(direct);
		int time = 0;
		for (int i = 0; i < Laser_Range; i++)
		{
			
		}
		*/
	}
	
	private static void Torch_Explosions(Block bl)
	{
		World world = bl.getWorld();
		boolean sec = true;
		int counter = 0;
		int X = bl.getX();
		int Y = bl.getY();
		int Z = bl.getZ();
		for (int y = Y-size; y <= Y+size; y++)
		{
			for (int x = X-size; x <= X+size; x++)
			{
				for (int z = Z-size; z <= Z+size; z++)
				{
					Block blk = world.getBlockAt(x, y, z);
					if ((blk.getTypeId() == 0) && (touchSolide(blk)) && (sec == true))
					{
						blk.setTypeId(50);
						sec = false;
					}
					if (sec == false && counter == 5)
						sec = true;
					counter++;
					if (counter > 5)
						counter = 0;
				}
			}
		}
	}
	private static boolean touchSolide(Block bl)
	{
		int north = bl.getFace(BlockFace.NORTH).getTypeId();
		int east = bl.getFace(BlockFace.EAST).getTypeId();
		int south = bl.getFace(BlockFace.SOUTH).getTypeId();
		int west = bl.getFace(BlockFace.WEST).getTypeId();
		int down = bl.getFace(BlockFace.DOWN).getTypeId();
		if (WO == false)
		{
			if (down != 0 && down != 50 && down != 8 && down != 9)
    			return true;
		}
		else
			if (down != 0 && down != 50 && down != 8 && down != 9)
    			return false;
		
		if (north != 0 && north != 50 && north != 8 && north != 9)
    		return true;
    	else if (east != 0 && east != 50 && east != 8 && east != 9)
    		return true;
    	else if (south != 0 && south != 50 && south != 8 && south != 9)
    		return true;
    	else if (west != 0 && west != 50 && west != 8 && west != 9)
    		return true;
		
		return false;
	}
	private static void RemoveItem(Player player, int ID)
	{
		Inventory inv = player.getInventory();
		ItemStack stack = inv.getItem(inv.first(ID));
		if (stack.getAmount() - 1 <= 0)
			player.getInventory().remove(stack);
		else
			stack.setAmount(stack.getAmount() - 1);
	}
	private void loadSettings()
	{
		this.getDataFolder().mkdir();
		TorchConfig = new PropertiesFile(this.getDataFolder().getPath() + "/TorchMissile.txt");
		try
		{
			TorchConfig.load();
			if (TorchConfig.containsKey("Torch_Spawn_Radius"))
			{
				size = Integer.parseInt(TorchConfig.props.getProperty("Torch_Spawn_Radius"));
			}
			if (TorchConfig.containsKey("Walls_Only"))
			{
				WO = Boolean.parseBoolean(TorchConfig.props.getProperty("Walls_Only"));
			}
		}
		catch (IOException ioe)
		{
			TorchConfig.saveDefaultSettings();
		}
	}
}
