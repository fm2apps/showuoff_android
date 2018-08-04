package app.shouoff.model;

public class NotificationHistoryModel
{
    private String id,sender_id,notification_type,post_id,created_at,sender_image,body,name,status;

    public NotificationHistoryModel(String id, String sender_id, String notification_type,
                                    String post_id, String created_at, String sender_image, String body,String name,String status) {
        this.id = id;
        this.sender_id = sender_id;
        this.notification_type = notification_type;
        this.post_id = post_id;
        this.created_at = created_at;
        this.sender_image = sender_image;
        this.body = body;
        this.name=name;
        this.status=status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getSender_image() {
        return sender_image;
    }

    public String getBody() {
        return body;
    }
}
