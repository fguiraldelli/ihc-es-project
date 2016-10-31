package ihces.barganha.models;

import com.google.gson.annotations.SerializedName;

public class Ad {

    private int id;
    @SerializedName("titulo")
    private String title;
    @SerializedName("descricao")
    private String description;
    @SerializedName("preco")
    private String price;
    @SerializedName("imagem")
    private String photoBase64;

    public Ad() { } // Enable Serialization

    public Ad(int id, String title, String description, String price) {
        this(id, title, description, price, "");
    }

    public Ad(int id, String title, String description, String price, String photoBase64) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.photoBase64 = photoBase64;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }
}
