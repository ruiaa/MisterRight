package com.misterright.ui.widget.emotion;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.misterright.R;

import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class EmotionAdapter extends ArrayAdapter<Emojicon> {
    private boolean mUseSystemDefault = false;

    public EmotionAdapter(Context context, List<Emojicon> data) {
        super(context, R.layout.emotion_item, data);
        mUseSystemDefault = false;
    }

    public EmotionAdapter(Context context, List<Emojicon> data, boolean useSystemDefault) {
        super(context, R.layout.emotion_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    public EmotionAdapter(Context context, Emojicon[] data) {
        super(context, R.layout.emotion_item, data);
        mUseSystemDefault = false;
    }

    public EmotionAdapter(Context context, Emojicon[] data, boolean useSystemDefault) {
        super(context, R.layout.emotion_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emotion_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (EmojiconTextView) v.findViewById(R.id.emotion_icon);
            holder.icon.setUseSystemDefault(mUseSystemDefault);
            v.setTag(holder);
        }
        Emojicon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(emoji.getEmoji());
        return v;
    }

    static class ViewHolder {
        EmojiconTextView icon;
    }
}
