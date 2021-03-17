package learncodeonline.in.mymall;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learncodeonline.in.mymall.address.AddAddressActivity;
import learncodeonline.in.mymall.address.AdressesModel;
import learncodeonline.in.mymall.address.DeliveryActivity;
import learncodeonline.in.mymall.cart.CartItemModel;
import learncodeonline.in.mymall.cart.MyCartFragment;
import learncodeonline.in.mymall.home.CategoryAdapter;
import learncodeonline.in.mymall.home.CategoryModel;
import learncodeonline.in.mymall.home.HomeFragment;
import learncodeonline.in.mymall.home.HomePageAdapter;
import learncodeonline.in.mymall.home.HomePageModel;
import learncodeonline.in.mymall.home.HorizontalProductScrollModel;
import learncodeonline.in.mymall.home.SliderModel;
import learncodeonline.in.mymall.order.MyOrderAdapter;
import learncodeonline.in.mymall.order.MyOrderItemModel;
import learncodeonline.in.mymall.order.MyOrdersFragment;
import learncodeonline.in.mymall.product.ProductDetailActivity;
import learncodeonline.in.mymall.reward.MyRewardsFragment;
import learncodeonline.in.mymall.reward.RewardModel;
import learncodeonline.in.mymall.wishlist.MyWishlistFragment;
import learncodeonline.in.mymall.wishlist.WishlistModel;

import static learncodeonline.in.mymall.product.ProductDetailActivity.productID;


public class DBqueries {


    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String email,fullname,profile;

    public static List<CategoryModel> categoryModelList = new ArrayList<CategoryModel>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static int selectedAddress = -1;
    public static List<AdressesModel> adressesModelList = new ArrayList<>();

    public static List<RewardModel> rewardModelList = new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();

