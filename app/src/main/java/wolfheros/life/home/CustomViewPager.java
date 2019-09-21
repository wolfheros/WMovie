package wolfheros.life.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CustomViewPager extends ViewPager{

    private boolean isPagingEnbled;

    public CustomViewPager(@NonNull Context context) {
        super(context);
        this.isPagingEnbled = true;
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.isPagingEnbled = true;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isPagingEnbled && super.onTouchEvent(ev);
    }

    //for some phones to prevent(避免) tab switching keys to show on keyboard
    @Override
    public boolean executeKeyEvent(@NonNull KeyEvent event) {
        return isPagingEnbled && super.executeKeyEvent(event);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        super.addFocusables(views, direction, focusableMode);
    }

    public void setPagingEnabled(boolean enbled){
        this.isPagingEnbled = enbled;
    }
}
