package net.kalob.GodPets;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DamageRulesTest {

    @Test
    void everyNormalCauseIsBlocked() {
        for (DamageCause cause : DamageCause.values()) {
            if (cause == DamageCause.KILL || cause == DamageCause.VOID) continue;
            assertTrue(GodPetsPlugin.blocks(cause, true, true), cause + " should be blocked");
        }
    }

    @Test
    void killAndVoidFollowConfig() {
        assertFalse(GodPetsPlugin.blocks(DamageCause.KILL, true, true));
        assertFalse(GodPetsPlugin.blocks(DamageCause.VOID, true, true));
        assertTrue(GodPetsPlugin.blocks(DamageCause.KILL, false, true));
        assertTrue(GodPetsPlugin.blocks(DamageCause.VOID, true, false));
    }
}
