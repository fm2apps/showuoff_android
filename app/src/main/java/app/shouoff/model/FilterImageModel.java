package app.shouoff.model;

public class FilterImageModel
{
    private String id;
    private Float height,weight;

    public FilterImageModel(String id,Float height,Float weight) {
        this.id = id;
        this.height=height;
        this.weight=weight;
    }

    public Float getHeight() {
        return height;
    }

    public Float getWeight() {
        return weight;
    }

    public String getId() {
        return id;
    }
}
