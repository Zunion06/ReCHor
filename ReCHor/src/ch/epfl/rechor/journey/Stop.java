package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;


public record Stop(String name, String platformName, double longitude, double latitude) {
    /**
     * a Stop is either a station or a platform, with a name and position
     * @param name stop name, can't be null
     * @param platformName platform name
     * @param longitude longitude coordinate of the stop (between 180 and -180)
     * @param latitude latitude coordinate of the stop (between 90 and -90)
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if longitude or latitude are not valid
     */
    public Stop{
        Objects.requireNonNull(name);
        Preconditions.checkArgument(Math.abs(longitude)<=180&&Math.abs(latitude)<=90);
    }
}
