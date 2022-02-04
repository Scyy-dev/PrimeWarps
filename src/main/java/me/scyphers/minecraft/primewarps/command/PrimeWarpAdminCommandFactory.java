package me.scyphers.minecraft.primewarps.command;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.command.admin.*;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.command.BaseCommand;
import me.scyphers.scycore.command.CommandFactory;
import me.scyphers.scycore.command.commands.ReloadCommand;

import java.util.Map;

public class PrimeWarpAdminCommandFactory extends CommandFactory {

    public PrimeWarpAdminCommandFactory(PrimeWarps plugin, String baseCommandName) {
        super(plugin, baseCommandName, Map.of(
                "changeowner", new ChangeOwnerCommand(plugin, "primewarps.commands.changeowner"),
                "forceinactive", new ForceInactiveCommand(plugin, "primewarps.commands.forceinactive"),
                "nearby", new NearbyCommand(plugin, "primewarps.commands.nearby"),
                "reload", new ReloadCommand(plugin, "primewarps.commands.reload"),
                "remove", new RemoveWarpCommand(plugin, "primewarps.commands.remove"),
                "search", new SearchCommand(plugin, "primewarps.commands.search"),
                "setshard", new SetWarpShardCommand(plugin, "primewarps.commands.setshard"),
                "stats", new StatsCommand(plugin, "primewarps.commands.stats")
        ), (sender -> {
            for (String message : plugin.getSplashText()) {
                plugin.getMessenger().sendChat(sender, message);
            }
        }));
    }
}
