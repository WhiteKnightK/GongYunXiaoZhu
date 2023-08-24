package vip.chuansvip.gongyunxiaozhu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import vip.chuansvip.gongyunxiaozhu.R;


public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {
    private float cornerRadius;

    public RoundImageView(Context context) {
        super(context);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 设置圆角半径
        cornerRadius = getResources().getDimension(R.dimen.dp_25);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
