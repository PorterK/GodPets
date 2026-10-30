package net.kalob.GodPets;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * GodPets: anything wearing a name tag (any entity with a custom name) can't take damage.
 */
public final class GodPetsPlugin extends JavaPlugin {

    private final Set<String> disabledWorlds = new HashSet<>();
    private boolean allowKillCommand = true;
    private boolean allowVoid = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload();

        getServer().getPluginManager().registerEvents(new NameTagListener(this), this);

        PluginCommand command = getCommand("godpets");
        if (command != null) {
            GodPetsCommand handler = new GodPetsCommand(this);
            command.setExecutor(handler);
            command.setTabCompleter(handler);
        }

        if (Boolean.getBoolean(SelfTest.PROPERTY)) {
            getLogger().warning("Self-test mode enabled: the server will shut down when the test finishes.");
            new SelfTest(this).start();
        }
    }

    public void reload() {
        reloadConfig();
        allowKillCommand = getConfig().getBoolean("allow-kill-command", true);
        allowVoid = getConfig().getBoolean("allow-void", true);
        disabledWorlds.clear();
        for (String world : getConfig().getStringList("disabled-worlds")) {
            disabledWorlds.add(world.toLowerCase(Locale.ROOT));
        }
    }

    /** Whether this entity is protected right now (named, not a player, not in a disabled world). */
    public boolean isProtected(Entity entity) {
        if (entity instanceof Player) return false;
        if (entity.customName() == null) return false;
        return !disabledWorlds.contains(entity.getWorld().getName().toLowerCase(Locale.ROOT));
    }

    /** Whether damage of this cause should be blocked for a protected entity. */
    public boolean blocks(DamageCause cause) {
        return blocks(cause, allowKillCommand, allowVoid);
    }

    static boolean blocks(DamageCause cause, boolean allowKillCommand, boolean allowVoid) {
        if (cause == DamageCause.KILL) return !allowKillCommand;
        if (cause == DamageCause.VOID) return !allowVoid;
        return true;
    }

    public boolean allowKillCommand() {
        return allowKillCommand;
    }

    public boolean allowVoid() {
        return allowVoid;
    }
}
