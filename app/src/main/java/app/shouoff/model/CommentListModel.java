package app.shouoff.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentListModel implements Serializable
{
    private String id,user_id,description,created_at,first_name,family_name,image;
    private ArrayList<TagUserData> tagUserData;

    public CommentListModel(String id, String user_id, String description,
                            String created_at, String first_name, String family_name,
                            String image,ArrayList<TagUserData> tagUserData)
    {
        this.id = id;
        this.user_id = user_id;
        this.description = description;
        this.created_at = created_at;
        this.first_name = first_name;
        this.family_name = family_name;
        this.image = image;
        this.tagUserData=tagUserData;
    }

    public ArrayList<TagUserData> getTagUserData() {
        return tagUserData;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public String getImage() {
        return image;
    }
}
