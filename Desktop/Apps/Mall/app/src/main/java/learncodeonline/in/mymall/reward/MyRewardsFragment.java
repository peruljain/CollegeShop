package learncodeonline.in.mymall.reward;


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
public class MyRewardsFragment extends Fragment {


    public MyRewardsFragment() {
        // Required empty public constructor
    }

    private RecyclerView myrewardRecyclerView;
    private Dialog loadingDialog;
    public static RewardAdapter rewardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        /////// loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////// loading dialog

        myrewardRecyclerView = view.findViewById(R.id.my_rewards_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myrewardRecyclerView.setLayoutManager(layoutManager);


        if(DBqueries.rewardModelList.size()==0){
            DBqueries.loadRewards(getContext(),loadingDialog, true);
        }else{
            loadingDialog.dismiss();
        }


        rewardAdapter = new RewardAdapter(DBqueries.rewardModelList,false);
        myrewardRecyclerView.setAdapter(rewardAdapter);
        rewardAdapter.notifyDataSetChanged();

        return view;
    }

}
