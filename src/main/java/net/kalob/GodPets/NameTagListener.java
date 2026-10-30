package net.kalob.GodPets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;

final class NameTagListener implements Listener {

    private final GodPetsPlugin plugin;

    NameTagListener(GodPetsPlugin plugin) {
        this.plugin = plugin;
    }

    /** Named mobs take no damage from anything: players, mobs, fire, falls, drowning, explosions... */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!plugin.isProtected(event.getEntity())) return;
        if (plugin.blocks(event.getCause())) {
            event.setCancelled(true);
        }
    }

    /** Keeps named mobs from being set on fire (sunlight on undead, lava, fire aspect) so they don't burn forever. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombust(EntityCombustEvent event) {
        if (plugin.isProtected(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
