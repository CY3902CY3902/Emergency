package io.github.cy3902.emergency.utils;

import io.github.cy3902.emergency.Emergency;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MsgUtils {
    private Emergency emergency;
    public MsgUtils(Emergency emergency){
        this.emergency = emergency;
    }

    //發送訊息
    public void sendMessage(String playerName,String msg){
        Player p = Bukkit.getPlayerExact(playerName);
        if (p != null){
            p.sendMessage(msg(msg));
            return;
        }
    }


    public String msg(String msg)
    {
        return tm(msg);
    }

    //顏色轉換
    private String tm( String textToColor) {
        if (textToColor == null){
            return null;
        }
        return textToColor.replace("&","§");
    }

    public List<String> msg(List<String> msg)
    {
        List<String> lore=new ArrayList<>();
        for (String l:msg)
        {
            lore.add(tm(l));
        }
        return lore;
    }

    private String stripSpaceAfterColorCodes( String textToStrip) {

        textToStrip = textToStrip.replaceAll("(" + ChatColor.COLOR_CHAR + ".)[\\s]", "$1");
        return textToStrip;
    }
}
