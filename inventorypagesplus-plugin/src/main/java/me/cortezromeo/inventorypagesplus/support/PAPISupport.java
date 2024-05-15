package me.cortezromeo.inventorypagesplus.support;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
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
            DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).getPage();

        if (s.equals("maxpage"))
            DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).getMaxPage();

        return null;
    }
}