package learncodeonline.in.mymall.wishlist;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.R;
import learncodeonline.in.mymall.product.ProductDetailActivity;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<WishlistModel> wishlistModelList;
    private Boolean wishlist;
    private int lastPosition=-1;

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productId = wishlistModelList.get(position).getProductId();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        long freeCouponNum = wishlistModelList.get(position).getFreecoupons();
        String averageRate = wishlistModelList.get(position).getRating();
        long totalRatingNum = wishlistModelList.get(position).getTotalRating();
        String price = wishlistModelList.get(position).getProductPrice();
        String cutprice = wishlistModelList.get(position).getCuttedPrice();
        boolean payMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();
        holder.setData(productId,resource,title,freeCouponNum,averageRate,totalRatingNum,price,cutprice,payMethod, position, inStock);

        if(lastPosition<position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition=position;
        }

    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private TextView productTitle;
        private ImageView couponIcon;
        private TextView freeCoupon;
        private TextView rating;
        private TextView totalRating;
        private View priceCut;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethod;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             productImage = itemView.findViewById(R.id.product_image);
             productTitle = itemView.findViewById(R.id.product_title);
             couponIcon = itemView.findViewById(R.id.coupon_icon);
             freeCoupon = itemView.findViewById(R.id.free_coupon);
             rating = itemView.findViewById(R.id.tv_product_rating_miniview);
             totalRating = itemView.findViewById(R.id.total_ratings);
             priceCut = itemView.findViewById(R.id.cutted_divider);
             productPrice = itemView.findViewById(R.id.product_price);
             cuttedPrice = itemView.findViewById(R.id.cutted_price);
             paymentMethod = itemView.findViewById(R.id.payment_method);
             deleteBtn = itemView.findViewById(R.id.delete_button);
        }
        private void setData(final String productId, String resource, String title, long freeCouponNum, String averageRate, long totalRatingNum, String price, String cutprice, boolean payMethod, final int index, boolean inStock){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.smallplaceholder)).into(productImage);
            productTitle.setText(title);
            if (freeCouponNum != 0 && inStock) {
                freeCoupon.setVisibility(View.VISIBLE);
                couponIcon.setVisibility(View.VISIBLE);
                if (freeCouponNum == 1)
                    freeCoupon.setText("free " + freeCouponNum + " Coupon");
                else
                    freeCoupon.setText("free " + freeCouponNum + " Coupons");
            } else {
                freeCoupon.setVisibility(View.INVISIBLE);
                couponIcon.setVisibility(View.INVISIBLE);
            }
            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if(inStock) {

                rating.setVisibility(View.VISIBLE);
                totalRating.setVisibility(View.VISIBLE);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                rating.setText(averageRate);
                totalRating.setText("(" + totalRatingNum + ")" + "ratings");
                productPrice.setText(price);
                cuttedPrice.setText(cutprice);
                if (payMethod == true) {
                    paymentMethod.setVisibility(View.VISIBLE);
                } else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            }else{
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRating.setVisibility(View.INVISIBLE);
                priceCut.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);
            }


            if(wishlist){
                deleteBtn.setVisibility(View.VISIBLE);
            }else{
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ProductDetailActivity.running_wishlist_query) {
                        ProductDetailActivity.running_wishlist_query = true;
                        DBqueries.removeFromWishlist(index, itemView.getContext());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID",productId);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}
