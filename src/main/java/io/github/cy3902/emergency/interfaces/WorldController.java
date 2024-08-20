package io.github.cy3902.emergency.interfaces;


import io.github.cy3902.emergency.abstracts.AbstractsEmergency;

public interface WorldController {

    void randomEmergency();
    void startEmergency(String group, AbstractsEmergency abstractsEmergency);
    void pause(String group);

    void resume(String group);
}
