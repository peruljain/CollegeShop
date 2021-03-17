package learncodeonline.in.mymall.home;

import android.util.Log;

public class CategoryModel {
    private String CategoryIconLink;
    private String CategoryName;

    public CategoryModel(String categoryIconLink, String categoryName) {
        CategoryIconLink = categoryIconLink;
        this.CategoryName = categoryName;
    }

    public String getCategoryIconLink() {
        return CategoryIconLink;
    }

    public void setCategoryIconLink(String categoryIconLink) {
        CategoryIconLink = categoryIconLink;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}
