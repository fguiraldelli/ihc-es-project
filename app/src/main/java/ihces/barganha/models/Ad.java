package ihces.barganha.models;

import java.math.BigDecimal;

public class Ad {
    private int id;
    private String title;
    private String description;
    private BigDecimal price;
    private String photoBase64;
    private String photoFileName;

    public Ad() { } // Enable Serialization

    public Ad(int id, String title, String description, BigDecimal price) {
        this(id, title, description, price, "", "");
    }

    public Ad(int id, String title, String description, BigDecimal price, String photoBase64, String photoFileName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.photoBase64 = photoBase64;
        this.photoFileName = photoFileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }
}
