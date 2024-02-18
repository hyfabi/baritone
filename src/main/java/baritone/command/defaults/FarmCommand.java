/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.cache.IWaypoint;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.ForBlockOptionalMeta;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.command.datatypes.IDatatypeFor;
import baritone.api.command.exception.CommandException;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMeta;
import baritone.process.FarmProcess;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FarmCommand extends Command {

    public FarmCommand(IBaritone baritone) {
        super(baritone, "farm");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {

        Integer range = null;
        BetterBlockPos origin = null;
        //waypoint
        if (args.has(1)) {
            range = args.getAsOrDefault(Integer.class, null);
            if(range != null && range < 0){
                IWaypoint[] waypoints = args.getDatatypeFor(ForWaypoints.INSTANCE);
                IWaypoint waypoint = null;
                switch (waypoints.length) {
                    case 0:
                        throw new CommandInvalidStateException("No waypoints found");
                    case 1:
                        waypoint = waypoints[0];
                        break;
                    default:
                        throw new CommandInvalidStateException("Multiple waypoints were found");
                }
                origin = waypoint.getLocation();
            }
        }

        ArrayList<String> harvestAbles = new ArrayList<>();

        while(args.hasAny()){
            harvestAbles.add(args.getString());
        }

        baritone.getFarmProcess().farm(range != null ? range : 100, origin, harvestAbles.toArray(new String[]{}));
        logDirect("Farming");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        try{
            int i = args.peekAs(Integer.class);
        }catch (Exception ignored){}

        return Stream.of(FarmProcess.HarvestAble.values()).map(e -> e.block.builtInRegistryHolder().key().location().toString());
    }

    @Override
    public String getShortDesc() {
        return "Farm nearby crops";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The farm command starts farming nearby plants. It harvests mature crops and plants new ones.",
                "",
                "Usage:",
                "> farm - farms every crop it can find.",
                "> farm <range> - farm crops within range from the starting position.",
                "> farm <range> <waypoint> - farm crops within range from waypoint."
        );
    }
}
