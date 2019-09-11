package com.example.loadinfo;

public class InfoData {
    private int albumId;
    private int id;
    private String title;
    private String url;
    private String thumbnailUrl;

    public InfoData(int albumId, int id, String title, String url, String thunmNailUrl) {
        setAlbumId(albumId);
        setId(id);
        setTitle(title);
        setUrl(url);
        setThumbnailUrl(thunmNailUrl);
    }

    public void setAlbumId(int mAlbumId) {
        this.albumId = mAlbumId;
    }

    public void setId(int mId) {
        this.id = mId;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
    }

    public void setUrl(String mUrl) {
        this.url = mUrl;
    }

    public void setThumbnailUrl(String mThumbNailUrl) {
        this.thumbnailUrl = mThumbNailUrl;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String toString() {
        return String.valueOf(getId())+" "+
                String.valueOf(getAlbumId())+" "+
                getTitle();
    }
}
