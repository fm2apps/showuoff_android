package app.shouoff.model;

import java.io.File;
import java.io.Serializable;

public class PostCreateIVModel implements Serializable
{
    private String id,type;
    private File attachment,video;

    public PostCreateIVModel(String id, String type, File attachment,File video)
    {
        this.id = id;
        this.type = type;
        this.attachment = attachment;
        this.video=video;
    }

    public File getVideo() {
        return video;
    }

    public void setVideo(File video) {
        this.video = video;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public File getAttachment()
    {
        return attachment;
    }

    public void setAttachment(File attachment)
    {
        this.attachment = attachment;
    }
}
