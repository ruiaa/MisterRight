package com.misterright.ui.widget.emotion;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.misterright.R;
import com.misterright.ui.adapter.SimpleRecyclerAdapter;

import java.util.Arrays;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconRecents;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import io.github.rockerhieu.emojicon.emoji.People;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public class EmotionGridFragment extends Fragment {

    private static final String ARG_USE_SYSTEM_DEFAULTS = "useSystemDefaults";
    private static final String ARG_EMOJICONS = "emojicons";
    private static final String ARG_EMOJICON_TYPE = "emojiconType";

    private static final int EMOTION_COUNT_ROW = 4;
    private static final int EMOTION_COUNT_COLUMN=7;

    public static EmotionGridFragment newInstance(Emojicon[] emojicons, EmojiconRecents recents) {
        return newInstance(Emojicon.TYPE_UNDEFINED, emojicons, recents, false);
    }

    public static EmotionGridFragment newInstance(@Emojicon.Type int type, EmojiconRecents recents, boolean useSystemDefault) {
        return newInstance(type, null, recents, useSystemDefault);
    }

    public static EmotionGridFragment newInstance(@Emojicon.Type int type, Emojicon[] emojicons, EmojiconRecents recents, boolean useSystemDefault) {
        EmotionGridFragment emojiGridFragment = new EmotionGridFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EMOJICON_TYPE, type);
        args.putParcelableArray(ARG_EMOJICONS, emojicons);
        args.putBoolean(ARG_USE_SYSTEM_DEFAULTS, useSystemDefault);
        emojiGridFragment.setArguments(args);
        emojiGridFragment.setRecents(recents);
        return emojiGridFragment;
    }

    private OnEmotionClickedListener mOnEmotionClickedListener;
    private EmojiconRecents mRecents;
    private Emojicon[] mEmojicons;
    private
    @Emojicon.Type
    int mEmojiconType;
    private boolean mUseSystemDefault = false;
    private View rootView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null) {
            mEmojiconType = Emojicon.TYPE_UNDEFINED;
            mEmojicons = People.DATA;
            mUseSystemDefault = false;
        } else {
            //noinspection WrongConstant
            mEmojiconType = bundle.getInt(ARG_EMOJICON_TYPE);
            if (mEmojiconType == Emojicon.TYPE_UNDEFINED) {
                Parcelable[] parcels = bundle.getParcelableArray(ARG_EMOJICONS);
               if (parcels!=null){
                   mEmojicons = new Emojicon[parcels.length];
                   for (int i = 0; i < parcels.length; i++) {
                       mEmojicons[i] = (Emojicon) parcels[i];
                   }
               }else {
                   mEmojicons=new Emojicon[0];
               }
            } else {
                mEmojicons = Emojicon.getEmojicons(mEmojiconType);
            }
            mUseSystemDefault = bundle.getBoolean(ARG_USE_SYSTEM_DEFAULTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.emotion_grid, container, false);
        initRecyclerView(rootView, Arrays.asList(mEmojicons), mUseSystemDefault);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected SimpleRecyclerAdapter<Emojicon> initRecyclerView(View rootView, List<Emojicon> emojiconList, boolean useSystemDefault) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.emotion_grid);
        SimpleRecyclerAdapter<Emojicon> adapter = new SimpleRecyclerAdapter<Emojicon>(
                rootView.getContext(),
                R.layout.emotion_item_recycler,
                emojiconList,
                ((holder, position, model) -> {
                    EmojiconTextView emojiconTextView = (EmojiconTextView) holder.getBinding().getRoot().findViewById(R.id.emotion_icon);
                    emojiconTextView.setUseSystemDefault(useSystemDefault);
                    emojiconTextView.setText(model.getEmoji());
                }),
                ((holder, position, model) -> {
                    holder.getBinding().getRoot().setOnClickListener(v -> {
                        onSelectItem(model);
                    });
                })
        );
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), EMOTION_COUNT_COLUMN);

        recyclerView.setLayoutManager(gridLayoutManager);
        return adapter;
    }

    protected EmotionAdapter initGridView(View rootView, Emojicon[] emojicons, boolean useSystemDefault) {
        GridView gridView = (GridView) rootView.findViewById(R.id.emotion_grid);
        EmotionAdapter adapter = new EmotionAdapter(rootView.getContext(), emojicons, useSystemDefault);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            onSelectItem((Emojicon) (parent.getItemAtPosition(position)));
        });
        return adapter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(ARG_EMOJICONS, mEmojicons);
    }

    public void setOnEmotionClickedListener(OnEmotionClickedListener mOnEmotionClickedListener) {
        this.mOnEmotionClickedListener = mOnEmotionClickedListener;
    }

    private void onSelectItem(Emojicon emojicon) {
        if (mOnEmotionClickedListener != null) {
            mOnEmotionClickedListener.onEmotionClicked(emojicon);
        }
        if (mRecents != null) {
            mRecents.addRecentEmoji(getContext(), emojicon);
        }
    }

    private void setRecents(EmojiconRecents recents) {
        mRecents = recents;
    }

    public interface OnEmotionClickedListener {
        void onEmotionClicked(Emojicon emojicon);
    }
}