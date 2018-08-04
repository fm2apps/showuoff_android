package app.shouoff.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopModel implements Serializable
{
    private String id,name,price,description,cat_id,currency;
    private ArrayList<String> strings;

    public ShopModel(String id, String name, String price, String description, ArrayList<String> strings,String cat_id,
                     String currency) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.strings = strings;
        this.cat_id=cat_id;
        this.currency=currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCat_id() {
        return cat_id;
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

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }
}
