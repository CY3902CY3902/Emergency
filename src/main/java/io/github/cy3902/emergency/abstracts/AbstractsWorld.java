package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.interfaces.WorldController;
import io.github.cy3902.emergency.task.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.logging.Level;



public abstract class AbstractsWorld implements WorldController{


    protected final Emergency emergency;
    protected World world;
    protected int day;
    protected List<AbstractsEmergency> worldEmergency = new ArrayList<>();
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


    @Override
    public void randomEmergency(){};

    @Override
    public void startEmergency(String group,  AbstractsEmergency abstractsEmergency){}

    @Override
    public void pause(String group){};

    @Override
    public void resume(String group){};

    // Getter for world
    public World getWorld() {
        return world;
    }



    public int getDay() {
        return day;
    }


    public List<AbstractsEmergency> getWorldEmergency() {
        return worldEmergency;
    }

    public void setWorldEmergency(List<AbstractsEmergency> worldEmergency) {
        this.worldEmergency = worldEmergency;
    }

    public Map<String, TaskManager.TaskStatus> getGroupStates() {
        return groupStates;
    }

}