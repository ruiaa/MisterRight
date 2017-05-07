package com.misterright.ui.widget.emotion;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.misterright.ui.adapter.SimpleRecyclerAdapter;

import io.github.rockerhieu.emojicon.EmojiconRecents;
import io.github.rockerhieu.emojicon.EmojiconRecentsManager;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

/**
 * @author Daniele Ricci
 */
public class EmotionRecentsGridFragment extends EmotionGridFragment implements EmojiconRecents {
    private SimpleRecyclerAdapter<Emojicon> adapter;
    private boolean mUseSystemDefault = false;

    private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";

    protected static EmotionRecentsGridFragment newInstance() {
        return newInstance(false);
    }

    public static EmotionRecentsGridFragment newInstance(boolean useSystemDefault) {
        EmotionRecentsGridFragment fragment = new EmotionRecentsGridFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(USE_SYSTEM_DEFAULT_KEY, useSystemDefault);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUseSystemDefault = getArguments().getBoolean(USE_SYSTEM_DEFAULT_KEY);
        } else {
            mUseSystemDefault = false;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        EmojiconRecentsManager recents = EmojiconRecentsManager
                .getInstance(view.getContext());

        adapter = initRecyclerView(view,recents,mUseSystemDefault);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        EmojiconRecentsManager recents = EmojiconRecentsManager
                .getInstance(context);
        recents.push(emojicon);

        // notify dataset changed
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

}