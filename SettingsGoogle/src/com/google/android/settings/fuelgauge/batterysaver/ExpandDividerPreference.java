package com.google.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;

class ExpandDividerPreference extends Preference {
    static final String PREFERENCE_KEY = "expandable_divider";
    ImageView mImageView;
    private boolean mIsExpanded;
    private OnExpandListener mOnExpandListener;
    TextView mTextView;
    private String mTitleContent;

    public interface OnExpandListener {
        void onExpand(boolean z);
    }

    public ExpandDividerPreference(Context context) {
        this(context, null);
    }

    ExpandDividerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mIsExpanded = false;
        mTitleContent = null;
        setLayoutResource(R.layout.preference_expand_divider);
        setKey(PREFERENCE_KEY);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        mTextView = (TextView) preferenceViewHolder.findViewById(R.id.expand_title);
        mImageView = (ImageView) preferenceViewHolder.findViewById(R.id.expand_icon);
        refreshState();
    }

    @Override
    public void onClick() {
        setExpanded(!mIsExpanded);
        OnExpandListener onExpandListener = mOnExpandListener;
        if (onExpandListener != null) {
            onExpandListener.onExpand(mIsExpanded);
        }
    }

    public void setTitle(String str) {
        mTitleContent = str;
        refreshState();
    }

    public void setExpanded(boolean z) {
        mIsExpanded = z;
        refreshState();
    }

    public boolean isExpended() {
        return mIsExpanded;
    }

    public void setOnExpandListener(OnExpandListener onExpandListener) {
        mOnExpandListener = onExpandListener;
    }

    private void refreshState() {
        int i;
        ImageView imageView = mImageView;
        if (imageView != null) {
            if (mIsExpanded) {
                i = R.drawable.ic_settings_expand_less;
            } else {
                i = R.drawable.ic_settings_expand_more;
            }
            imageView.setImageResource(i);
        }
        TextView textView = mTextView;
        if (textView != null) {
            textView.setText(mTitleContent);
        }
    }
}
