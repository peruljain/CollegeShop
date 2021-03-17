package learncodeonline.in.mymall.home;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.MainActivity;
import learncodeonline.in.mymall.R;
import learncodeonline.in.mymall.wishlist.WishlistModel;

import static learncodeonline.in.mymall.DBqueries.categoryModelList;
import static learncodeonline.in.mymall.DBqueries.lists;
import static learncodeonline.in.mymall.DBqueries.loadCategories;
import static learncodeonline.in.mymall.DBqueries.loadFragmentData;
import static learncodeonline.in.mymall.DBqueries.loadedCategoriesNames;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView categoryRecyclerView;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private RecyclerView homepageRecyclerView;
    private HomePageAdapter adapter;
    private ImageView noInternetConnection;
    private Button retryBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        homepageRecyclerView = view.findViewById(R.id.home_page_recycler_view);
        retryBtn = view.findViewById(R.id.retry_button);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homepageRecyclerView.setLayoutManager(testingLayoutManager);

        //////category fake list
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        //////category fake list

        //////home page fake list
        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));

        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"","#dfdfdf"));
        homePageModelFakeList.add(new HomePageModel(2,"","#dfdfdf",horizontalProductScrollModelFakeList,new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3,"","#dfdfdf",horizontalProductScrollModelFakeList));
        //////home page fake list
        categoryAdapter = new CategoryAdapter(categoryModelFakeList);

        adapter = new HomePageAdapter(homePageModelFakeList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homepageRecyclerView.setVisibility(View.VISIBLE);

          if (categoryModelList.size() == 0) {
                loadCategories(categoryRecyclerView,categoryAdapter, getContext());
            } else {
              categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecyclerView.setAdapter(categoryAdapter);
            if (lists.size() == 0) {
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());

                loadFragmentData(homepageRecyclerView, getContext(), 0, "Home");
            } else {
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homepageRecyclerView.setAdapter(adapter);
        } else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            retryBtn.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.forgot_password_image).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            categoryRecyclerView.setVisibility(View.GONE);
            homepageRecyclerView.setVisibility(View.GONE);
        }
        /////////////////////////////////
        ////////refresh layout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
                }
            });
            ////////refresh layout
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               reloadPage();
            }
        });
        return view;
        }

        private void reloadPage(){
            networkInfo = connectivityManager.getActiveNetworkInfo();
//            categoryModelList.clear();
//            lists.clear();
//            loadedCategoriesNames.clear();
            DBqueries.clearData();

            if (networkInfo != null && networkInfo.isConnected() == true) {
                MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                noInternetConnection.setVisibility(View.GONE);
                retryBtn.setVisibility(View.GONE);
                categoryRecyclerView.setVisibility(View.VISIBLE);
                homepageRecyclerView.setVisibility(View.VISIBLE);
                categoryAdapter = new CategoryAdapter(categoryModelFakeList);
                adapter = new HomePageAdapter(homePageModelFakeList);
                categoryRecyclerView.setAdapter(categoryAdapter);
                homepageRecyclerView.setAdapter(adapter);

                loadCategories(categoryRecyclerView,categoryAdapter, getContext());

                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(homepageRecyclerView, getContext(), 0, "Home");
            }else{
                MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                Toast.makeText(getContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();
                retryBtn.setVisibility(View.VISIBLE);
                categoryRecyclerView.setVisibility(View.GONE);
                homepageRecyclerView.setVisibility(View.GONE);
                Glide.with(getContext()).load(R.drawable.forgot_password_image).into(noInternetConnection);
                noInternetConnection.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
