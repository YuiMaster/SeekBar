package com.hd.seekwidget;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;


import org.jetbrains.annotations.NotNull;

/**
 * @Description: java类作用描述
 * @Author: 作者名
 * @CreateDate: 2019/10/25 0025 11:59
 */
public class DensityUtil {
    private DensityUtil() {
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return dp2px(AppContext.INSTANCE.getContext(), dpValue);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dp2pxf(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, AppContext.INSTANCE.getContext().getResources().getDisplayMetrics());
    }

    public static int dp2px(@NotNull Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @return
     */
    public static int sp2px(@NotNull Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 获取屏幕宽高
     *
     * @param context 上下文
     * @return Point 包含x，y
     */
    public static Point getScreenMetrics(@NotNull Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int wScreen = dm.widthPixels;
        int hScreen = dm.heightPixels;
        return new Point(wScreen, hScreen);
    }

}
