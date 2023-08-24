package vip.chuansvip.gongyunxiaozhu.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;


import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.holder.BannerImageHolder;

import java.util.List;

import vip.chuansvip.gongyunxiaozhu.view.RoundImageView;

public abstract class MyBannerAdapter<T> extends BannerAdapter<T, BannerImageHolder> {

    public MyBannerAdapter(List<T> mData) {
        super(mData);
    }

    @Override
    public BannerImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new RoundImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerImageHolder(imageView);
    }

}
