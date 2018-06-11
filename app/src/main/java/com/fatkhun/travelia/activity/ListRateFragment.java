package com.fatkhun.travelia.activity;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatkhun.travelia.Utils.ItemClickSupportUtils;
import com.fatkhun.travelia.Utils.NetworkUtils;
import com.fatkhun.travelia.adapter.HomeWisataAdapter;
import com.fatkhun.travelia.adapter.RatingAdapter;
import com.fatkhun.travelia.helper.SQLiteHandler;
import com.fatkhun.travelia.model.Rating;
import com.fatkhun.travelia.model.TourWisata;
import com.fatkhun.travelia.service.RatingService;
import com.fatkhun.travelia.service.TourWisataService;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListRateFragment extends Fragment {
    private SQLiteHandler db;
    /**
     *
     */
    public static final String KEY_RATEWISATA = "key_ratewisata";
    private static final String TAG = "LisrRateFragment";
    /**
     *
     */
    private static final String BASE_URL = "http://192.168.43.36:8000/";

    /**
     *
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     *
     */
    private ArrayList<Rating> mRateWisatas;

    /**
     *
     */
    private RatingAdapter mRateWisataAdapter;

    /**
     *
     */
    private TextView mTvSwipeDownInfo;

    /**
     *
     */
    private Context mContext;

    /**
     *
     */
    private Disposable mNetworkConnectivityObserver;

    /**
     *
     */
    private RatingService mRateWisataService;

    /**
     *
     */
    private Call<String> mCall;

    /**
     *
     */

    private Callback<String> mCallback = new Callback<String>() {
        @Override
        public void onResponse(@NonNull Call<String> call, @NonNull final Response<String> response) {
            String jsonResponse = response.body();
            mRateWisatas.clear();

            try {
                JSONObject object = new JSONObject(jsonResponse);
                JSONArray jsonArray = object.getJSONArray("result");

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int WisataId = jsonObject.getInt("id");
                        int WIsataUserId = jsonObject.getInt("user_id");
                        String WisataNama = jsonObject.getString("nama_wisata");
                        String WisataNamaUser = jsonObject.getString("nama");
                        int WisataRate = jsonObject.getInt("rating");
                        String WisataReview = jsonObject.getString("review");


                        mRateWisatas.add(
                                new Rating(
                                        WisataId,
                                        WIsataUserId,
                                        WisataNama,
                                        WisataNamaUser,
                                        WisataRate,
                                        WisataReview
                                ));

                    }
                }

                mRateWisataAdapter.notifyDataSetChanged();
                mTvSwipeDownInfo.setText(R.string.swipe_down_info);
                mSwipeRefreshLayout.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(@NonNull Call<String> mCall, @NonNull Throwable t) {
            t.printStackTrace();
        }
    };


    @Override
    public void onAttach(Context mContext) {
        super.onAttach(mContext);
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_list_rate, container, false);

        if (savedInstanceState != null) {
            mRateWisatas = savedInstanceState.getParcelableArrayList(KEY_RATEWISATA);
        } else {
            mRateWisatas = new ArrayList<>();
        }

        mRateWisataService = (RatingService) NetworkUtils.fetchUrl(BASE_URL, RatingService.class);

        mTvSwipeDownInfo = viewRoot.findViewById(R.id.tv_swipe_down_info);

        mSwipeRefreshLayout = viewRoot.findViewById(R.id.srl_list);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        // SQLite database handler
        db = new SQLiteHandler(getActivity());

        HashMap<String, String> user = db.getUserDetails();
        String apitoken = user.get("api_token");
        Log.i(TAG, "onResponse: Rate " + apitoken);
        mNetworkConnectivityObserver = ReactiveNetwork.observeNetworkConnectivity(mContext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.getState() == NetworkInfo.State.CONNECTED) {
                        if (savedInstanceState == null) {
                            mSwipeRefreshLayout.setEnabled(true);
                            mSwipeRefreshLayout.setRefreshing(true);
                            mTvSwipeDownInfo.setText(R.string.synchronizing);

                            mCall = mRateWisataService.getRateWisata(apitoken);
                            mCall.enqueue(mCallback);
                        }
                    } else {
                        mTvSwipeDownInfo.setText(R.string.network_is_not_connected);
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mTvSwipeDownInfo.setText(R.string.synchronizing);

            mCall = mRateWisataService.getRateWisata(apitoken);
            mCall.enqueue(mCallback);
        });

        RecyclerView wisataRecyclerView = viewRoot.findViewById(R.id.rv_rate);
        wisataRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        wisataRecyclerView.setLayoutManager(layoutManager);
        wisataRecyclerView.setItemAnimator(new DefaultItemAnimator());
        wisataRecyclerView.setAdapter(mRateWisataAdapter);


//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        wisataRecyclerView.setLayoutManager(layoutManager);

        mRateWisataAdapter = new RatingAdapter(mRateWisatas);
        wisataRecyclerView.setAdapter(mRateWisataAdapter);

//        ItemClickSupportUtils.addTo(wisataRecyclerView)
//                .setOnItemClickListener((recyclerView, position, v) -> {
//                    Intent WisataDetailIntent = new Intent(mContext, DeatilWisataActivity.class);
//                    TourWisata tourWisata = mTourWisatas.get(position);
//
//                    WisataDetailIntent.putExtra("nama", tourWisata.getNama());
//                    WisataDetailIntent.putExtra("deskripsi", tourWisata.getDeskripsi());
//                    WisataDetailIntent.putExtra("info", tourWisata.getInfo());
//                    WisataDetailIntent.putExtra("penginapan", tourWisata.getPenginapan());
//                    WisataDetailIntent.putExtra("transportasi", tourWisata.getTransportasi());
//                    WisataDetailIntent.putExtra("makan", tourWisata.getMakan());
//                    WisataDetailIntent.putExtra("lokasi", tourWisata.getLokasi());
//                    WisataDetailIntent.putExtra("gambar", tourWisata.getGambar());
//                    WisataDetailIntent.putExtra("tiket", tourWisata.getTiket());
//                    WisataDetailIntent.putExtra("harga", tourWisata.getHarga());
//
//
//                    startActivity(WisataDetailIntent);
//                });

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        // SQLite database handler
        db = new SQLiteHandler(getActivity());

        HashMap<String, String> user = db.getUserDetails();
        String apitoken = user.get("api_token");
        Log.i(TAG, "onResponse: Rate " + apitoken);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(true);
        mTvSwipeDownInfo.setText(R.string.synchronizing);

        mCall = mRateWisataService.getRateWisata(apitoken);
        mCall.enqueue(mCallback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_RATEWISATA, mRateWisatas);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNetworkConnectivityObserver.dispose();
    }
}
