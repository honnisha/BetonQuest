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

import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to wear this item as an armor
 * 
 * @author Co0sh
 */
public class ArmorCondition extends Condition {

    private QuestItem item;

    public ArmorCondition(String playerID, String pack, String instructions) {
        super(playerID, pack, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Armor not defined in: " + instructions);
            isOk = false;
            return;
        }
        String itemInstruction = super.pack.getString("items." + parts[1]);
        if (itemInstruction == null) {
            Debug.error("No such item: " + parts[1]);
            isOk = false;
            return;
        }
        item = new QuestItem(itemInstruction);
    }

    @Override
    public boolean isMet() {
        for (ItemStack armor : PlayerConverter.getPlayer(playerID).getEquipment().getArmorContents()) {
            if (item != null && item.equalsI(armor)) {
                return true;
            }
        }
        return false;
    }

}
