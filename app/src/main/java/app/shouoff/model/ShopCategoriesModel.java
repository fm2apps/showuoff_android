package app.shouoff.model;

public class ShopCategoriesModel
{
    private String id,name,image;
    private boolean aBoolean;

    public ShopCategoriesModel(String id, String name, String image,boolean aBoolean)
    {
        this.id = id;
        this.name = name;
        this.image = image;
        this.aBoolean=aBoolean;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
