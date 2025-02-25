package com.onecricket.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onecricket.APICallingPackage.Class.APIRequestManager;
import com.onecricket.APICallingPackage.Interface.ResponseManager;
import com.onecricket.Bean.BeanAddCashOfferList;
import com.onecricket.R;
import com.onecricket.utils.SessionManager;
import com.onecricket.databinding.ActivityAddCashBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.onecricket.APICallingPackage.Class.Validations.ShowToast;
import static com.onecricket.APICallingPackage.Config.ADDAMOUNTOFFER;
import static com.onecricket.APICallingPackage.Constants.ADDAMOUNTOFFERTYPE;

public class AddCashActivity extends AppCompatActivity implements ResponseManager {

    AddCashActivity activity;
    Context context;
    ResponseManager responseManager;
    APIRequestManager apiRequestManager;

    SessionManager sessionManager;


    String FinalAmountToAdd;
    String EntryFee;
    AdapterAddCashOffertList adapterAddCashOfferList;


    public static String Activity = "";

    ActivityAddCashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_cash);
        context = activity = this;
        sessionManager = new SessionManager();
        responseManager = this;
        apiRequestManager = new APIRequestManager(activity);


        binding.head.tvHeaderName.setText(getResources().getString(R.string.add_cash_head));

        binding.head.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.RVAddCashOffer.setHasFixedSize(true);
        binding.RVAddCashOffer.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        binding.RVAddCashOffer.setLayoutManager(mLayoutManager);
        //Rv_PlayerList.setItemAnimator(new DefaultItemAnimator());
        binding.RVAddCashOffer.setItemAnimator(null);


        try {
            EntryFee = getIntent().getStringExtra("EntryFee");

            if (EntryFee != null) {
                binding.etAddCashEnterAmount.setText(String.valueOf(EntryFee));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Activity = getIntent().getStringExtra("Activity");
            System.out.print(Activity);
            if (Activity == null) {
                Activity = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        binding.tvAddCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinalAmountToAdd = binding.etAddCashEnterAmount.getText().toString();

                if (FinalAmountToAdd.equals("")) {
                    ShowToast(context, getResources().getString(R.string.enter_valid_amt));
                    FinalAmountToAdd = "0";
                } else if (Integer.parseInt(FinalAmountToAdd) < 10) {
                    ShowToast(context, getResources().getString(R.string.enter_min_amt));

                } else {
                    Intent i = new Intent(activity, PaymentOptionActivity.class);
                    i.putExtra("FinalAmount", FinalAmountToAdd);

                    startActivity(i);
                }
            }
        });

        binding.tvOneHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etAddCashEnterAmount.setText(getResources().getString(R.string.hundered));
            }
        });
        binding.tvTwoHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etAddCashEnterAmount.setText(getResources().getString(R.string.two_hundred));
            }
        });
        binding.tvFiveHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etAddCashEnterAmount.setText(getResources().getString(R.string.five_hundred));
            }
        });

        CallAddAmountOffer(true);

    }

    private void CallAddAmountOffer(boolean isShowLoader) {
        try {

            apiRequestManager.callAPI(ADDAMOUNTOFFER,
                    createRequestJson(), context, activity, ADDAMOUNTOFFERTYPE,
                    isShowLoader, responseManager);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    JSONObject createRequestJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", sessionManager.getUser(context).getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void getResult(Context mContext, String type, String message, JSONObject result) {
        binding.RVAddCashOffer.setVisibility(View.VISIBLE);
        binding.LLAddCashOffer.setVisibility(View.VISIBLE);
        try {
            JSONArray jsonArray = result.getJSONArray("data");
            //Log.e("Data",jsonArray.toString());
            List<BeanAddCashOfferList> beanContestLists = new Gson().fromJson(jsonArray.toString(),
                    new TypeToken<List<BeanAddCashOfferList>>() {
                    }.getType());
            adapterAddCashOfferList = new AdapterAddCashOffertList(beanContestLists, activity);
            binding.RVAddCashOffer.setAdapter(adapterAddCashOfferList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterAddCashOfferList.notifyDataSetChanged();
    }


    @Override
    public void onError(Context mContext, String type, String message) {

        binding.RVAddCashOffer.setVisibility(View.GONE);
        binding.LLAddCashOffer.setVisibility(View.GONE);

    }

    public class AdapterAddCashOffertList extends RecyclerView.Adapter<AdapterAddCashOffertList.MyViewHolder> {
        private List<BeanAddCashOfferList> mListenerList;
        Context mContext;


        public AdapterAddCashOffertList(List<BeanAddCashOfferList> mListenerList, Context context) {
            mContext = context;
            this.mListenerList = mListenerList;

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_BonusCashLimit,tv_BonusOfferAmount;
            public MyViewHolder(View view) {
                super(view);
                tv_BonusCashLimit = view.findViewById(R.id.tv_BonusCashLimit);
                tv_BonusOfferAmount=view.findViewById(R.id.tv_BonusOfferAmount);
            }

        }
        @Override
        public int getItemCount() {
            return mListenerList.size();
        }

        @Override
        public AdapterAddCashOffertList.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_add_cash_offer, parent, false);

            return new AdapterAddCashOffertList.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AdapterAddCashOffertList.MyViewHolder holder, final int position) {


            final String max_range = mListenerList.get(position).getMax_range();
            final  String min_range=mListenerList.get(position).getMin_range();
            final  String amount=mListenerList.get(position).getAmount();
            if (!max_range.equals(""))
            {
                holder.tv_BonusCashLimit.setText("Add Cash "+min_range+" ₹ to "+max_range+" ₹");
                holder.tv_BonusOfferAmount.setText("Get "+amount+ " ₹ Bonus ");
            }

        }

    }
}
