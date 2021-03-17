package learncodeonline.in.mymall.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import learncodeonline.in.mymall.R;
import learncodeonline.in.mymall.product.ProductDetailActivity;
import learncodeonline.in.mymall.wishlist.WishlistModel;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastPosition = -1;

    public HomePageAdapter(List<HomePageModel> homePageModelList ) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
}

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;

            case 1:
                return HomePageModel.STRIP_AD_BANNER;

            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;

            case 3:
                return HomePageModel.GRID_PRODUCT_VIEW;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);
            case HomePageModel.STRIP_AD_BANNER:
                View stripAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_aid_layout, parent, false);
                return new StripAdBannerViewHolder(stripAdView);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductViewHolder(horizontalProductView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridProductView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;
            case HomePageModel.STRIP_AD_BANNER:
                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();
                ((StripAdBannerViewHolder) holder).setStripAd(resource, color);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String layoutcolor = homePageModelList.get(position).getBackgroundColor();
                String horizontalLayoutTitle = homePageModelList.get(position).getTitle();
                List<WishlistModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModelList, horizontalLayoutTitle, layoutcolor,viewAllProductList);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String gridlayoutcolor = homePageModelList.get(position).getBackgroundColor();
                String gridLayoutTitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductScrollModelList, gridLayoutTitle, gridlayoutcolor);
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
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager bannerSliderViewPager;
        private int currentPage;
        private Timer timer;
        final private long DELAY_TIME = 2000;
        final private long PERIOD_TIME = 2000;
        private List<SliderModel> arrangedlist;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);
        }

        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {
            currentPage = 2;
            if(timer != null){
                timer.cancel();
            }
            arrangedlist = new ArrayList<>();
            for(int x=0;x<sliderModelList.size();x++){
                arrangedlist.add(x,sliderModelList.get(x));
            }
            arrangedlist.add(0,sliderModelList.get(sliderModelList.size()-2));
            arrangedlist.add(1,sliderModelList.get(sliderModelList.size()-1));
            arrangedlist.add(sliderModelList.get(0));
            arrangedlist.add(sliderModelList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangedlist);
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);
            bannerSliderViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedlist);
                    }
                }
            };
            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideShow(arrangedlist);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedlist);
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow(arrangedlist);
                    }
                    return false;
                }
            });
        }

        private void pageLooper(List<SliderModel> sliderModelList) {
            if (currentPage == sliderModelList.size() - 2) {
                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = sliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
        }

        private void startBannerSlideShow(final List<SliderModel> sliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerSlideShow() {
            timer.cancel();
        }
    }

    public class StripAdBannerViewHolder extends RecyclerView.ViewHolder {

        private ImageView stripAdImage;
        private ConstraintLayout stripAdContainer;

        public StripAdBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            stripAdImage = itemView.findViewById(R.id.strip_aid_image);
            stripAdContainer = itemView.findViewById(R.id.strip_aid_container);
        }

        private void setStripAd(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.largeplaceholder)).into(stripAdImage);
            stripAdContainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout horizontalLayoutContainer;
        private TextView horizontalLayoutTitle;
        private Button horizontalviewAllbtn;
        private RecyclerView horizontalRecyclerView;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalLayoutContainer = itemView.findViewById(R.id.horizontal_layout_container);
            horizontalLayoutTitle = itemView.findViewById(R.id.hs_layout_title);
            horizontalviewAllbtn = itemView.findViewById(R.id.hs_view_all);
            horizontalRecyclerView = itemView.findViewById(R.id.hs_layout_recycler_view);
            horizontalRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String layoutcolor, final List<WishlistModel> viewAllProductList) {

            horizontalLayoutContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(layoutcolor)));
            horizontalLayoutTitle.setText(title);

            if (horizontalProductScrollModelList.size() > 8) {
                horizontalviewAllbtn.setVisibility(View.VISIBLE);
                horizontalviewAllbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewAllProductList;
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code",0);
                        viewAllIntent.putExtra("title",title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            } else {
                horizontalviewAllbtn.setVisibility(View.INVISIBLE);
            }

            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerView.setLayoutManager(linearLayoutManager);


            horizontalRecyclerView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout gridLayoutContainer;
        private TextView gridLayoutTitle;
        private Button gridLayoutBtn;
        private GridLayout gridproductlayout;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridLayoutTitle = itemView.findViewById(R.id.grid_product_title);
            gridLayoutBtn = itemView.findViewById(R.id.grid_product_btn);
            gridproductlayout = itemView.findViewById(R.id.grid_layout);
            gridLayoutContainer = itemView.findViewById(R.id.grid_layout_container);
        }

        private void setGridProductLayout(final List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String gridlayoutcolor) {
            gridLayoutContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(gridlayoutcolor)));
            gridLayoutTitle.setText(title);

             for(int x=0;x<4;x++){
                 ImageView productImage = gridproductlayout.getChildAt(x).findViewById(R.id.hs_product_image);
                 TextView productTitle = gridproductlayout.getChildAt(x).findViewById(R.id.hs_product_name);
                 TextView productPrice = gridproductlayout.getChildAt(x).findViewById(R.id.hs_product_price);
                 TextView productDescription = gridproductlayout.getChildAt(x).findViewById(R.id.hs_product_description);

                 Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.smallplaceholder)).into(productImage);
                 productTitle.setText(horizontalProductScrollModelList.get(x).getProductName());
                 productPrice.setText("Rs."+horizontalProductScrollModelList.get(x).getProductPrice()+"/-");
                 productDescription.setText(horizontalProductScrollModelList.get(x).getProductDescription());
                 gridproductlayout.getChildAt(x).setBackgroundColor(Color.parseColor("#FFFFFF"));

                 if(!title.equals("")) {
                     final int finalX = x;
                     gridproductlayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                             productDetailsIntent.putExtra("PRODUCT_ID",horizontalProductScrollModelList.get(finalX).getProductID());
                             itemView.getContext().startActivity(productDetailsIntent);
                         }
                     });
                 }

             }

             if(!title.equals("")) {
                 gridLayoutBtn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
                         Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                         viewAllIntent.putExtra("layout_code", 1);
                         viewAllIntent.putExtra("title", title);
                         itemView.getContext().startActivity(viewAllIntent);
                     }
                 });
             }
        }
    }
}
