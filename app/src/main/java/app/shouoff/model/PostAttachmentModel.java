package app.shouoff.model;

import java.io.Serializable;

/**
 * Created by pro25 on 15/3/18.
 */

public class PostAttachmentModel implements Serializable
{
    private String attachment_name,attachment_type,thumbnail,id;

    private String clicks,redirecting_link,description;

    public PostAttachmentModel(String attachment_name, String attachment_type, String thumbnail) {
        this.attachment_name = attachment_name;
        this.attachment_type = attachment_type;
        this.thumbnail = thumbnail;
    }

    public PostAttachmentModel(String attachment_name, String attachment_type, String thumbnail,
                               String clicks,String redirecting_link,String description,String id)
    {
        this.attachment_name = attachment_name;
        this.attachment_type = attachment_type;
        this.thumbnail = thumbnail;
        this.clicks = clicks;
        this.redirecting_link = redirecting_link;
        this.description = description;
        this.id=id;
    }



    public String getClicks() {
        return clicks;
    }

    public String getRedirecting_link() {
        return redirecting_link;
    }

    public String getDescription() {
        return description;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }

    public PostAttachmentModel(String attachment_name, String attachment_type, String thumbnail, String id) {
        this.attachment_name = attachment_name;
        this.attachment_type = attachment_type;
        this.thumbnail = thumbnail;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public String getAttachment_type() {
        return attachment_type;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
