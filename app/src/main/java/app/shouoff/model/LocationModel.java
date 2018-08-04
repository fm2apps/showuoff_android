package app.shouoff.model;

import java.io.Serializable;

/**
 * Created by pro25 on 12/3/18.
 */

public class LocationModel implements Serializable
{
    private String id,name;

    public LocationModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
