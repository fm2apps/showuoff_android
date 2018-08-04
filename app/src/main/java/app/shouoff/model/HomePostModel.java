package app.shouoff.model;

import java.io.Serializable;
import java.util.ArrayList;

public class HomePostModel implements Serializable
{
    private String id,user_id,description,created_at,like_count,comment_count,share_count,
            first_name,family_name,image,like_status,file_to_show;
    private String share_user_id,user_name,user_image,award_image;
    private boolean aBoolean;
    private ArrayList<PostAttachmentModel> attachmentModels;
    private ArrayList<TagUserData> tagUserData;

    /*For home , my profile and highly like */
    public HomePostModel(String id, String user_id, String description,
                         String created_at, String like_count,
                         String comment_count, String share_count,
                         String first_name, String family_name,
                         String image, String like_status,
                         boolean aBoolean, ArrayList<PostAttachmentModel> attachmentModels,String file_to_show,
                         ArrayList<TagUserData> tagUserData)
    {
        this.id = id;
        this.user_id = user_id;
        this.description = description;
        this.created_at = created_at;
        this.like_count = like_count;
        this.comment_count = comment_count;
        this.share_count = share_count;
        this.first_name = first_name;
        this.family_name = family_name;
        this.image = image;
        this.like_status = like_status;
        this.aBoolean = aBoolean;
        this.attachmentModels = attachmentModels;
        this.file_to_show=file_to_show;
        this.tagUserData=tagUserData;
    }

    /*For Award Post */
    public HomePostModel(String id, String user_id, String description,
                         String created_at, String like_count,
                         String comment_count, String share_count,
                         String first_name, String family_name,
                         String image, String like_status,
                         boolean aBoolean, ArrayList<PostAttachmentModel> attachmentModels,
                         String award_image,String file_to_show,ArrayList<TagUserData> tagUserData)
    {
        this.id = id;
        this.user_id = user_id;
        this.description = description;
        this.created_at = created_at;
        this.like_count = like_count;
        this.comment_count = comment_count;
        this.share_count = share_count;
        this.first_name = first_name;
        this.family_name = family_name;
        this.image = image;
        this.like_status = like_status;
        this.aBoolean = aBoolean;
        this.attachmentModels = attachmentModels;
        this.award_image=award_image;
        this.file_to_show=file_to_show;
        this.tagUserData=tagUserData;
    }

    public String getFile_to_show() {
        return file_to_show;
    }

    public String getAward_image()
    {
        return award_image;
    }

    /*For Shared Post*/
    public HomePostModel(String id, String user_id, String description,
                         String created_at, String like_count,
                         String comment_count, String share_count,
                         String first_name, String family_name,
                         String image, String like_status,
                         boolean aBoolean, ArrayList<PostAttachmentModel> attachmentModels
                        ,String share_user_id,String user_name,String user_image,ArrayList<TagUserData> tagUserData)
    {
        this.id = id;
        this.user_id = user_id;
        this.description = description;
        this.created_at = created_at;
        this.like_count = like_count;
        this.comment_count = comment_count;
        this.share_count = share_count;
        this.first_name = first_name;
        this.family_name = family_name;
        this.image = image;
        this.like_status = like_status;
        this.aBoolean = aBoolean;
        this.attachmentModels = attachmentModels;
        this.share_user_id=share_user_id;
        this.user_name=user_name;
        this.user_image=user_image;
        this.tagUserData=tagUserData;
    }

    public String getShare_user_id() {
        return share_user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_image() {
        return user_image;
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

    public ArrayList<TagUserData> getTagUserData() {
        return tagUserData;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getLike_count() {
        return like_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public String getShare_count() {
        return share_count;
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

    public String getLike_status() {
        return like_status;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public ArrayList<PostAttachmentModel> getAttachmentModels() {
        return attachmentModels;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public void setShare_count(String share_count) {
        this.share_count = share_count;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }
}
