package net.kalob.GodPets;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Zombie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * In-server integration test, run by CI against a real Paper server:
 * {@code java -Dgodpets.selftest=true -jar paper.jar}.
 * Writes {@code plugins/GodPets/selftest-result.txt} and shuts the server down.
 */
final class SelfTest {

    static final String PROPERTY = "godpets.selftest";

    private final GodPetsPlugin plugin;
    private final List<String> failures = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();
    private Location origin;

    SelfTest(GodPetsPlugin plugin) {
        this.plugin = plugin;
    }

    void start() {
        Bukkit.getScheduler().runTaskLater(plugin, this::runChecks, 40L);
    }

    private void runChecks() {
        plugin.getLogger().info("Starting self-test");
        step("prepare world", this::prepareWorld);
        step("named mob survives huge damage", this::checkNamedSurvives);
        step("unnamed mob still dies", this::checkUnnamedDies);
        step("named hostile mob survives", this::checkNamedHostile);
        step("/kill still removes named mobs", this::checkKillCommand);
        step("reload", () -> { for (int i = 0; i < 3; i++) plugin.reload(); });
        // Let the server tick so deaths/removals register before finishing.
        Bukkit.getScheduler().runTaskLater(plugin, this::finish, 40L);
    }

    private void prepareWorld() {
        World world = Bukkit.getWorlds().getFirst();
        origin = world.getSpawnLocation().add(0.5, 1, 0.5);
        Chunk chunk = origin.getChunk();
        world.setChunkForceLoaded(chunk.getX(), chunk.getZ(), true);
    }

    private void checkNamedSurvives() {
        Cow cow = origin.getWorld().spawn(origin, Cow.class);
        cow.customName(Component.text("Bessie"));
        double before = cow.getHealth();
        cow.damage(10_000);
        expect(!cow.isDead(), "named cow died from damage()");
        expect(cow.getHealth() == before, "named cow lost health: " + before + " -> " + cow.getHealth());
        cow.remove();
    }

    private void checkUnnamedDies() {
        Cow cow = origin.getWorld().spawn(origin, Cow.class);
        cow.damage(10_000);
        expect(cow.isDead() || cow.getHealth() <= 0, "unnamed cow should have died");
        cow.remove();
    }

    private void checkNamedHostile() {
        Zombie zombie = origin.getWorld().spawn(origin, Zombie.class);
        zombie.customName(Component.text("Gerald"));
        zombie.damage(10_000);
        expect(!zombie.isDead(), "named zombie died");
        zombie.remove();
    }

    private void checkKillCommand() {
        Cow cow = origin.getWorld().spawn(origin, Cow.class);
        cow.customName(Component.text("KillMe"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill " + cow.getUniqueId());
        if (plugin.allowKillCommand()) {
            expect(cow.isDead() || !cow.isValid(), "/kill should remove a named cow when allow-kill-command is true");
        } else {
            expect(!cow.isDead(), "/kill should not work when allow-kill-command is false");
        }
        cow.remove();
    }

    private void step(String name, Runnable check) {
        try {
            check.run();
            notes.add("ok: " + name);
        } catch (Exception | LinkageError e) {
            failures.add(name + " threw " + e);
        }
    }

    private void expect(boolean condition, String message) {
        if (!condition) failures.add(message);
    }

    private void finish() {
        List<String> lines = new ArrayList<>();
        lines.add(failures.isEmpty() ? "PASS" : "FAIL");
        failures.forEach(f -> lines.add("FAIL: " + f));
        lines.addAll(notes);
        try {
            Path out = plugin.getDataFolder().toPath().resolve("selftest-result.txt");
            Files.createDirectories(out.getParent());
            Files.write(out, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not write self-test result: " + e);
        }
        lines.forEach(plugin.getLogger()::info);
        Bukkit.shutdown();
    }
}
