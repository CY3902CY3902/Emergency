package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.manager.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;



public abstract class AbstractsWorld{


    protected final Emergency emergency;
    protected World world;
    protected int day;
    protected Map<String, AbstractsEmergency> worldEmergency = new HashMap<>();
    protected Map<String, TaskManager.TaskStatus> groupStates = new HashMap<>();


    protected AbstractsWorld(String worldName) {
        this.emergency = Emergency.getInstance();
        this.world = Bukkit.getWorld(worldName);
        this.day = 0;

        if (this.world == null) {
            emergency.info(emergency.getLang().plugin + emergency.getLang().worldNotFoundMessage, Level.SEVERE);
            return;
        }

    }



    public void randomEmergency() throws SQLException {};


    public void startEmergency(String group,  AbstractsEmergency abstractsEmergency){}


    public void pause(String group){};


    public void resume(String group){};

    // Getter for world
    public World getWorld() {
        return world;
    }



    public int getDay() {
        return day;
    }


    public Map<String, AbstractsEmergency> getWorldEmergency() {
        return worldEmergency;
    }


    public Map<String, TaskManager.TaskStatus> getGroupStates() {
        return groupStates;
    }

}