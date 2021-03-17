package learncodeonline.in.mymall.address;

public class AdressesModel {
    private String fullName;
    private String mobileNo;
    private String address;
    private String pincode;
    private Boolean selected;

    public AdressesModel(String fullName, String mobileNo, String address, String pincode, Boolean selected) {
        this.fullName = fullName;
        this.mobileNo = mobileNo;
        this.address = address;
        this.pincode = pincode;
        this.selected = selected;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