    public static void loadCategories(final RecyclerView categoryRecyclerView, final CategoryAdapter categoryAdapter, final Context context){

        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryNames){

        firebaseFirestore.collection("CATEGORIES")
                .document(categoryNames.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if(documentSnapshot.get("view_type").equals((long)0)){

                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long no_of_banners = (long)documentSnapshot.get("no_of_banner");
                                    for(long x=1;x<no_of_banners+1;x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString()
                                                , documentSnapshot.get("banner_" + x + "_background").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(0,sliderModelList));
                                }
                                else if(documentSnapshot.get("view_type").equals((long)1)){
                                    lists.get(index).add(new HomePageModel(1,documentSnapshot.get("strip_ad_banner").toString()
                                            ,documentSnapshot.get("background").toString()));
                                }
                                else if(documentSnapshot.get("view_type").equals((long)2)){
                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                    long no_of_products = (long)documentSnapshot.get("no_of_products");
                                    for(long x=1;x<no_of_products+1;x++){
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_"+x).toString(),
                                                documentSnapshot.get("product_image_"+x).toString(),
                                                documentSnapshot.get("product_title_"+x).toString(),
                                                documentSnapshot.get("product_subtitle_"+x).toString(),
                                                documentSnapshot.get("product_price_"+x).toString()));

                                        viewAllProductList.add(new WishlistModel(documentSnapshot.get("product_ID_"+x).toString(),
                                                documentSnapshot.get("product_image_"+x).toString(),
                                                documentSnapshot.get("product_full_title_"+x).toString(),
                                                (long)documentSnapshot.get("free_coupons_"+x),
                                                documentSnapshot.get("average_rating_"+x).toString(),
                                                (long)documentSnapshot.get("total_rating_"+x),
                                                documentSnapshot.get("product_price_"+x).toString(),
                                                documentSnapshot.get("cutted_price_"+x).toString(),
                                                (boolean)documentSnapshot.get("COD_"+x),
                                                (boolean) documentSnapshot.get("in_stock_"+x)));
                                    }
                                    lists.get(index).add(new HomePageModel(2,documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList,viewAllProductList));
                            }
                                else if(documentSnapshot.get("view_type").equals((long)3)){
                                    List<HorizontalProductScrollModel> GridLayoutModelList = new ArrayList<>();
                                    long no_of_products = (long)documentSnapshot.get("no_of_products");
                                    for(long x=1;x<no_of_products+1;x++){
                                        GridLayoutModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_"+x).toString()
                                                ,documentSnapshot.get("product_image_"+x).toString(),documentSnapshot.get("product_title_"+x).toString(),documentSnapshot.get("product_subtitle_"+x).toString(),documentSnapshot.get("product_price_"+x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(3,documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), GridLayoutModelList));

                                }
                                else{

                                    return;
                                }
                            }
                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadWishList(final Context context, final Dialog dialog, final boolean loadProductData){
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    for(long x=0;x<(long)task.getResult().get("list_size");x++) {
                        wishList.add(task.getResult().get("product_ID_" + x).toString());

                        if(DBqueries.wishList.contains(productID)){
                            ProductDetailActivity.ALREADY_ADDED_TO_WISHLIST = true;
                            if(ProductDetailActivity.addToWishlistBtn != null) {
                                ProductDetailActivity.addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                            }
                        }else{
                            if(ProductDetailActivity.addToWishlistBtn != null) {
                                ProductDetailActivity.addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                            }
                            ProductDetailActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        }

                        if (loadProductData) {
                            wishlistModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            if(task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){
                                                                wishlistModelList.add(new WishlistModel(productId,
                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        (long) documentSnapshot.get("free_coupons"),
                                                                        documentSnapshot.get("average_rating").toString(),
                                                                        (long) documentSnapshot.get("total_ratings"),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (boolean) documentSnapshot.get("COD"),
                                                                        true));
                                                            }else{
                                                                wishlistModelList.add(new WishlistModel(productId,
                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        (long) documentSnapshot.get("free_coupons"),
                                                                        documentSnapshot.get("average_rating").toString(),
                                                                        (long) documentSnapshot.get("total_ratings"),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (boolean) documentSnapshot.get("COD"),
                                                                        false));
                                                            }
                                                            MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                                                        }
                                                        else{
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
}

    public static void removeFromWishlist(final int index, final Context context){
        final String removedProductId = wishList.get(index);
        wishList.remove(index);
        Map<String,Object> updateWishlist = new HashMap<>();

        for(int x=0;x<wishList.size();x++){
            updateWishlist.put("product_ID_"+x,wishList.get(x));
        }
        updateWishlist.put("list_size",(long)wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    if (wishlistModelList.size() != 0){
                        wishlistModelList.remove(index);
                        MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                    }
                    ProductDetailActivity.ALREADY_ADDED_TO_WISHLIST = false;
                    Toast.makeText(context,"Removed Successfully!",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(ProductDetailActivity.addToWishlistBtn != null) {
                        ProductDetailActivity.addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                    }
                    wishList.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                }
                ProductDetailActivity.running_wishlist_query = false;
            }
        });
    }

    public static void loadRatingsList(final Context context){
        if(!ProductDetailActivity.running_rating_query) {
            ProductDetailActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> orderProductIds = new ArrayList<>();
                        for(int x=0;x<myOrderItemModelList.size();x++){
                            orderProductIds.add(myOrderItemModelList.get(x).getProductId());
                        }


                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRating.add((long) task.getResult().get("rating_" + x));

                            if (task.getResult().get("product_ID_" + x).toString().equals(productID) ) {
                                ProductDetailActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if(ProductDetailActivity.rateNowContainer!=null) {
                                    ProductDetailActivity.setRating(ProductDetailActivity.initialRating);
                                }

                            }

                            if(orderProductIds.contains(task.getResult().get("product_ID_" + x).toString())){
                                myOrderItemModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                            }
                        }
                        if(MyOrdersFragment.myOrderAdapter != null){
                            MyOrdersFragment.myOrderAdapter.notifyDataSetChanged();
                        }

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount, final TextView cartTotalAmount){
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    for(long x=0;x<(long)task.getResult().get("list_size");x++) {
                        cartList.add(task.getResult().get("product_ID_" + x).toString());

                        if(DBqueries.cartList.contains(productID)){
                            ProductDetailActivity.ALREADY_ADDED_TO_CART = true;
                        }else{
                            ProductDetailActivity.ALREADY_ADDED_TO_CART = false;
                        }

                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            int index = 0;
                                                            if(cartList.size()>=2){
                                                                index = cartList.size()-2;
                                                            }
                                                            if(task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){
                                                                cartItemModelList.add(index,new CartItemModel(documentSnapshot.getBoolean("COD"),
                                                                        CartItemModel.CART_ITEM,
                                                                        productId, documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (long) documentSnapshot.get("free_coupons"),
                                                                        (long) 1,
                                                                        (long) documentSnapshot.get("max_quantity"),
                                                                        (long) documentSnapshot.get("stock_quantity"),
                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                        (long) 0,
                                                                        true));

                                                            }else{
                                                                cartItemModelList.add(index,new CartItemModel(documentSnapshot.getBoolean("COD"),
                                                                        CartItemModel.CART_ITEM,
                                                                        productId, documentSnapshot.get("product_image_1").toString(),
                                                                        documentSnapshot.get("product_title").toString(),
                                                                        documentSnapshot.get("product_price").toString(),
                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                        (long) documentSnapshot.get("free_coupons"),
                                                                        (long) 1,
                                                                        (long) documentSnapshot.get("max_quantity"),
                                                                        (long) documentSnapshot.get("stock_quantity"),
                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                        (long) 0,
                                                                        false));
                                                            }
                                                            if(cartList.size() == 1){
                                                                cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                                LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                                parent.setVisibility(View.VISIBLE);
                                                            }
                                                            if(cartList.size() == 0){
                                                                cartItemModelList.clear();
                                                            }

                                                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                        }
                                                        else{
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    if(cartList.size() != 0){
                        badgeCount.setVisibility(View.VISIBLE);
                    }
                    else{
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if(DBqueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    }
                    else{
                        badgeCount.setText("99");
                    }
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount){
        final String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String,Object> updateCartList = new HashMap<>();

        for(int x=0;x<cartList.size();x++){
            updateCartList.put("product_ID_"+x,cartList.get(x));
        }
        updateCartList.put("list_size",(long)cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (cartItemModelList.size() != 0){
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if(cartList.size() == 0){
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }

                    Toast.makeText(context,"Removed Successfully!",Toast.LENGTH_SHORT).show();
                }
                else{
                    cartList.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                }
                ProductDetailActivity.running_cart_query = false;
            }
        });
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog){

        adressesModelList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Intent deliveryIntent;
                   // Log.i("kajal", String.valueOf((long)task.getResult().get("list_size")));
                    if((long)task.getResult().get("list_size") == 0){
                        deliveryIntent = new Intent(context, AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT","deliveryIntent");
                    }
                    else{

                        for(long x=1;x<(long)task.getResult().get("list_size")+1;x++){
                            adressesModelList.add(new AdressesModel(task.getResult().get("fullname_"+x).toString(),
                                    task.getResult().getString("mobile_no_"+x),
                                    task.getResult().get("address_"+x).toString(),
                                    task.getResult().get("pincode_"+x).toString(),
                                    (boolean)task.getResult().get("selected_"+x)));
                            if((boolean)task.getResult().get("selected_"+x)){
                                selectedAddress = Integer.parseInt(String.valueOf(x-1));
                            }
                        }

                        deliveryIntent = new Intent(context, DeliveryActivity.class);
                    }
                    context.startActivity(deliveryIntent);
                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    public static void loadRewards(final Context context, final Dialog loadingDialog, final boolean onRewardFragment){
        rewardModelList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final Date lastseenDate = task.getResult().getDate("Last seen");
                            Log.i("kajal",lastseenDate.toString());

                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_REWARDS").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    if (documentSnapshot.get("type").toString().equals("Discount") && lastseenDate.before(documentSnapshot.getDate("validity"))) {
                                                        rewardModelList.add(new RewardModel(documentSnapshot.getId(),
                                                                documentSnapshot.get("type").toString(),
                                                                documentSnapshot.get("lower_limit").toString(),
                                                                documentSnapshot.get("upper_limit").toString(),
                                                                documentSnapshot.get("percentage").toString(),
                                                                documentSnapshot.get("body").toString(),
                                                                (Timestamp) documentSnapshot.get("validity"),
                                                                (boolean)documentSnapshot.get("already_used")
                                                                ));
                                                    } else if(documentSnapshot.get("type").toString().equals("Flat Rs.*OFF") && lastseenDate.before(documentSnapshot.getDate("validity"))) {
                                                        rewardModelList.add(new RewardModel(documentSnapshot.getId(),
                                                                documentSnapshot.get("type").toString(),
                                                                documentSnapshot.get("lower_limit").toString(),
                                                                documentSnapshot.get("upper_limit").toString(),
                                                                documentSnapshot.get("amount").toString(),
                                                                documentSnapshot.get("body").toString(),
                                                                (Timestamp) documentSnapshot.get("validity"),
                                                                (boolean)documentSnapshot.get("already_used")
                                                                ));
                                                    }
                                                }
                                                if(onRewardFragment) {
                                                    MyRewardsFragment.rewardAdapter.notifyDataSetChanged();
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                            loadingDialog.dismiss();
                                        }
                                    });
                        }else{
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public static void loadOrders(final Context context, final MyOrderAdapter myOrderAdapter, final Dialog loadingDialog){
        myOrderItemModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                       firebaseFirestore.collection("ORDERS").document((String) documentSnapshot.get("order_id")).collection("OrderItems")
                               .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if(task.isSuccessful()){
                                   for(DocumentSnapshot orderItems : task.getResult().getDocuments()){

                                       final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderItems.getString("Product Id"),
                                               orderItems.getString("Order Status"),
                                               orderItems.getString("Address"),
                                               orderItems.getString("Coupon Id"),
                                               orderItems.getString("Cutted Price"),
                                               orderItems.getDate("Ordered date"),
                                               orderItems.getDate("Packed date"),
                                               orderItems.getDate("Shipped date"),
                                               orderItems.getDate("Delivered date"),
                                               orderItems.getDate("Cancelled date"),
                                               orderItems.getString("Discounted Price"),
                                               orderItems.getLong("Free Coupons"),
                                               orderItems.getString("FullName"),
                                               orderItems.getString("ORDER ID"),
                                               orderItems.getString("Payment Method"),
                                               orderItems.getString("Pincode"),
                                               orderItems.getString("Product Price"),
                                               orderItems.getLong("Product Quantity"),
                                               orderItems.getString("User Id"),
                                               orderItems.getString("Product Image"),
                                               orderItems.getString("Product Title"),
                                               orderItems.getString("Delivery Price"),
                                               orderItems.getBoolean("Cancellation Requested"));

                                       myOrderItemModelList.add(myOrderItemModel);
                                   }

                                   loadRatingsList(context);
                                   myOrderAdapter.notifyDataSetChanged();
                               }
                               else{
                                   String error = task.getException().getMessage();
                                   Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                               }
                               loadingDialog.dismiss();
                           }
                       });
                   }
               }
               else{
                   loadingDialog.dismiss();
                   String error = task.getException().getMessage();
                   Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    public static void clearData(){
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        adressesModelList.clear();
        rewardModelList.clear();
        myOrderItemModelList.clear();
    }
}
