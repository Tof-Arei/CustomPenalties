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

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author Arei
 */
public class DeathListener implements Listener {
    private FileConfiguration config;
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        config = Bukkit.getServer().getPluginManager().getPlugin("WeatherControl").getConfig();
        
        // Overrides default death penalties settings.
        e.setKeepInventory(true);
        e.setKeepLevel(true);
        
        if (config.getBoolean("lose-exp")) {
            loseExp(e);
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
    
    private void loseExp(PlayerDeathEvent e) {
        int newExp = (int) (e.getEntity().getExp() * config.getInt("exp-penalty")) / 100;
        int newTotalExp = (e.getEntity().getTotalExperience() * config.getInt("exp-penalty")) / 100;
        int newLevel = (e.getEntity().getLevel() * config.getInt("exp-penalty")) / 100;
        
        e.setNewExp(newExp);
        e.setNewTotalExp(newTotalExp);
        e.setNewLevel(newLevel);
    }
    
    private void loseBackpack(PlayerDeathEvent e) {
        PlayerInventory inventory = e.getEntity().getInventory();
        
        for (int i = 8; i < inventory.getContents().length; i++) {
            inventory.remove(inventory.getContents()[i]);
        }
    }
    
    private void loseBelt(PlayerDeathEvent e) {
        PlayerInventory inventory = e.getEntity().getInventory();
        
        for (int i = 0; i < 9; i++) {
            inventory.remove(inventory.getContents()[i]);
        }
    }
    
    private void loseEquipment(PlayerDeathEvent e) {
        PlayerInventory inventory = e.getEntity().getInventory();
        
        for (int i = 0; i < inventory.getArmorContents().length; i++) {
            inventory.remove(inventory.getArmorContents()[i]);
        }
    }
    
    private void lowerDurability(PlayerDeathEvent e) {
        PlayerInventory inventory = e.getEntity().getInventory();
        
        for (int i = 0; i < inventory.getArmorContents().length; i++) {
            ItemStack item = inventory.getArmorContents()[i];
            short newDura = (short) (item.getDurability() * config.getInt("dura-penalty") / 100);
            if (!config.getBoolean("item-break") && newDura < 1) {
                newDura = 1;
            }
            
            item.setDurability(newDura);
        }
    }
}
