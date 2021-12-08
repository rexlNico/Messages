package de.rexlnico.messages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class Messages extends Plugin {

    @Override
    public void onEnable() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            List<List<String>> messages = (List<List<String>>) configuration.getList("messages");
            AtomicInteger msg = new AtomicInteger();
            getProxy().getScheduler().schedule(this, () -> {
                List<String> s = messages.get(msg.get());
                s.forEach(m -> getProxy().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&', m))));
                msg.getAndIncrement();
                if (msg.get() >= messages.size()) msg.set(0);
            }, 0, configuration.getInt("interval"), TimeUnit.MINUTES);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
