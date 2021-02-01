package ramonper.tablistapi.tablist;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ramonper.tablistapi.utils.*;

public class TabList {

    private final File file;
    private final YamlConfiguration config;

    public TabList(String path) {
        file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void sendTabListToPlayer(Player player, Object packet) {
        ReflectionUtils.sendPacket(player, packet);
    }

    /**
     * This method is a workaround for a faster creation of a TabList.
     * This should be made on the config.yml.
     *
     * @param tabList the TabList whose its to be selected
     *                Ex.: defaultTabList("lobbyTabList") get lobbyTabList TabList on the config.yml
     */
    public Object fromConfig(String tabList) {
        Object packet = null;
        try {
            final String header = config.getString("tablists." + tabList + ".header");
            final String footer = config.getString("tablists." + tabList + ".footer");
            packet = create(header, footer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

    public void createOnConfig(String name, String header, String footer) {
        try {
            config.set("tablists." + name + ".header",
                    header);
            config.set("tablists." + name + ".footer",
                    footer);
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object create(String header, String footer) {
        Object packet = null;
        try {
            final Class<?> ppop = ReflectionUtils.getNMSClass("PacketPlayOutPlayerListHeaderFooter");
            packet = ppop.newInstance();

            final Class<?> icbc = ReflectionUtils.getNMSClass("IChatBaseComponent");

            final Method a = icbc.getDeclaredClasses()[0].getMethod("a", String.class);

            final Object tabHeader = a.invoke(null, "{\"text\":\"" + header + "\"}");
            final Object tabFooter = a.invoke(null, "{\"text\":\"" + footer + "\"}");

            final Field footerFields = ppop.getDeclaredFields()[0];
            footerFields.set(packet, tabHeader);
            footerFields.set(packet, tabFooter);

            final Field headerFields = ppop.getDeclaredFields()[0];
            headerFields.set(packet, tabHeader);
            headerFields.set(packet, tabFooter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packet;
    }

}
