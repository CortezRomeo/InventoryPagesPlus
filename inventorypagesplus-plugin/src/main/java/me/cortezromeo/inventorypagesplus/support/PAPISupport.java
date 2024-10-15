package me.cortezromeo.inventorypagesplus.support;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import org.bukkit.entity.Player;

public class PAPISupport extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return InventoryPagesPlus.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "inventorypagesplus";
    }

    @Override
    public String getVersion() {
        return InventoryPagesPlus.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (s == null)
            return null;

        if (s.equals("page"))
            InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(player.getUniqueId()).getCurrentPage();

        if (s.equals("maxpage"))
            InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(player.getUniqueId()).getMaxPage();

        return null;
    }
}