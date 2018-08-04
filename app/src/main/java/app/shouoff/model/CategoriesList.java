package app.shouoff.model;

public class CategoriesList
{
    private String id,name;
    public CategoriesList(String id,String name)
    {
        this.id=id;
        this.name=name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
