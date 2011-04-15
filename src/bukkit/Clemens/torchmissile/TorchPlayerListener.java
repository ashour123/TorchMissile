package bukkit.Clemens.torchmissile;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class TorchPlayerListener extends PlayerListener
{
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack stack = event.getItem();
		if (stack != null)
		{
			if (stack.getTypeId() == 50)
			{
				TorchMissile.AddClick(player);
			}
		}
	}
}
