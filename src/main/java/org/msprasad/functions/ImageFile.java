package org.msprasad.functions;

public class ImageFile {

    private String id;
    private String base64;
    private String url;

    public ImageFile(String id, String base64, String url) {
        this.id = id;
        this.base64 = base64;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getBase64() {
        return base64;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ImageFile{" +
                "id='" + id + '\'' +
                ", base64='" + base64 + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
