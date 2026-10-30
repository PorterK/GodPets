package net.kalob.GodPets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

final class GodPetsCommand implements TabExecutor {

    private final GodPetsPlugin plugin;

    GodPetsCommand(GodPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("godpets.admin")) {
                sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
                return true;
            }
            plugin.reload();
            sender.sendMessage(Component.text("GodPets config reloaded.", NamedTextColor.GREEN));
            return true;
        }
        sender.sendMessage(Component.text("GodPets: anything with a name tag can't die.", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/kill works on named mobs: " + plugin.allowKillCommand()
                + " | the void works: " + plugin.allowVoid(), NamedTextColor.GRAY));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("godpets.admin")) {
            return "reload".startsWith(args[0].toLowerCase()) ? List.of("reload") : List.of();
        }
        return List.of();
    }
}
