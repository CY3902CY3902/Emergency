package io.github.cy3902.emergency.interfaces;


import io.github.cy3902.emergency.abstracts.AbstractsWorld;

public interface EmergencyController {

    

    void start(AbstractsWorld world, String group);

    void stop(AbstractsWorld world, String group);


    void pause(AbstractsWorld world, String group);

    void resume(AbstractsWorld world, String group);

    void createBossBar(AbstractsWorld world);

    void removeBossBar(AbstractsWorld world);

}
