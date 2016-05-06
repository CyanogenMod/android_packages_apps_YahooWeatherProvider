package org.cyanogenmod.yahooweatherprovider;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ImagePreference extends Preference {
    public ImagePreference(Context context) {
        super(context);
    }

    public ImagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        LayoutInflater li = (LayoutInflater)
                getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        return li.inflate(R.layout.image_preference, parent, false);
    }
}
