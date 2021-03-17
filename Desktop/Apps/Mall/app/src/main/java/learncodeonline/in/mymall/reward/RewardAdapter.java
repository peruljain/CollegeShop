package learncodeonline.in.mymall.reward;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;

import learncodeonline.in.mymall.R;
import learncodeonline.in.mymall.cart.CartItemModel;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private List<RewardModel> rewardModelList;
    private Boolean useMiniLayout=false;
    private RecyclerView couponRecyclerView;
    private LinearLayout selectedCoupon;
    private String productOriginalPrice;
    private TextView selectedcouponTitle;
    private TextView selectedcouponValidity;
    private TextView selectedcouponBody;
    private TextView discountedPrice;
    private int cartItemPosition = -1;
    private List<CartItemModel> cartItemModelList;

    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
    }
    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView couponTitle, TextView couponValidity, TextView couponBody, TextView discountedPrice) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponRecyclerView = couponRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedcouponTitle = couponTitle;
        this.selectedcouponValidity = couponValidity;
        this.selectedcouponBody = couponBody;
        this.discountedPrice = discountedPrice;
    }
    public RewardAdapter(int cartItemPosition, List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView couponTitle, TextView couponValidity, TextView couponBody, TextView discountedPrice, List<CartItemModel> cartItemModelList) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponRecyclerView = couponRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedcouponTitle = couponTitle;
        this.selectedcouponValidity = couponValidity;
        this.selectedcouponBody = couponBody;
        this.discountedPrice = discountedPrice;
        this.cartItemPosition = cartItemPosition;
        this.cartItemModelList = cartItemModelList;
    }


    @NonNull
    @Override
    public RewardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(useMiniLayout){
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_reward_item_layout, parent, false);
        }
        else {
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String couponId = rewardModelList.get(position).getCouponId();
        String type = rewardModelList.get(position).getType();
        Timestamp validity = rewardModelList.get(position).getTimestamp();
        String body = rewardModelList.get(position).getCouponBody();
        String lowerLimit = rewardModelList.get(position).getLowerLimit();
        String upperLimit = rewardModelList.get(position).getUpperLimit();
        String discORamt = rewardModelList.get(position).getDiscORamt();
        Boolean alreadyUsed = rewardModelList.get(position).getAlreadyUsed();
        holder.setData(couponId, type, validity, body, upperLimit, lowerLimit, discORamt, alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView couponTitle;
        private TextView couponValidity;
        private TextView couponBody;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            couponTitle = itemView.findViewById(R.id.coupon_title);
            couponValidity = itemView.findViewById(R.id.coupon_validity);
            couponBody = itemView.findViewById(R.id.coupon_body);
        }
        private void setData(final String couponId, final String type, final Timestamp validity, final String body, final String upperLimit, final String lowerLimit, final String discORamt, final boolean alreadyUsed){

            if(type.equals("Discount")){
                couponTitle.setText(type);
            }else{
                couponTitle.setText("FLAT Rs."+discORamt+" OFF");
            }

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/YYYY");

            if(alreadyUsed){
                couponValidity.setText("Already used");
                couponValidity.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                couponBody.setTextColor(Color.parseColor("#50ffffff"));
                couponTitle.setTextColor(Color.parseColor("#50ffffff"));
            }
            else{
                couponBody.setTextColor(Color.parseColor("#ffffff"));
                couponTitle.setTextColor(Color.parseColor("#ffffff"));
                couponValidity.setTextColor(itemView.getContext().getResources().getColor(R.color.couponPurple));
                couponValidity.setText("till "+simpleDateFormat.format(validity.toDate()));
            }
            couponBody.setText(body);

            if(useMiniLayout){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!alreadyUsed) {
                            selectedcouponTitle.setText(type);
                            selectedcouponValidity.setText("till " + simpleDateFormat.format(validity.toDate()));
                            selectedcouponBody.setText(body);

                            if (Long.valueOf(productOriginalPrice) > Long.valueOf(lowerLimit) && Long.valueOf(productOriginalPrice) < Long.valueOf(upperLimit)) {
                                if (type.equals("Discount")) {
                                    Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(discORamt) / 100;
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "/-");
                                } else {
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discORamt)) + "/-");
                                }
                                if(cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(couponId);
                                }
                            } else {
                                if(cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(null);
                                }
                                discountedPrice.setText("Invalid");
                                Toast.makeText(itemView.getContext(), "Sorry! Product doesn't matches the Coupon terms.", Toast.LENGTH_SHORT).show();
                            }

                            if (couponRecyclerView.getVisibility() == View.GONE) {
                                couponRecyclerView.setVisibility(View.VISIBLE);
                                selectedCoupon.setVisibility(View.GONE);
                            } else {
                                couponRecyclerView.setVisibility(View.GONE);
                                selectedCoupon.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }
    }
}
