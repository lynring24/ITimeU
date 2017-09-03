package com.itti7.itimeu;

import android.app.Application;

/**
 * Created by Admin on 2017-09-03.
 */

public class Util extends Application {
    private static int stateCounter;

    public void onCreate()
    {
        super.onCreate();
        stateCounter = 0;
    }

    /**
     * @return true if application is on background
     * */
    public static boolean isApplicationOnBackground()
    {
        return stateCounter == 0;
    }

    //to be called on each Activity onStart()
    public static void activityStarted()
    {
        stateCounter++;
    }

    //to be called on each Activity onStop()
    public static void activityStopped()
    {
        stateCounter--;
    }
}
