package learncodeonline.in.mymall.home;

public class HorizontalProductScrollModel {

    private String productID;
    private String productImage;
    private String productName;
    private String productDescription;
    private String productPrice;

    public HorizontalProductScrollModel(String productID, String productImage, String productName, String productDescription, String productPrice) {
        this.productID = productID;
        this.productImage = productImage;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
