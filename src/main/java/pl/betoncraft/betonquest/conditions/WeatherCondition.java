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
package pl.betoncraft.betonquest.conditions;

import org.bukkit.World;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the weather to be of specific type
 * 
 * @author Co0sh
 */
public class WeatherCondition extends Condition {

    private String weather;
    private World world;

    public WeatherCondition(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Weather type not defined in weather condition: " + instructions);
            isOk = false;
            return;
        }
        weather = parts[1];
        world = PlayerConverter.getPlayer(playerID).getWorld();
    }

    @Override
    public boolean isMet() {
        switch (weather.toLowerCase().trim()) {
            case "sun":
                if (!world.isThundering() && !world.hasStorm()) {
                    return true;
                }
                break;
            case "rain":
                if (world.hasStorm()) {
                    return true;
                }
                break;
            case "storm":
                if (world.isThundering()) {
                    return true;
                }
                break;
            default:
                Debug.error("Weather type '" + weather + "' not defined!");
                break;
        }
        return false;
    }

}
