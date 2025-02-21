package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;

public record Stop(String name, String plateformName, double longitude, double latitude) {
    public Stop{
        Objects.requireNonNull(name);
        Preconditions.checkArgument(Math.abs(longitude)<=180&&Math.abs(latitude)<=90);
    }
}
