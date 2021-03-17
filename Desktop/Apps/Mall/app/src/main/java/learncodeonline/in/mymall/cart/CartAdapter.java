package learncodeonline.in.mymall.cart;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.MainActivity;
import learncodeonline.in.mymall.R;
import learncodeonline.in.mymall.address.DeliveryActivity;
import learncodeonline.in.mymall.product.ProductDetailActivity;
import learncodeonline.in.mymall.reward.RewardAdapter;
import learncodeonline.in.mymall.reward.RewardModel;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;


    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new cartItemViewholder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new cartTotalAmountViewholder(cartTotalView);
            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductId();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                Long freeCoupon = cartItemModelList.get(position).getFreeCoupon();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIds = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean COD = cartItemModelList.get(position).isCOD();
                ((cartItemViewholder)holder).setItemDetails(productID,resource,title,freeCoupon,productPrice,cuttedPrice,offersApplied, position, inStock, String.valueOf(productQuantity), maxQuantity, qtyError, qtyIds, stockQty, COD);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalPrice;
                int savedAmount = 0;

                for(int x=0;x<cartItemModelList.size();x++){
                    if(cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + quantity;
                        if(TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                        }else{
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                        }

                        if(!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice()))*quantity;
                            if(!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())){
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))*quantity;
                            }
                        }else{
                            if(!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())){
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))*quantity;
                            }
                        }
                    }
                }
                if(totalItemPrice>500){
                    deliveryPrice = "FREE";
                    totalPrice = totalItemPrice;
                }
                else{
                    deliveryPrice = "60";
                    totalPrice = totalItemPrice + 60;
                }

                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalPrice(totalPrice);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((cartTotalAmountViewholder)holder).setTotalAmount(totalItems,totalItemPrice,deliveryPrice,totalPrice,savedAmount);
                break;
            default:
                return;
        }
        if(lastPosition<position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition=position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class cartItemViewholder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private ImageView freeCouponIcon;
        private TextView productTitle;
        private TextView freeCoupons;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView productQuantity;
        private TextView offersApplied;
        private TextView couponsApplied;
        private LinearLayout couponRedemptionLayout;
        private TextView couponRedemptionBody;

        private LinearLayout deleteBtn;
        private Button reedemBtn;
        private ImageView codIndicator;

        ///////coupon redem
        private  TextView couponTitle;
        private  TextView couponExpiryDate;
        private  TextView couponBody;
        private RecyclerView couponRecyclerView;
        private LinearLayout selectedCoupon;
        private TextView discountedPrice;
        private TextView originalPrice;
        private LinearLayout applyOrRemoveBtnContainer;
        private TextView footerText;
        private String productOriginalPrice;
        private Button removeCouponBtn,applyCouponBtn;
        //////coupon redem

        public cartItemViewholder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCouponIcon = itemView.findViewById(R.id.free_coupon_icon);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupon);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            offersApplied = itemView.findViewById(R.id.offers_applied);
            couponsApplied = itemView.findViewById(R.id.coupons_applied);
            couponRedemptionLayout = itemView.findViewById(R.id.coupon_redemption_layout);
            couponRedemptionBody = itemView.findViewById(R.id.tv_coupon_redemption);
            codIndicator = itemView.findViewById(R.id.cod_indicator);

            reedemBtn = itemView.findViewById(R.id.coupon_redemption_btn);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
        }

        private void setItemDetails(final String productID, String resource, String title, Long freeCouponNum, final String productPriceText, String cuttedPriceText, Long offersAppliedNum, final int position, boolean inStock, final String quantity, final Long maxQuantity, boolean qtyError, final List<String> qtyIds, final long stockQty, boolean COD) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.smallplaceholder)).into(productImage);
            productTitle.setText(title);

            final Dialog checkCouponPriceDialog = new Dialog(itemView.getContext());
            checkCouponPriceDialog.setContentView(R.layout.coupon_reedem_dialog);
            checkCouponPriceDialog.setCancelable(false);
            checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            if(COD){
                codIndicator.setVisibility(View.VISIBLE);
            }
            else{
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if(inStock) {

                if (freeCouponNum > 0) {
                    freeCoupons.setVisibility(View.VISIBLE);
                    freeCouponIcon.setVisibility(View.VISIBLE);
                    if (freeCouponNum == 1)
                        freeCoupons.setText("free " + freeCouponNum + " Coupon");
                    else
                        freeCoupons.setText("free " + freeCouponNum + " Coupons");
                } else {
                    freeCoupons.setVisibility(View.INVISIBLE);
                    freeCouponIcon.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("Rs." + productPriceText + "/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("Rs." + cuttedPriceText + "/-");
                couponRedemptionLayout.setVisibility(View.VISIBLE);

                ///////coupon dialog

                ImageView toogleRecyclerView = checkCouponPriceDialog.findViewById(R.id.toogle_recyclerview);
                couponRecyclerView = checkCouponPriceDialog.findViewById(R.id.coupons_recyclerview);
                selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);
                couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
                couponExpiryDate = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
                couponBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);
                footerText = checkCouponPriceDialog.findViewById(R.id.footer_text);
                applyOrRemoveBtnContainer = checkCouponPriceDialog.findViewById(R.id.apply_or_remove_btn_container);
                removeCouponBtn = checkCouponPriceDialog.findViewById(R.id.remove_btn);
                applyCouponBtn = checkCouponPriceDialog.findViewById(R.id.apply_btn);

                footerText.setVisibility(View.GONE);
                applyOrRemoveBtnContainer.setVisibility(View.VISIBLE);

                originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
                discountedPrice = checkCouponPriceDialog.findViewById(R.id.discounted_price);

                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                couponRecyclerView.setLayoutManager(layoutManager);

                originalPrice.setText(productPrice.getText());
                productOriginalPrice = productPriceText;
                RewardAdapter rewardAdapter = new RewardAdapter(position,DBqueries.rewardModelList,true,couponRecyclerView,selectedCoupon,productOriginalPrice,couponTitle,couponExpiryDate,couponBody,discountedPrice, cartItemModelList);
                couponRecyclerView.setAdapter(rewardAdapter);
                rewardAdapter.notifyDataSetChanged();

                applyCouponBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                            for (RewardModel rewardModel : DBqueries.rewardModelList) {
                                if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                                    couponRedemptionBody.setText(rewardModel.getCouponBody());
                                    reedemBtn.setText("Coupon");
                                }
                            }

                            couponsApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2)));
                            couponsApplied.setText("Coupon applied -" + offerDiscountedAmt);
                            notifyItemChanged(cartItemModelList.size() - 1);
                            checkCouponPriceDialog.dismiss();
                        }
                    }
                });

                removeCouponBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if(rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        couponTitle.setText("Coupon");
                        couponExpiryDate.setText("validity");
                        couponBody.setText("Tap the icon on the top right corner to select your coupon.");
                        couponsApplied.setVisibility(View.INVISIBLE);
                        couponRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.couponRed));
                        couponRedemptionBody.setText("Apply your coupon here.");
                        reedemBtn.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCouponId(null);
                        productPrice.setText("Rs." + productPriceText + "/-");
                        notifyItemChanged(cartItemModelList.size() - 1);
                        checkCouponPriceDialog.dismiss();
                    }
                });

                toogleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if(rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        showDialogRecyclerView();
                    }
                });


                if(!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                            couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                            couponRedemptionBody.setText(rewardModel.getCouponBody());
                            reedemBtn.setText("Coupon");

                            couponBody.setText(rewardModel.getCouponBody());
                            if(rewardModel.getType().equals("Discount")){
                                couponTitle.setText(rewardModel.getType());
                            }else{
                                couponTitle.setText("FLAT Rs."+rewardModel.getDiscORamt()+" OFF");
                            }
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/YYYY");
                            couponExpiryDate.setText("till "+simpleDateFormat.format(rewardModel.getTimestamp().toDate()));
                        }
                    }
                    discountedPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    couponsApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    couponsApplied.setText("Coupon applied - Rs." + offerDiscountedAmt + "/-");
                }
                else{
                    couponsApplied.setVisibility(View.INVISIBLE);
                    couponRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.couponRed));
                    couponRedemptionBody.setText("Apply your coupon here.");
                    reedemBtn.setText("Redeem");
                }
                /////////coupon dialog

                productQuantity.setText("Qty: " + quantity);

                if(!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    }
                }
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.setCancelable(false);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max " + maxQuantity);

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0) {
                                        if(itemView.getContext()instanceof MainActivity){
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        }
                                        else {
                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }

                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size() - 1);
                                        //DeliveryActivity.loadingDialog.show();

                                        if(!showDeleteBtn){

                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQty = Integer.parseInt(quantity);
                                            final int finalQty = Integer.parseInt(quantityNo.getText().toString());
                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if(finalQty > initialQty) {

                                                for (int y = 0; y < finalQty-initialQty; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timestamp = new HashMap<>();
                                                    timestamp.put("time", FieldValue.serverTimestamp());
                                                    final int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    qtyIds.add(quantityDocumentName);

                                                                    if (finalY + 1 == finalQty - initialQty) {
                                                                        firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {

                                                                                            List<String> serverQuantity = new ArrayList<>();

                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }
                                                                                            long availableQty = 0;
                                                                                            for (String qtyId : qtyIds) {

                                                                                                if (!serverQuantity.contains(qtyId)) {
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "Sorry ! all products may not be available in required quantity....", Toast.LENGTH_SHORT).show();
                                                                                                } else {
                                                                                                    availableQty++;
                                                                                                }

                                                                                            }
                                                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }else if(initialQty > finalQty){
                                                for (int x=0;x<initialQty-finalQty;x++) {
                                                    final String qtyId = qtyIds.get(qtyIds.size() - 1 - x);
                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(qtyId).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.remove(qtyId);
                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    if(finalX+1 <initialQty-finalQty){
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }

                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max quantity : " + maxQuantity, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });

                if (offersAppliedNum > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(cuttedPriceText) - Long.valueOf(productPriceText));
                    offersApplied.setText("Offers applied - Rs."+offerDiscountedAmt+"/-");
                } else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }

            }else{
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setText("");
                couponRedemptionLayout.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
                couponsApplied.setVisibility(View.GONE);
                offersApplied.setVisibility(View.GONE);
                freeCouponIcon.setVisibility(View.INVISIBLE);

            }


            if(showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else{
                deleteBtn.setVisibility(View.GONE);
            }


            reedemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkCouponPriceDialog.show();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if(rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }

                        if(!ProductDetailActivity.running_cart_query){
                        ProductDetailActivity.running_cart_query = true;
                        DBqueries.removeFromCart(position,itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }
        private void showDialogRecyclerView(){
            if(couponRecyclerView.getVisibility()==View.GONE){
                couponRecyclerView.setVisibility(View.VISIBLE);
                selectedCoupon.setVisibility(View.GONE);
            }
            else{
                couponRecyclerView.setVisibility(View.GONE);
                selectedCoupon.setVisibility(View.VISIBLE);
            }
        }
    }

    class cartTotalAmountViewholder extends RecyclerView.ViewHolder {

        private TextView totalItem;
        private TextView totalItemPrice;
        private TextView totalPrice;
        private TextView deliveryPrice;
        private TextView savedAmount;

        public cartTotalAmountViewholder(@NonNull View itemView) {
            super(itemView);
            totalItem = itemView.findViewById(R.id.total_item);
            totalItemPrice = itemView.findViewById(R.id.total_item_price);
            totalPrice = itemView.findViewById(R.id.total_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalPriceText,  int savedAmountText) {
            totalItem.setText("Price("+totalItemText+" items)");
            totalItemPrice.setText("Rs."+totalItemPriceText+"/-");
            totalPrice.setText("Rs."+totalPriceText+"/-");
            cartTotalAmount.setText("Rs."+totalPriceText+"/-");
            if(deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            }else{
                deliveryPrice.setText("Rs."+deliveryPriceText+"/-");
            }
            savedAmount.setText("You saved Rs."+savedAmountText+"/- on this order.");
            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if(totalItemPriceText == 0){
                if(DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if(showDeleteBtn){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            }else{
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
