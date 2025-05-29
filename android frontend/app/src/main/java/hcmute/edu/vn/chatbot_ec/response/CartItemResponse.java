package hcmute.edu.vn.chatbot_ec.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class CartItemResponse implements Parcelable {
    private int id;
    private int productId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String thumbnailUrl;

    public CartItemResponse() {
    }

    protected CartItemResponse(Parcel in) {
        id = in.readInt();
        productId = in.readInt();
        name = in.readString();
        price = (BigDecimal) in.readSerializable();
        quantity = in.readInt();
        thumbnailUrl = in.readString();
    }

    public static final Creator<CartItemResponse> CREATOR = new Creator<CartItemResponse>() {
        @Override
        public CartItemResponse createFromParcel(Parcel in) {
            return new CartItemResponse(in);
        }

        @Override
        public CartItemResponse[] newArray(int size) {
            return new CartItemResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(productId);
        dest.writeString(name);
        dest.writeSerializable(price);
        dest.writeInt(quantity);
        dest.writeString(thumbnailUrl);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}