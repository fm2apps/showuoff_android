package app.shouoff.model;

public class BlockedUserListModel
{
    private String id,email,nick_name,image,username;

    public BlockedUserListModel(String id, String email, String nick_name, String image, String username)
    {
        this.id = id;
        this.email = email;
        this.nick_name = nick_name;
        this.image = image;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNick_name() {
        return nick_name;
    }

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }
}
