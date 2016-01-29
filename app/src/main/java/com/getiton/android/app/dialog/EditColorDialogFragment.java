package com.getiton.android.app.dialog;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.getiton.android.app.rgb.ColorUtils;
import com.getiton.android.app.rgb.color.SavedColor;
import com.getiton.android.app.ui.views.text.LgioEditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.ledsgetiton.R;

/**
 * Created by christophesmet on 09/11/15.
 */

public class EditColorDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_SAVED_COLOR_ARG = "SAVED_COLOR_ARG";

    @InjectView(R.id.btn_close)
    ImageView mBtnClose;
    @InjectView(R.id.btn_delete)
    ImageView mBtnDelete;
    @InjectView(R.id.btn_save)
    ImageView mBtnSave;
    @InjectView(R.id.frm_color)
    FrameLayout mFrmColor;
    @InjectView(R.id.seekbar_r)
    SeekBar mSeekbarR;
    @InjectView(R.id.txt_r_value)
    LgioEditText mTxtRValue;
    @InjectView(R.id.seekbar_g)
    SeekBar mSeekbarG;
    @InjectView(R.id.txt_g_value)
    LgioEditText mTxtGValue;
    @InjectView(R.id.seekbar_b)
    SeekBar mSeekbarB;
    @InjectView(R.id.txt_b_value)
    LgioEditText mTxtBValue;

    //Data
    @Nullable
    private SavedColor mColor;

    //Listener
    private View.OnClickListener mDeleteListener;
    private View.OnClickListener mSaveListener;

    public static EditColorDialogFragment getInstance(@NonNull SavedColor color) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SAVED_COLOR_ARG, color);
        EditColorDialogFragment output = new EditColorDialogFragment();
        output.setArguments(bundle);
        output.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);
        return output;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(KEY_SAVED_COLOR_ARG)) {
            mColor = getArguments().getParcelable(KEY_SAVED_COLOR_ARG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.color_dialog_fragment, container, false);
        ButterKnife.inject(this, v);
        initViews();
        loadData();
        loadListeners();
        return v;
    }

    private void initViews() {
        mSeekbarR.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(0xff303C40, PorterDuff.Mode.MULTIPLY));
        mSeekbarG.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(0xff303C40, PorterDuff.Mode.MULTIPLY));
        mSeekbarB.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(0xff303C40, PorterDuff.Mode.MULTIPLY));
    }

    private void loadData() {
        if (mColor != null) {
            int[] rgb = ColorUtils.splitColors(mColor.getColor());
            setR(rgb[0]);
            setG(rgb[1]);
            setB(rgb[2]);
        }
    }

    private void setR(int requestedValue) {
        requestedValue = normalizeColor(requestedValue);
        mSeekbarR.setProgress(requestedValue);
        mTxtRValue.setText(String.valueOf(requestedValue));
        updatePreviewColor();
    }

    private void setG(int requestedValue) {
        requestedValue = normalizeColor(requestedValue);
        mSeekbarG.setProgress(requestedValue);
        mTxtGValue.setText(String.valueOf(requestedValue));
        updatePreviewColor();
    }

    private void setB(int requestedValue) {
        requestedValue = normalizeColor(requestedValue);
        mSeekbarB.setProgress(requestedValue);
        mTxtBValue.setText(String.valueOf(requestedValue));
        updatePreviewColor();
    }

    private void updatePreviewColor() {
        mFrmColor.setBackgroundColor(ColorUtils.combineRGB(mSeekbarR.getProgress(), mSeekbarG.getProgress(), mSeekbarB.getProgress()));
        mFrmColor.invalidate();
    }

    private int normalizeColor(int channel) {
        if (channel < 0) {
            return 0;
        } else if (channel > 255) {
            return 255;
        } else {
            return channel;
        }
    }

    public void setDeleteListener(View.OnClickListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public void setSaveListener(View.OnClickListener saveListener) {
        mSaveListener = saveListener;
    }

    private void loadListeners() {
        mSeekbarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setR(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekbarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setB(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekbarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setG(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTxtRValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTxtRValue.removeTextChangedListener(this);
                setR(parseColorForStringByte(s));
                mTxtRValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTxtGValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTxtGValue.removeTextChangedListener(this);
                setG(parseColorForStringByte(s));
                mTxtGValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTxtBValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTxtBValue.removeTextChangedListener(this);
                setB(parseColorForStringByte(s));
                mTxtBValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireDeleteListener();
                dismiss();
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColor != null) {
                    mColor.setColor(ColorUtils.combineRGB(mSeekbarR.getProgress(), mSeekbarG.getProgress(), mSeekbarB.getProgress()));
                }
                fireSaveListener();
                dismiss();
            }
        });
    }

    private void fireSaveListener() {
        if (mSaveListener != null) {
            mSaveListener.onClick(mBtnSave);
        }
    }

    private void fireDeleteListener() {
        if (mDeleteListener != null) {
            mDeleteListener.onClick(mBtnDelete);
        }
    }

    public SavedColor getColor() {
        return mColor;
    }

    private int parseColorForStringByte(@NonNull CharSequence input) {
        try {
            return Integer.parseInt(String.valueOf(input));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}