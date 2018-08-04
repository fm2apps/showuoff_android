package app.shouoff.model;

public class ModificationModel
{
    private String id,name,price,currency;
    private boolean aBoolean;

    public ModificationModel(String id, String name, String price, boolean aBoolean,String currency) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.aBoolean = aBoolean;
        this.currency=currency;
    }

    public String getCurrency() {
        return currency;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }
}
