package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Vehicle;
import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.Map;

/**
 * gives access to the icons of the different vehicles type
 */
public class VehicleIcons {
    private static final Map<Vehicle, Image> myMap = new EnumMap<>(Vehicle.class);

    private VehicleIcons(){}

    /**
     * provides the Image of the icon corresponding the given vehicle
     * @param vehicle type of the vehicle who's icon we're looking for
     * @return the icon of the vehicle in the format of an Image
     */
    public static Image iconFor(Vehicle vehicle){
        return myMap.computeIfAbsent(vehicle,x -> new Image(x.toString()+".png"));
    }
}
