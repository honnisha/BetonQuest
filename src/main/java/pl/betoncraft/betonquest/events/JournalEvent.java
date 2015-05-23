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
package pl.betoncraft.betonquest.events;

import java.util.Date;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.Journal;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.SimpleTextOutput;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class JournalEvent extends QuestEvent {

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public JournalEvent(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        // check if playerID isn't null, this event cannot be static
        if (playerID == null) {
            Debug.error("This event cannot be static: " + instructions);
            return;
        }
        // the event cannot be fired for offline players
        if (PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player " + playerID + " is offline, cannot fire event");
            return;
        }
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Pointer name not specified!");
            return;
        }
        String name = packName + "." + parts[1];
        Journal journal = BetonQuest.getInstance().getDBHandler(playerID).getJournal();
        journal.addPointer(new Pointer(name, new Date().getTime()));
        journal.updateJournal();
        SimpleTextOutput.sendSystemMessage(playerID, Config.getMessage("new_journal_entry"), Config.getString("config.sounds.journal"));
    }

}
