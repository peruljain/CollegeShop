package learncodeonline.in.mymall.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import learncodeonline.in.mymall.R;
import learncodeonline.in.mymall.product.ProductDetailActivity;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_scroll_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {
        String resource = horizontalProductScrollModelList.get(position).getProductImage();
      String name = horizontalProductScrollModelList.get(position).getProductName();
      String description = horizontalProductScrollModelList.get(position).getProductDescription();
      String price = horizontalProductScrollModelList.get(position).getProductPrice();
      String productID = horizontalProductScrollModelList.get(position).getProductID();

      holder.setData(resource, name, description, price, productID);
    }

    @Override
    public int getItemCount() {
        if(horizontalProductScrollModelList.size() > 8){
            return 8;
        }else {
            return horizontalProductScrollModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName;
        private TextView productDescription;
        private TextView productPrice;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.hs_product_image);
            productName = itemView.findViewById(R.id.hs_product_name);
            productDescription = itemView.findViewById(R.id.hs_product_description);
            productPrice = itemView.findViewById(R.id.hs_product_price);

        }

        private void setData(String resource, String title, String description, String price, final String productID){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.smallplaceholder)).into(productImage);
            productName.setText(title);
            productDescription.setText(description);
            productPrice.setText(price);

            if(!title.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailIntent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                        productDetailIntent.putExtra("PRODUCT_ID",productID);
                        itemView.getContext().startActivity(productDetailIntent);
                    }
                });
            }
        }
    }
}
