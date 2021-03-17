package learncodeonline.in.mymall.order;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrdersFragment extends Fragment {

    private RecyclerView myorderRecyclerView;
    public static MyOrderAdapter myOrderAdapter;
    private Dialog loadingDialog;

    public MyOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        /////// loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////// loading dialog

        myorderRecyclerView = view.findViewById(R.id.my_orders_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myorderRecyclerView.setLayoutManager(layoutManager);


        myOrderAdapter = new MyOrderAdapter(DBqueries.myOrderItemModelList, loadingDialog);
        myorderRecyclerView.setAdapter(myOrderAdapter);

        DBqueries.loadOrders(getContext(),myOrderAdapter,loadingDialog);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        myOrderAdapter.notifyDataSetChanged();
    }
}
