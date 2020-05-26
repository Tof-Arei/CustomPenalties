/*
 *                GLWT(Good Luck With That) Public License
 *                  Copyright (c) Everyone, except Author
 * 
 * Everyone is permitted to copy, distribute, modify, merge, sell, publish,
 * sublicense or whatever they want with this software but at their OWN RISK.
 * 
 *                             Preamble
 * 
 * The author has absolutely no clue what the code in this project does.
 * It might just work or not, there is no third option.
 * 
 * 
 *                 GOOD LUCK WITH THAT PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION, AND MODIFICATION
 * 
 *   0. You just DO WHATEVER YOU WANT TO as long as you NEVER LEAVE A
 * TRACE TO TRACK THE AUTHOR of the original product to blame for or hold
 * responsible.
 * 
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * Good luck and Godspeed.
 */
package ch.ar.cp.listeners;

import ch.ar.cp.env.Logger;
import ch.ar.cp.minecraft.env.ItemsUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author Arei
 */
public class DeathListener implements Listener {
    private FileConfiguration config;
    private final Map<UUID, Integer> hmPlayerLevel = new HashMap<>();
    private final Map<UUID, Float> hmPlayerExp = new HashMap<>();
    private final Map<UUID, Integer> hmPlayerTotalExp = new HashMap<>();
    private final Map<UUID, PlayerInventory> hmPlayerInv = new HashMap<>();
    
    @EventHandler
    private void onEntityDamage(EntityDamageEvent e) {
        config = Bukkit.getServer().getPluginManager().getPlugin("CustomPenalties").getConfig();
        
        if (e.getEntity() instanceof Player) {
            Logger.log(e.getEntity().getName() + " took " + e.getDamage() + " damage from " + e.getCause(), Logger.Level.DEBUG);
            
            Player player = (Player) e.getEntity();
            
            // Is player dying ?
            if (e.getDamage() >= player.getHealth() || player.getHealth() <= 0) {
                // Register player current level, exp, total exp and inventory.
                hmPlayerLevel.put(player.getUniqueId(), player.getLevel());
                hmPlayerExp.put(player.getUniqueId(), player.getExp());
                hmPlayerTotalExp.put(player.getUniqueId(), player.getTotalExperience());
                hmPlayerInv.put(player.getUniqueId(), player.getInventory());
            }
        }
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        config = Bukkit.getServer().getPluginManager().getPlugin("CustomPenalties").getConfig();
        
        Logger.log(e.getEntity().getName() + " just died.", Logger.Level.DEVENT);
        
        // Overrides default death penalties settings.
        e.setKeepInventory(true);
        e.setKeepLevel(true);
        
        dropExp(e);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        config = Bukkit.getServer().getPluginManager().getPlugin("CustomPenalties").getConfig();
        
        Logger.log(e.getPlayer().getName() + " just respawned.", Logger.Level.DEBUG);
        
        // Custom penalties handling.
        if (config.getBoolean("lose-exp")) {
            loseExp(e.getPlayer());
        }
        if (config.getBoolean("lose-backpack")) {
            loseBackpack(e);
        }
        if (config.getBoolean("lose-belt")) {
            loseBelt(e);
        }
        if (config.getBoolean("lose-equipment")) {
            loseEquipment(e);
        } else {
            lowerDurability(e);
        }
    }
    
    private void dropExp(PlayerDeathEvent e) {
        int droppedExp = (hmPlayerTotalExp.get(e.getEntity().getUniqueId()) * config.getInt("exp-penalty")) / 100;
        droppedExp += (hmPlayerExp.get(e.getEntity().getUniqueId()) * config.getInt("exp-penalty")) / 100;
        e.setDroppedExp(droppedExp);
        
        Logger.log(e.getEntity().getName() + " dropped " + droppedExp + " exp.", Logger.Level.DEBUG);
    }
    
    private void loseExp(Player player) {
        int newLevel = (hmPlayerLevel.get(player.getUniqueId()) * config.getInt("exp-penalty")) / 100;
        float newExp = (hmPlayerExp.get(player.getUniqueId()) * config.getInt("exp-penalty")) / 100;
        int newTotalExp = (hmPlayerTotalExp.get(player.getUniqueId()) * config.getInt("exp-penalty")) / 100;
        
        player.setLevel(newLevel);
        player.setExp(newExp);
        player.setTotalExperience(newTotalExp);
        
        Logger.log(player.getName() + " new level : " + newLevel, Logger.Level.DEBUG);
        Logger.log(player.getName() + " new exp : " + newExp, Logger.Level.DEBUG);
        Logger.log(player.getName() + " new total exp : " + newTotalExp, Logger.Level.DEBUG);
    }
    
    private void loseBackpack(PlayerRespawnEvent e) {
        PlayerInventory inventory = hmPlayerInv.get(e.getPlayer().getUniqueId());
        
        for (int i = 8; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getContents()[i];
            inventory.remove(item);
            Logger.log(e.getPlayer().getName() + " lost backpack item : " + item, Logger.Level.DEBUG);
        }
    }
    
    private void loseBelt(PlayerRespawnEvent e) {
        PlayerInventory inventory = hmPlayerInv.get(e.getPlayer().getUniqueId());
        
        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.getContents()[i];
            inventory.remove(item);
            Logger.log(e.getPlayer().getName() + " lost belt item : " + item, Logger.Level.DEBUG);
        }
    }
    
    private void loseEquipment(PlayerRespawnEvent e) {
        PlayerInventory inventory = hmPlayerInv.get(e.getPlayer().getUniqueId());
        
        for (int i = 0; i < inventory.getArmorContents().length; i++) {
            ItemStack equipment = inventory.getContents()[i];
            inventory.remove(equipment);
            Logger.log(e.getPlayer().getName() + " lost equipment : " + equipment, Logger.Level.DEBUG);
        }
    }
    
    private void lowerDurability(PlayerRespawnEvent e) {
        PlayerInventory inventory = hmPlayerInv.get(e.getPlayer().getUniqueId());
        
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getContents()[i];
            if (item != null) {
                if (ItemsUtils.isEquipment(item)) {
                    setNewDura(e.getPlayer(), item);
                }
            }
        }
    }
    
    private void setNewDura(Player player, ItemStack item) {
        short maxDura = item.getType().getMaxDurability();
        short currentDura = item.getDurability();
        short newDura = item.getDurability();
        
        if (config.getString("dura-method").equals("absolute")) {
            newDura = (short) ((maxDura * config.getInt("dura-penalty")) / 100);
        } else if (config.getString("dura-method").equals("relative")) {
            newDura = (short) (currentDura + ((maxDura - currentDura) * config.getInt("dura-penalty")) / 100);
        }
        
        if (!config.getBoolean("item-break") && newDura >= maxDura) {
            newDura = (short) (maxDura - 1);
        }

        item.setDurability(newDura);
        Logger.log(player.getName() + " item : " + item + " lost " + newDura + " durability.", Logger.Level.DEBUG);
    }
}
