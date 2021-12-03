package Model;

public class ContentModel {
    private String Video_Name,Video_Url;

    public ContentModel() {
    }

    public ContentModel(String video_Name, String video_Url) {
        Video_Name = video_Name;
        Video_Url = video_Url;
    }

    public String getVideo_Name() {
        return Video_Name;
    }

    public void setVideo_Name(String video_Name) {
        Video_Name = video_Name;
    }

    public String getVideo_Url() {
        return Video_Url;
    }

    public void setVideo_Url(String video_Url) {
        Video_Url = video_Url;
    }
}
