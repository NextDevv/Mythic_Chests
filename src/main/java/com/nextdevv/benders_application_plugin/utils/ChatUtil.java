package com.nextdevv.benders_application_plugin.utils;

import net.md_5.bungee.api.ChatColor;

/**
 * Utility class for chat
 *
 * @author giovanni
 */
public class ChatUtil {
    /**
     * Translates alternate color codes in the given text string.
     *
     * @param text the text containing alternate color codes
     * @return the text with translated color codes
     */
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
