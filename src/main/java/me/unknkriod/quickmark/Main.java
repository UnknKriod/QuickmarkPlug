package me.unknkriod.quickmark;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin implements PluginMessageListener {
    private final Map<UUID, Long> playerLastPing = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "quickmark:main", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "quickmark:main");

        // Периодическая очистка старых пингов
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            long currentTime = System.currentTimeMillis();
            playerLastPing.entrySet().removeIf(entry ->
                    currentTime - entry.getValue() > 30000); // 30 секунд
        }, 6000L, 6000L);

        getLogger().info("QuickMark plugin enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getLogger().info("QuickMark plugin disabled!");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!"quickmark:main".equals(channel)) return;

        String payloadStr = new String(message, StandardCharsets.UTF_8);
        if ("PING".equals(payloadStr)) {
            playerLastPing.put(player.getUniqueId(), System.currentTimeMillis());
            player.sendPluginMessage(this, "quickmark:main", "PONG".getBytes(StandardCharsets.UTF_8));
            return;
        }

        byte[] binary;
        try {
            String base85 = new String(message, StandardCharsets.UTF_8);
            binary = Base85Encoder.decode(base85);
        } catch (Exception e) {
            getLogger().warning("Invalid Base85 from " + player.getName() + ": " + e.getMessage());
            return;
        }

        if (binary.length == 0) return;
        char type = (char) (binary[0] & 0xFF);

        switch (type) {
            case 'I', 'R' -> handleTargetedMessage(binary, message);
            case 'M', 'X', 'T', 'J' -> broadcast(player, message);
            default -> getLogger().warning("Unknown type: " + (int)type + " (char: '" + type + "')");
        }
    }

    private void handleTargetedMessage(byte[] data, byte[] message) {
        if (data.length < 33) return;

        UUID targetId = Base85Encoder.bytesToUuid(data, 1);
        Player target = Bukkit.getPlayer(targetId);
        if (target == null || !hasQuickMark(target)) return;

        target.sendPluginMessage(this, "quickmark:main", message);
    }

    private void broadcast(Player sender, byte[] message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getUniqueId().equals(sender.getUniqueId()) && hasQuickMark(p)) {
                p.sendPluginMessage(this, "quickmark:main", message);
            }
        }
    }

    private boolean hasQuickMark(Player player) {
        return playerLastPing.containsKey(player.getUniqueId());
    }
}