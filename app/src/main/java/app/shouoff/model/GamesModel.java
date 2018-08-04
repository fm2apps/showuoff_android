package app.shouoff.model;

/**
 * Created by pro25 on 12/3/18.
 */

public class GamesModel
{
    private String id,name;

    public GamesModel(String id, String name) {
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
    public int hashCode()
    {
        return id.hashCode();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
