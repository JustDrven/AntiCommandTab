package me.justdrven.anticommandtab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiCommandTab extends JavaPlugin {

    private ProtocolManager manager;

    @Override
    public void onLoad() {
        this.manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final String source = "ProtocolLib";
        PluginManager pm = this.getServer().getPluginManager();
        if (pm.getPlugin(source) == null) {
            this.onDisable();
            throw new RuntimeException("ProtocolLib is't found!");
        }
        if (!pm.getPlugin(source).getDescription().getVersion().equals("5.0.0")) {
            this.onDisable();
            throw new RuntimeException("This ProtocolLib does not run on version: 5.0.0");
        }
        this.manager.addPacketListener(new PacketAdapter(this,
                PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                String command = event.getPacket().getStrings().read(0);
                Player p = event.getPlayer();
                if ((command.startsWith("/")) &&
                        !p.hasPermission(getConfig().getString("permission")))
                    event.setCancelled(true);
            }
        });
    }

    @Override
    public void onDisable() {
        this.saveConfig();
        this.reloadConfig();
    }
}
