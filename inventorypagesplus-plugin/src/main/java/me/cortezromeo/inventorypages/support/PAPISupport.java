package me.cortezromeo.inventorypages.support;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.cortezromeo.inventorypages.InventoryPages;
import me.cortezromeo.inventorypages.manager.DatabaseManager;
import org.bukkit.entity.Player;

public class PAPISupport extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return InventoryPages.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "inventorypagesplus";
    }

    @Override
    public String getVersion() {
        return InventoryPages.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (s == null)
            return null;

        if (s.equals("page"))
            DatabaseManager.playerInvs.get(player.getUniqueId().toString()).getPage();

        if (s.equals("maxpage"))
            DatabaseManager.playerInvs.get(player.getUniqueId().toString()).getMaxPage();

        return null;
    }
}