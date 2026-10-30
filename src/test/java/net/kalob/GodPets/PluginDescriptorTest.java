package net.kalob.GodPets;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PluginDescriptorTest {

    private static YamlConfiguration load(String name) throws Exception {
        try (InputStream in = PluginDescriptorTest.class.getClassLoader().getResourceAsStream(name)) {
            assertNotNull(in, name + " missing");
            return YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }

    @Test
    void mainClassExistsAndIsAPlugin() throws Exception {
        String main = load("plugin.yml").getString("main");
        assertEquals("net.kalob.GodPets.GodPetsPlugin", main);
        Class<?> cls = Class.forName(main, false, getClass().getClassLoader());
        assertTrue(JavaPlugin.class.isAssignableFrom(cls));
    }

    @Test
    void targetsMinecraft26_3() throws Exception {
        assertEquals("26.3", load("plugin.yml").getString("api-version"));
    }

    @Test
    void versionWasFilledInByTheBuild() throws Exception {
        String version = load("plugin.yml").getString("version");
        assertNotNull(version);
        assertFalse(version.contains("${"), "version placeholder was not expanded: " + version);
    }

    @Test
    void commandAndPermissionAreDeclared() throws Exception {
        YamlConfiguration yml = load("plugin.yml");
        assertTrue(yml.isConfigurationSection("commands.godpets"));
        assertEquals("op", yml.getString("permissions.godpets.admin.default"));
    }

    @Test
    void defaultConfigLetsKillAndVoidThrough() throws Exception {
        YamlConfiguration config = load("config.yml");
        assertTrue(config.getBoolean("allow-kill-command"));
        assertTrue(config.getBoolean("allow-void"));
        assertTrue(config.getStringList("disabled-worlds").isEmpty());
    }
}
