/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * StaticEvents contains logic for running events that aren't tied to any player
 * 
 * @author Coosh
 */
public class StaticEvents {
    
    /**
     * Contains pointers to timers, so they can be canceled if needed
     */
    private static ArrayList<EventTimer> timers = new ArrayList<>(); 

    /**
     * Creates new instance of a StaticEvents object, scheduling static
     * events to run at specified times
     */
    public StaticEvents() {
        Debug.info("Initializing static events");
        // old timers need to be deleted in case of reloading the plugin
        boolean deleted = false;
        for (EventTimer eventTimer : timers) {
            eventTimer.cancel();
            deleted = true;
        }
        if (deleted) {
            Debug.info("Previous timers has been canceled");
        }
        for (String packName : Config.getPackageNames()) {
            Debug.info("Searching package " + packName);
            ConfigPackage pack = Config.getPackage(packName);
            // get those hours and events
            ConfigurationSection config = pack.getMain().getConfig().getConfigurationSection("static");
            if (config == null) {
                Debug.info("There are no static events defined, skipping");
                return;
            }
            // for each hour, create an event timer
            for (String key : config.getKeys(false)) {
                final String value = config.getString(key);
                long timeStamp = getTimestamp(key);
                if (timeStamp < 0) {
                    Debug.error("Incorrect time value in static event declaration (" + key
                        + "), skipping this one");
                    continue;
                }
                Debug.info("Scheduling static event " + value + " at hour " + key + ". Current "
                    + "timestamp: " + new Date().getTime() + ", target timestamp: " + timeStamp);
                // add the timer to static list, so it can be canceled if needed
                timers.add(new EventTimer(timeStamp, value));
            }
        }
        Debug.info("Static events initialization done");
    }
    
    /**
     * Cancels all scheduled timers
     */
    public static void stop() {
        Debug.info("Killing all timers on disable");
        for (EventTimer timer : timers) {
            timer.cancel();
        }
    }
    
    /**
     * Generates a timestamp closest to the specified hour
     * 
     * @param hour time of the day
     * @return timestamp representing next occurence of specified hour
     */
    private long getTimestamp(String hour) {
        // get the current day and add the given hour to it
        Date time = new Date();
        String timeString = new SimpleDateFormat("dd.MM.yy").format(time) + " " + hour;
        // convert it into a timestamp
        long timeStamp = -1;
        try {
            timeStamp = new SimpleDateFormat("dd.MM.yy HH:mm").parse(timeString).getTime();
        } catch (ParseException e) {
            Debug.error("Error in time setting in static event declaration: " + hour);
        }
        // if the timestamp is too old, add one day to it
        if (timeStamp < new Date().getTime()) {
            timeStamp += (24*60*60*1000);
        }
        return timeStamp;
    }
    
    /**
     * EventTimer represents a timer for an event
     * 
     * @author Coosh
     */
    private class EventTimer extends TimerTask {
        
        protected final String event;
        protected final String pack;
        
        /**
         * Creates and schedules a new timer for specified event, based on given timeStamp
         * 
         * @param timeStamp
         * @param event
         */
        public EventTimer(long timeStamp, String eventID) {
            String[] parts = eventID.split("\\.");
            this.event = parts[1];
            this.pack = parts[0];
            new Timer().schedule(this, timeStamp - new Date().getTime(), 24*60*60*1000);
        }

        @Override
        public void run() {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // run the event in sync
                    Debug.info("Firing static event " + pack + "." + event);
                    BetonQuest.event(null, pack, event);
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

}
