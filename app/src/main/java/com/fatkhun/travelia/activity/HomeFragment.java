package com.fatkhun.travelia.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fatkhun.travelia.Utils.ItemClickSupportUtils;
import com.fatkhun.travelia.Utils.NetworkUtils;
import com.fatkhun.travelia.adapter.HomeWisataAdapter;
import com.fatkhun.travelia.adapter.TourWisataAdapter;
import com.fatkhun.travelia.model.TourWisata;
import com.fatkhun.travelia.service.TourWisataService;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private LinearLayout fabContainer;
    private Animation fab1_show, fab1_hide;
    private FloatingActionButton fab, fab1;
    private boolean fabMenuOpen = false;
    /**
     *
     */
    public static final String KEY_TOURWISATA = "key_homewisata";
    private static final String TAG = "HomeFragment";
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
    private ArrayList<TourWisata> mTourWisatas;

    /**
     *
     */
    private HomeWisataAdapter mTourWisataAdapter;

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
    private TourWisataService mTourWisataService;

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
            mTourWisatas.clear();

            try {
                JSONObject object = new JSONObject(jsonResponse);
                JSONArray jsonArray = object.getJSONArray("result");

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int WisataId = jsonObject.getInt("id");
                        String WisataNama = jsonObject.getString("nama");
                        String WisataDeskripsi = jsonObject.getString("deskripsi");
                        String WisataInfo = jsonObject.getString("info");
                        String WisataPenginapan = jsonObject.getString("penginapan");
                        String WisataTranport = jsonObject.getString("transportasi");
                        String WisataMakan = jsonObject.getString("makan");
                        String WisataLokasi = jsonObject.getString("lokasi");
                        String WisataGambar = jsonObject.getString("gambar");
                        int WisataTiket = jsonObject.getInt("tiket");
                        int WisataHarga = jsonObject.getInt("harga");


                        mTourWisatas.add(
                                new TourWisata(
                                        WisataId,
                                        WisataNama,
                                        WisataDeskripsi,
                                        WisataInfo,
                                        WisataPenginapan,
                                        WisataTranport,
                                        WisataMakan,
                                        WisataLokasi,
                                        WisataGambar,
                                        WisataTiket,
                                        WisataHarga
                                ));

                    }
                }

                mTourWisataAdapter.notifyDataSetChanged();
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
        View viewRoot = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState != null) {
            mTourWisatas = savedInstanceState.getParcelableArrayList(KEY_TOURWISATA);
        } else {
            mTourWisatas = new ArrayList<>();
        }

        initCollapsingToolbar();

        mTourWisataService = (TourWisataService) NetworkUtils.fetchUrl(BASE_URL, TourWisataService.class);

        mTvSwipeDownInfo = viewRoot.findViewById(R.id.tv_swipe_down_info);

        mSwipeRefreshLayout = viewRoot.findViewById(R.id.srl_list);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        mNetworkConnectivityObserver = ReactiveNetwork.observeNetworkConnectivity(mContext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.getState() == NetworkInfo.State.CONNECTED) {
                        if (savedInstanceState == null) {
                            mSwipeRefreshLayout.setEnabled(true);
                            mSwipeRefreshLayout.setRefreshing(true);
                            mTvSwipeDownInfo.setText(R.string.synchronizing);

                            mCall = mTourWisataService.getTourWisata();
                            mCall.enqueue(mCallback);
                        }
                    } else {
                        mTvSwipeDownInfo.setText(R.string.network_is_not_connected);
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mTvSwipeDownInfo.setText(R.string.synchronizing);

            mCall = mTourWisataService.getTourWisata();
            mCall.enqueue(mCallback);
        });

        RecyclerView wisataRecyclerView = viewRoot.findViewById(R.id.rv_list);
        wisataRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        wisataRecyclerView.setLayoutManager(mLayoutManager);
        wisataRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        wisataRecyclerView.setItemAnimator(new DefaultItemAnimator());
        wisataRecyclerView.setAdapter(mTourWisataAdapter);


//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        wisataRecyclerView.setLayoutManager(layoutManager);

        mTourWisataAdapter = new HomeWisataAdapter(mTourWisatas);
        wisataRecyclerView.setAdapter(mTourWisataAdapter);

        ItemClickSupportUtils.addTo(wisataRecyclerView)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Intent WisataDetailIntent = new Intent(mContext, DeatilWisataActivity.class);
                    TourWisata tourWisata = mTourWisatas.get(position);

                    WisataDetailIntent.putExtra("nama", tourWisata.getNama());
                    WisataDetailIntent.putExtra("deskripsi", tourWisata.getDeskripsi());
                    WisataDetailIntent.putExtra("info", tourWisata.getInfo());
                    WisataDetailIntent.putExtra("penginapan", tourWisata.getPenginapan());
                    WisataDetailIntent.putExtra("transportasi", tourWisata.getTransportasi());
                    WisataDetailIntent.putExtra("makan", tourWisata.getMakan());
                    WisataDetailIntent.putExtra("lokasi", tourWisata.getLokasi());
                    WisataDetailIntent.putExtra("gambar", tourWisata.getGambar());
                    WisataDetailIntent.putExtra("tiket", tourWisata.getTiket());
                    WisataDetailIntent.putExtra("harga", tourWisata.getHarga());


                    startActivity(WisataDetailIntent);
                });

        return viewRoot;
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
//                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(true);
        mTvSwipeDownInfo.setText(R.string.synchronizing);

        mCall = mTourWisataService.getTourWisata();
        mCall.enqueue(mCallback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_TOURWISATA, mTourWisatas);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNetworkConnectivityObserver.dispose();
    }

}
