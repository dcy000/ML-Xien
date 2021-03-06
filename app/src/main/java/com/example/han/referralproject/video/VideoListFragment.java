package com.example.han.referralproject.video;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.han.referralproject.R;
import com.example.han.referralproject.application.MyApplication;
import com.example.han.referralproject.network.NetworkApi;
import com.example.han.referralproject.network.NetworkManager;
import com.example.han.referralproject.util.GridViewDividerItemDecoration;
import com.example.han.referralproject.util.LocalShared;
import com.gzq.lib_core.base.Box;
import com.ml.videoplayer.MlVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VideoListFragment extends Fragment {


    private String mNetless;
    private Bundle mSavedInstanceState;

    public static void addOrShow(FragmentManager fm, int id, int position) {
        Fragment fragment = fm.findFragmentByTag(VideoListFragment.class.getSimpleName());
        FragmentTransaction transaction = fm.beginTransaction();
        if (fragment == null) {
            transaction.add(
                    id,
                    VideoListFragment.newInstance(position),
                    VideoListFragment.class.getSimpleName()
            );
        } else {
            transaction.show(fragment);
        }
        transaction.commitNowAllowingStateLoss();
    }

    private static final String ARG_POSITION = "position";

    private int position;

    public VideoListFragment() {

    }

    public static VideoListFragment newInstance(int position) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    private RecyclerView rvVideos;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        rvVideos = (RecyclerView) view.findViewById(R.id.rv_videos);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rvVideos.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvVideos.setLayoutManager(layoutManager);
        rvVideos.addItemDecoration(new GridViewDividerItemDecoration(30, 52));
        rvVideos.setAdapter(adapter);
        if (getUserVisibleHint()) {
            onShow(savedInstanceState);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null && isVisibleToUser) {
            onShow(mSavedInstanceState);
            return;
        }

        if (getView() != null && !isVisibleToUser) {
            onHide();
        }
    }


    private AtomicInteger init = new AtomicInteger(0);

    private void onShow(Bundle savedInstanceState) {
        //fetchVideos(position);
        if (init.compareAndSet(0, 1)) {
            mNetless = LocalShared.getInstance(getActivity()).getString("netless");
            String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
            if (!TextUtils.isEmpty(noNetless) || TextUtils.isEmpty(mNetless)) {
                loadMore();
                getVideos();
            } else {
                showVideos(getVideoEntities(getType(position)));
            }
        }
//

        // 健康课堂5个
        // 动画片2个
        // 设备简介1个
        // 公司简介1个
    }

    private void onHide() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        init.set(0);
    }

    private String getType(int position) {
        String type = "hypertension";
        switch (position) {
            case 0:
                type = "hypertension";
                break;
            case 1:
                type = "opera";
                break;
            case 2:
                type = "lifetip";
                break;
            case 3:
                type = "cartoon";
                break;
            case 4:
                type = "deviceshow";
                break;
            case 5:
                type = "xienshow";
                break;
            default:
                break;

        }
        return type;
    }

    private void loadMore() {
        rvVideos.addOnScrollListener(loadMoreOnScrollListener);
    }

    private RecyclerView.OnScrollListener loadMoreOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();

            int totalItemCount = recyclerView.getAdapter().getItemCount();
            int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
            int visibleItemCount = recyclerView.getChildCount();
            // 屏幕滑动后停止（空闲状态）
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItemPosition >= totalItemCount - 1
                    && visibleItemCount > 0) {
                page++;
                getVideos();
            }
        }
    };

    private void getVideos() {
        if (position + 1 == 7) {
            if (page == 1) {
                fetchLudeVideos();
            }
            return;
        }

        String version = position == 5 ? "2" : "1";
        NetworkApi.getVideoList(
                position + 1, version, "1", page, pageSize,
                new NetworkManager.SuccessCallback<List<VideoEntity>>() {
                    @Override
                    public void onSuccess(List<VideoEntity> entities) {
                        if (videos == null) {
                            videos = new ArrayList<>();
                        }
                        videos.addAll(entities);
                        adapter.notifyDataSetChanged();
                    }
                }, new NetworkManager.FailedCallback() {
                    @Override
                    public void onFailed(String message) {
                        if (position == 5) {
                            if (first.compareAndSet(0, 1)) {
                                ArrayList<VideoEntity> entities = getVideoEntities(getType(position));
                                if (videos == null) {
                                    videos = new ArrayList<>();
                                }
                                videos.addAll(entities);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );
    }

    private AtomicInteger first = new AtomicInteger(0);

    private int page = 1;
    private int pageSize = 9;


    private void fetchVideos(int position) {
        switch (position) {
            case 0:
                //tag1=1
                provideVideos("hypertension", "健康课堂");
                break;
            case 1:
                //tag1=2
                provideVideos(/*"stroke"*/"opera", "曲艺天地");
                break;
            case 2:
                //tag1=3
                provideVideos("lifetip", "生活助手");
                break;
            case 3:
                //tag1=4
                provideVideos(/*"palsy"*/"cartoon", "动画片");
                break;
            default:
                break;
        }
    }

    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String BASE_URL = "http://oyptcv2pb.bkt.clouddn.com/";

    public void fetchLudeVideos() {
        ArrayList<VideoEntity> entities = new ArrayList<>();
        VideoEntity entity;
        entity = new VideoEntity(
                BASE_PATH + "/lude1/血糖检测.mp4",
                "血糖检测"
        );
        entities.add(entity);
        entity = new VideoEntity(
                BASE_PATH + "/lude1/体温检测.mp4",
                "体温检测"
        );
        entities.add(entity);
        entity = new VideoEntity(
                BASE_PATH + "/lude1/血氧检测.mp4",
                "血氧检测"
        );
        entities.add(entity);
        entity = new VideoEntity(
                BASE_PATH + "/lude1/三合一检测.mp4",
                "三合一检测"
        );
        entities.add(entity);
        entity = new VideoEntity(
                BASE_PATH + "/lude1/体重检测.mp4",
                "体重检测"
        );
        entities.add(entity);
        entity = new VideoEntity(
                BASE_PATH + "/lude1/血压检测.mp4",
                "血压检测"
        );
        entities.add(entity);
        entity = new VideoEntity(
                BASE_PATH + "/lude1/心电检测.mp4",
                "心电检测"
        );
        entities.add(entity);
        if (videos == null) {
            videos = new ArrayList<>();
        }
        videos.addAll(entities);
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private ArrayList<VideoEntity> getVideoEntities(String type) {
        String dir = BASE_PATH + "/lude/" + type;
        File file = new File(dir);
        ArrayList<VideoEntity> videos = new ArrayList<>();
        try {
            if (file.exists() && file.isDirectory()) {
                String[] filenames = file.list();
                for (String filename : filenames) {
                    String path = dir + File.separator + filename;
                    String title = filename.split("\\.")[0];
                    videos.add(new VideoEntity(path, title));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            videos = new ArrayList<>();
        }
        return videos;
    }

    private void provideVideos(String type, String extra) {
        int size = provideSize(type);
        List<VideoEntity> entities = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String url = BASE_URL + type + i + ".mp4";
            String title = mTitleMap.get(type).get(i);
            entities.add(new VideoEntity(url, title));
        }
        showVideos(entities);
    }

    private int provideSize(String type) {
        switch (type) {
            case "hypertension":
                //tag1=1
                return 18;
            case "opera":
                //tag1=2
                return 7;
            case "lifetip":
                //tag1=3
                return 21;
            //tag1=4
            case "cartoon":
                return 18;
        }
        return 7;
    }

    private HashMap<String, List<String>> mTitleMap;

    {
        mTitleMap = new HashMap<>();
        List<String> hypertensions = new ArrayList<>();
        hypertensions.add("高血压常识");
        hypertensions.add("糖尿病常识");
        hypertensions.add("糖尿病之胰岛素作用");
        hypertensions.add("防暑小技巧");
        hypertensions.add("防蚊技巧");
        hypertensions.add("空调病防止");
        hypertensions.add("止泻常识");
        hypertensions.add("擦伤处理");
        hypertensions.add("孕产期保健知识讲座");
        hypertensions.add("人体老化时间表");
        hypertensions.add("一般人群的膳食建议");
        hypertensions.add("正确拨打120");
        hypertensions.add("急救知识普及");
        hypertensions.add("旅游时崴脚、骨折怎么办");
        hypertensions.add("如何健康吃油");
        hypertensions.add("上班族怎么保护腰颈肩膝盖");
        hypertensions.add("细嚼慢咽真的能减肥吗");
        hypertensions.add("心肺复苏急救知识");
        mTitleMap.put("hypertension", hypertensions);
        List<String> operas = new ArrayList<>();
        operas.add("曲艺天地1");
        operas.add("曲艺天地2");
        operas.add("曲艺天地3");
        operas.add("曲艺天地4");
        operas.add("曲艺天地5");
        operas.add("曲艺天地6");
        operas.add("曲艺天地7");
        mTitleMap.put("opera", operas);
        List<String> cartoons = new ArrayList<>();
        cartoons.add("猫和老鼠");
        cartoons.add("喜羊羊与灰太狼");
        cartoons.add("蓝色的地球");
        cartoons.add("贝贝熊");
        cartoons.add("猫和老鼠2");
        cartoons.add("白天与黑夜");
        cartoons.add("猫和老鼠3");
        cartoons.add("团团转的地球");
        cartoons.add("发现新世界");
        cartoons.add("幼儿园的一天");
        cartoons.add("虫儿飞飞");
        cartoons.add("去野营");
        cartoons.add("爱哭的熊二");
        cartoons.add("种西瓜");
        cartoons.add("突突来俺家");
        cartoons.add("爱要长高");
        cartoons.add("门外的陌生人");
        cartoons.add("旧玩具");
        mTitleMap.put("cartoon", cartoons);
        List<String> lifetips = new ArrayList<>();
        lifetips.add("生活小技巧1");
        lifetips.add("生活小技巧2");
        lifetips.add("生活小技巧3");
        lifetips.add("生活小技巧4");
        lifetips.add("生活小技巧5");
        lifetips.add("生活小技巧6");
        lifetips.add("生活小技巧7");
        lifetips.add("生活小技巧8");
        lifetips.add("袜子这样跌不起球");
        lifetips.add("1秒钟叠衬衫T恤");
        lifetips.add("对付手脚干裂的小妙招");
        lifetips.add("八个清洁小技巧");
        lifetips.add("落枕了怎么办");
        lifetips.add("葡萄干和提子怎么洗");
        lifetips.add("数据线还能这么用");
        lifetips.add("衣服收纳大全");
        lifetips.add("这五种水果最难切了");
        lifetips.add("几种牢固绳结实用打法");
        lifetips.add("生活健康常识");
        lifetips.add("切洋葱不流泪");
        lifetips.add("用过之后生活立变");
        mTitleMap.put("lifetip", lifetips);
    }

    private void showVideos(List<VideoEntity> entities) {
        videos = entities;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private List<VideoEntity> videos;

    private Adapter adapter = new Adapter();

    private class Adapter extends RecyclerView.Adapter<Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_health_video, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            final VideoEntity entity = videos.get(position);
            holder.tvTitle.setText(entity.getTitle());
            String imageurl = entity.getImageurl();
            if (imageurl == null) {
                holder.ivThumbnail.setImageResource(R.drawable.ic_thumbnail_placeholder);
                return;
            }
            Glide.with(holder.ivThumbnail.getContext())
                    .load(imageurl)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_thumbnail_placeholder))
                    .into(holder.ivThumbnail);
        }

        @Override
        public int getItemCount() {
            return videos == null ? 0 : videos.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        private ImageView ivThumbnail;
        private TextView tvTitle;

        public Holder(final View itemView) {
            super(itemView);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_video_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_video_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    VideoEntity entity = videos.get(position);
                    Context context = itemView.getContext();
//                    Intent intent = new Intent(context, MlVideoPlayerActivity.class);
//                    intent.putExtra("url", entity.getVideourl());
//                    context.startActivity(intent);
                    MlVideoPlayer.play(context, entity.getVideourl(), entity.getTitle());
                }
            });
        }
    }

    @Override
    public void onResume() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        MlVideoPlayer.release();
        super.onPause();
    }
}