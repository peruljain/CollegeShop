package learncodeonline.in.mymall.address;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.constraintlayout.widget.ConstraintLayout;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.Manifest;
        import android.app.Dialog;
        import android.app.DownloadManager;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.DefaultRetryPolicy;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.FieldValue;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.Query;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;
        import com.paytm.pgsdk.PaytmOrder;
        import com.paytm.pgsdk.PaytmPGService;
        import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.UUID;

        import learncodeonline.in.mymall.DBqueries;
        import learncodeonline.in.mymall.MainActivity;
        import learncodeonline.in.mymall.OTPverificationActivity;
        import learncodeonline.in.mymall.R;
        import learncodeonline.in.mymall.cart.CartAdapter;
        import learncodeonline.in.mymall.cart.CartItemModel;
        import learncodeonline.in.mymall.product.ProductDetailActivity;

public class DeliveryActivity extends AppCompatActivity {


    public static List<CartItemModel> cartItemModelList;
    public static final int SELECT_ADDRESS = 0;

    private RecyclerView deliveryRecyclerView;
    public static CartAdapter cartAdapter;
    private Button changeOrAddNewAddressbtn;
    private TextView totalAmount;
    private TextView fullname;
    private String name,mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    private Button continueBtn;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private TextView codTitle;
    private View divider;
    private ImageButton paytm,cod;
    private String paymentMethod = "PAYTM";
    private ConstraintLayout orderConfirmation;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private String order_id;

    private boolean successResponse = false;
    private FirebaseFirestore firebaseFirestore;

