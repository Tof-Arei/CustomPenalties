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
package ch.ar.cp;

import ch.ar.cp.env.Logger;
import ch.ar.cp.listeners.DeathListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Arei
 */
public class CustomPenalties extends JavaPlugin {
    public static final String NAME = "CustomPenalties";
    public static final String SHORTNAME = "CP";
    public static final String VERSION = "1.0.0";
    public static final String MINECRAFT_VERSION = "1.12.2";
    
    private static CustomPenalties instance = null;
    
    private final FileConfiguration config = getConfig();
    

    
    @Override
    public void onEnable() {
        instance = this;
        config();
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        Logger.log(NAME + " version " + VERSION + " started successfully.\n"
                + "Using custom weather : " + config.getBoolean("custom-weather"), Logger.Level.INFO);
    }
    
    private void config() {
        config.addDefault("verbose", false);
        config.addDefault("verbose-level", 0);
        
        saveDefaultConfig();
        reloadConfig();
    }
    
    @Override
    public void saveConfig() {
        config.options().copyDefaults(true);
        super.saveConfig();
        reloadConfig();
    }
    
    public static CustomPenalties getPlugin() {
        return instance;
    }
}
