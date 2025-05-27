package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class ProductResponse {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("stock")
    private Integer stock;

    @SerializedName("color")
    private String color;

    @SerializedName("size")
    private String size;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    @SerializedName("productImages")
    private List<ProductImageResponse> productImages;

    @SerializedName("categoryId")
    private Integer categoryId;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<ProductImageResponse> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImageResponse> productImages) {
        this.productImages = productImages;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}