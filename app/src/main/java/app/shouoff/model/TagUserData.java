package app.shouoff.model;

import java.io.Serializable;

public class TagUserData implements Serializable
{
    private String id,username,nickname;

    public TagUserData(String id, String username, String nickname)
    {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }
}