    public static boolean fromCart;
    public static boolean codOrderConfirmed = false;
    public static boolean getQtyIds = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);
        changeOrAddNewAddressbtn = findViewById(R.id.change_or_add_address_button);
        totalAmount = findViewById(R.id.total_cart_amount);
        fullname = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        orderConfirmation = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIds = true;


        /////// loading dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        /////// loading dialog


        /////// paymentMethod dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethodDialog.findViewById(R.id.paytm);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        codTitle = paymentMethodDialog.findViewById(R.id.cod_btn_title);
        divider = paymentMethodDialog.findViewById(R.id.divider);
        /////// paymentMethod dialog
        order_id = UUID.randomUUID().toString().substring(0,28);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(DeliveryActivity.cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressbtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIds = false;
                Intent myaddressesIntent = new Intent(DeliveryActivity.this, MyAddressActivity.class);
                myaddressesIntent.putExtra("MODE",SELECT_ADDRESS);
                startActivity(myaddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean allProductAvailable = true;
                for(CartItemModel cartItemModel:cartItemModelList){
                    if(cartItemModel.isQtyError()){
                        allProductAvailable = false;
                        break;
                    }
                    if(cartItemModel.getType() == cartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
                            codTitle.setAlpha(0.5f);
                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
                            codTitle.setAlpha(1f);
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if(allProductAvailable){
                    paymentMethodDialog.show();
                }
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               paymentMethod = "COD";
               placeOrderDetails();
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "PAYTM";
                placeOrderDetails();
            }
        });
    }


    List<String> serverQuantity = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();

        /////accessing quantity
        if(getQtyIds) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for(int y=0; y<cartItemModelList.get(x).getProductQuantity(); y++){
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0,20);

                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                        if(finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()){
                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductId()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){


                                                                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }
                                                                long availableQty = 0;
                                                                boolean nolongerAvailable = true;
                                                                for(String qtyId : cartItemModelList.get(finalX).getQtyIDs()){
                                                                    cartItemModelList.get(finalX).setQtyError(false);
                                                                    if(!serverQuantity.contains(qtyId)){

                                                                        if(nolongerAvailable){
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        }
                                                                        else{

                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "Sorry ! all products may not be available in required quantity....", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }else{
                                                                        availableQty++;
                                                                        nolongerAvailable = false;
                                                                    }


                                                                }
                                                                cartAdapter.notifyDataSetChanged();
                                                            }
                                                            else{
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        }
                                    }
                                    else{
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        }
        else{
            getQtyIds = true;
        }
        /////accessing quantity


        name = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getFullName();
        mobileNo = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        fullname.setText(name + "-" + mobileNo);
        fullAddress.setText(DBqueries.adressesModelList.get(DBqueries.selectedAddress).getAddress());
        pincode.setText(DBqueries.adressesModelList.get(DBqueries.selectedAddress).getPincode());

        if(codOrderConfirmed){
            showConfirmationLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();
        if(getQtyIds) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if(!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(qtyID).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size()-1))){
                                    cartItemModelList.get(finalX).getQtyIDs().clear();
                                }
                            }
                        });
                    }
                }
                else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(successResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout(){
        successResponse = true;
        codOrderConfirmed = false;
        getQtyIds = false;

        for(int x=0;x<cartItemModelList.size() - 1;x++){
            for(String qtyID : serverQuantity){
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(qtyID).update("user_ID",FirebaseAuth.getInstance().getUid());
                cartItemModelList.get(x).getQtyIDs().remove(qtyID);
            }
        }

        if(MainActivity.mainActivity != null){
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
            MainActivity.showCart = false;
        }else{
           MainActivity.resetMainactivity = true;
        }

        if( ProductDetailActivity.productDetailActivity != null){
            ProductDetailActivity.productDetailActivity.finish();
            ProductDetailActivity.productDetailActivity = null;
        }
        ////////sent confirmation SMS
        String SMS_API = "https://www.fast2sms.com/dev/bulk";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ////////nothing
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /////////nothing
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("authorization","kuaYUA4i3nLd12E7ebQ9wqCIVKzNRo6J8vTGgDBMsfclrxXhZOH6RTdhEcj43YItABfvxP9wLyszU7pV");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> body = new HashMap<>();
                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers",mobileNo);
                body.put("message","31283");
                body.put("variables","{#FF#}");
                body.put("variables_values", order_id);
                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);
        ////////sent confirmation SMS
        Log.i("KAJAL", String.valueOf(cartItemModelList.size()));
        if(fromCart){
            loadingDialog.show();
            Map<String,Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();

            for(int x=0;x<DBqueries.cartList.size();x++){
                if(!cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductId());
                    cartListSize++;
                }
                else{
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size",cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        for(int x=0;x<indexList.size();x++){
                            DBqueries.cartList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size()-1);
                        }
                    }
                    else{
                        String error = task.getException().getLocalizedMessage();
                        Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });

        }

        continueBtn.setEnabled(false);
        changeOrAddNewAddressbtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText("Order_ID: "+order_id);
        orderConfirmation.setVisibility(View.VISIBLE);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void placeOrderDetails(){

        String userID = FirebaseAuth.getInstance().getUid();
        // Log.i("kajal",userID);
        loadingDialog.show();
        for(CartItemModel cartItemModel : cartItemModelList) {
            if(cartItemModel.getType() == cartItemModel.CART_ITEM) {

                Map<String,Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID",order_id);
                orderDetails.put("Product Id",cartItemModel.getProductId());
                orderDetails.put("Product Image",cartItemModel.getProductImage());
                orderDetails.put("Product Title",cartItemModel.getProductTitle());
                orderDetails.put("User Id",userID);
                orderDetails.put("Product Quantity",cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("Cutted Price",cartItemModel.getCuttedPrice());
                }
                else{
                    orderDetails.put("Cutted Price","");
                }
                orderDetails.put("Product Price",cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCouponId() != null) {
                    orderDetails.put("Coupon Id", cartItemModel.getSelectedCouponId());
                }
                else{
                    orderDetails.put("Coupon Id", "");
                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted Price", cartItemModel.getDiscountedPrice());
                }
                else{
                    orderDetails.put("Discounted Price", "");
                }
                orderDetails.put("Ordered date",FieldValue.serverTimestamp());
                orderDetails.put("Packed date",FieldValue.serverTimestamp());
                orderDetails.put("Shipped date",FieldValue.serverTimestamp());
                orderDetails.put("Delivered date",FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date",FieldValue.serverTimestamp());
                orderDetails.put("Order Status","Ordered");
                orderDetails.put("Payment Method",paymentMethod);
                orderDetails.put("Address",fullAddress.getText());
                orderDetails.put("FullName",fullname.getText());
                orderDetails.put("Pincode",pincode.getText());
                orderDetails.put("Free Coupons",cartItemModel.getFreeCoupon());
                orderDetails.put("Delivery Price",cartItemModelList.get(cartItemModelList.size()-1).getDeliveryPrice());
                orderDetails.put("Cancellation Requested",false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductId())
                .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Map<String,Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items",cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price",cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery Price",cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Price",cartItemModel.getTotalPrice());
                orderDetails.put("Saved Amount",cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status","not Paid");
                orderDetails.put("Order Status","Cancelled");
                firebaseFirestore.collection("ORDERS").document(order_id)
                .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(paymentMethod.equals("PAYTM")){
                                paytm();
                            }else{
                                cod();
                            }
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void paytm(){
        getQtyIds = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();
        if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        final String M_id = "EdzfDp00596156443974";
        final String customer_id = FirebaseAuth.getInstance().getUid();
        final String url = "https://my-shopping-mall.000webhostapp.com/Paytm/generateChecksum.php";
        final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("CHECKSUMHASH")){
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");

                        PaytmPGService paytmPGService = PaytmPGService.getStagingService("");
                        HashMap<String, String> paramMap = new HashMap<>();
                        paramMap.put( "MID" , M_id);
                        paramMap.put( "ORDER_ID" , order_id);
                        paramMap.put( "CUST_ID" , customer_id);
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2));
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        paramMap.put( "CALLBACK_URL", callBackUrl);
                        paramMap.put( "CHECKSUMHASH" , CHECKSUMHASH);

                        PaytmOrder order = new PaytmOrder(paramMap);

                        paytmPGService.initialize(order,null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                                                       public void onTransactionResponse(Bundle inResponse) {
                                //Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                                    Map<String, Object> updateStatus = new HashMap<>();
                                    updateStatus.put("Payment Status", "Paid");
                                    updateStatus.put("Order Status", "Ordered");
                                    firebaseFirestore.collection("ORDERS").document(order_id).update(updateStatus)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Map<String,Object> userOrder = new HashMap<>();
                                                        userOrder.put("order_id",order_id);
                                                        userOrder.put("time",FieldValue.serverTimestamp());
                                                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id)
                                                                .set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    showConfirmationLayout();
                                                                }
                                                                else{
                                                                    Toast.makeText(DeliveryActivity.this, "Failed to update user's OrderedList", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else{
                                                        Toast.makeText(DeliveryActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put( "MID" , M_id);
                paramMap.put( "ORDER_ID" , order_id);
                paramMap.put( "CUST_ID" , customer_id);
                paramMap.put( "CHANNEL_ID" , "WAP");
                paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2));
                paramMap.put( "WEBSITE" , "WEBSTAGING");
                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                paramMap.put( "CALLBACK_URL", callBackUrl);
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void cod(){
        getQtyIds = false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this, OTPverificationActivity.class);
        otpIntent.putExtra("mobileNo",mobileNo.substring(0,10));
        otpIntent.putExtra("orderID",order_id);
        startActivity(otpIntent);
    }
}
