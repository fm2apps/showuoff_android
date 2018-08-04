package app.shouoff.model;

/**
 * Created by pro25 on 27/3/18.
 */

public class MyAdmireModel
{
    private String id,name,image,status,email,nick_name;
    private boolean aBoolean;

    public MyAdmireModel(String id, String name, String image, String status, String email,
                         boolean aBoolean,String nick_name) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.email = email;
        this.aBoolean = aBoolean;
        this.nick_name=nick_name;
    }

    public String getNick_name() {
        return nick_name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }
}
