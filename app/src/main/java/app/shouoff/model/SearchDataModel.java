package app.shouoff.model;

import java.io.Serializable;

public class SearchDataModel implements Serializable
{
    private String id,name,family_name,email,image,user_name,nick;
    private boolean aBoolean;

    public SearchDataModel(String id, String name, String family_name, String email,String image,String user_name,String nick)
    {
        this.id = id;
        this.name = name;
        this.family_name = family_name;
        this.email = email;
        this.image=image;
        this.user_name=user_name;
        this.nick=nick;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getNick() {
        return nick;
    }

    public SearchDataModel(String id, String name, String family_name, String email, String image, boolean aBoolean) {
        this.id = id;
        this.name = name;
        this.family_name = family_name;
        this.email = email;
        this.image = image;
        this.aBoolean = aBoolean;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return user_name;
    }
}
