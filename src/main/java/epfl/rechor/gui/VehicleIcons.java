package epfl.rechor.gui;

import java.util.EnumMap;
import java.util.Map;

import epfl.rechor.journey.Vehicle;
import javafx.scene.image.Image;

/**
 * gives access to the icons of the different vehicles type
 * @author Matthias Gibon (397827)
 */
public final class VehicleIcons {
    private static final Map<Vehicle, Image> myMap = new EnumMap<>(Vehicle.class);

    private VehicleIcons(){}

    /**
     * provides the Image of the icon corresponding the given vehicle
     * @param vehicle type of the vehicle whose icon we're looking for
     * @return the icon of the vehicle in the format of an Image
     */
    public static Image iconFor(Vehicle vehicle){
        return myMap.computeIfAbsent(vehicle,x -> new Image(x.toString()+".png"));
    }
}
