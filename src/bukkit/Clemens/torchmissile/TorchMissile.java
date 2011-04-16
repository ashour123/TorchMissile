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

import com.nijikokun.bukkit.Permissions.Permissions;


public class TorchMissile extends JavaPlugin
{
	static Server server;
	static Plugin plugin;
	static int size = 5;
	static int Missile_Range = 250;
	static boolean WO = true;
	static boolean Missile_Glow = true;
	//static String Missile_Speed = "normal";
	static List<Player> Clicks = new ArrayList<Player>();
	PropertiesFile TorchConfig;
	private static Permissions Permissions = null;
	
	private final TorchPlayerListener playerListener = new TorchPlayerListener();
	
	public void onEnable()
    {
		PluginDescriptionFile pdfFile = this.getDescription();
	    System.out.println("[ TorchMissile ] " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	    loadSettings();
		registerEvents();
		server = this.getServer();
		plugin = this;
		setupPermissions();
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
		/**
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
		*/
		return false;
    }
	public static void AddClick(final Player player)
	{
		if (Clicks.contains(player))
		{
			Clicks.remove(player);
			if (checkPermissions(player) == "normal" || checkPermissions(player) == "single")
			{
				ShootTorchMissile(player);
				RemoveItem(player, 50);
			}
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
	private static void ShootTorchMissile(final Player player)
	{
		int time = 0;
		int plus = 2;
		long lo = 2L;
		/**
		if (Missile_Speed == "fast")
		{
			plus = 1;
			lo = 1L;
		}
		else if (Missile_Speed == "slow")
		{
			plus = 40;
			lo = 3L;
		}
		*/
		List<Block> Blocks_tmp = new ArrayList<Block>();
		Blocks_tmp = player.getLineOfSight(null, Missile_Range);
		final List<Block> Blocks = Blocks_tmp;
		for (int i = 3; i < Blocks.size(); i++)
		{
			final Block bl = Blocks.get(i);
			final int id = bl.getTypeId();
			server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
	    	{
				public void run()
				{
					if (Missile_Glow)
						bl.setTypeId(89);
					else
						bl.setTypeId(49);
				}
	    	}, 0L + time);
			server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
	    	{
				public void run()
				{
					bl.setTypeId(id);
					if (Blocks.indexOf(bl) == Blocks.size() - 1)
					{
						if (checkPermissions(player) == "normal")
							Torch_Explosion(bl);
						else if (checkPermissions(player) == "single")
							SingleTorch(bl);
					}
				}
	    	}, lo + time);
			time = time + plus;
		}
	}
	private static void SingleTorch(Block bl)
	{
		WO = false;
		int range = 1;
		boolean placed = false;
		World world = bl.getWorld();
		int X = bl.getX();
		int Y = bl.getY();
		int Z = bl.getZ();
		while (placed == false)
		{
			for (int y = Y-range; y <= Y+range; y++)
			{
				for (int x = X-range; x <= X+range; x++)
				{
					for (int z = Z-range; z <= Z+range; z++)
					{
						Block blk = world.getBlockAt(x, y, z);
						if ((blk.getTypeId() == 0) && (touchSolide(blk)) && (placed == false))
						{
							blk.setTypeId(50);
							placed = true;
						}
					}
				}
			}
			range++;
		}
		WO = true;
	}
	
	private static void Torch_Explosion(Block bl)
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
			if (down != 0 && down != 50 && down != 8 && down != 9 && down != 78)
    			return true;
		}
		else
			if (down != 0 && down != 50 && down != 8 && down != 9 && down != 78)
    			return false;
		
		if (north != 0 && north != 50 && north != 8 && north != 9 && north != 78)
    		return true;
    	else if (east != 0 && east != 50 && east != 8 && east != 9 && east != 78)
    		return true;
    	else if (south != 0 && south != 50 && south != 8 && south != 9 && south != 78)
    		return true;
    	else if (west != 0 && west != 50 && west != 8 && west != 9 && west != 78)
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
			if (TorchConfig.containsKey("Missile_Glow"))
			{
				Missile_Glow = Boolean.parseBoolean(TorchConfig.props.getProperty("Missile_Glow"));
			}
			/**
			if (TorchConfig.containsKey("Missile_Speed"))
			{
				Missile_Speed = TorchConfig.props.getProperty("Missile_Speed");
			}
			*/
		}
		catch (IOException ioe)
		{
			TorchConfig.saveDefaultSettings();
		}
	}
	private void setupPermissions()
	{
	    Plugin test = getServer().getPluginManager().getPlugin("Permissions");
	    if (Permissions == null)
	    {
	    	if (test != null)
	    	{
	    		Permissions = ((Permissions)test);
	    	}
	    }
	}
	@SuppressWarnings("static-access")
	public static String checkPermissions(Player player)
	{
		if (Permissions != null)
		{
			if (Permissions.Security.permission(player, "torchmissile.Normal"))
				return "normal";
			else if (Permissions.Security.permission(player, "torchmissile.SingleMode"))
				return "single";
			else
				return "nothing";
		}
		else
		{
			if (player.isOp())
				return "normal";
			else
				return "nothing";
		}
	}
}
