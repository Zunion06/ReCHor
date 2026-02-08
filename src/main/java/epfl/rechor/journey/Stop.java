package epfl.rechor.journey;

import java.util.Objects;

import epfl.rechor.Preconditions;


/**
 * A stop is either a station or a platform, with a name and position
 * @author Matthias Gibon (397827)
 */
public record Stop(String name, String platformName, double longitude, double latitude) {
    private final static int MAX_LONGITUDE = 180;
    private final static int MAX_LATITUDE = 90;
    /**
     * create an instance of Stop with a name and position
     * @param name stop name, can't be null
     * @param platformName platform name
     * @param longitude longitude coordinate of the stop (between 180 and -180)
     * @param latitude latitude coordinate of the stop (between 90 and -90)
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if longitude or latitude are not valid
     */
    public Stop{
        Objects.requireNonNull(name);
        Preconditions.checkArgument(Math.abs(longitude) <= MAX_LONGITUDE
                && Math.abs(latitude) <= MAX_LATITUDE);
    }
}
