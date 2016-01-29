package com.getiton.android.app.rgb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.getiton.android.app.core.ApplicationController;
import com.getiton.android.app.core.CoreActivity;
import com.getiton.android.app.core.bindable.BindableRecyclerViewAdapter;
import com.getiton.android.app.core.collections.ArrayQueryable;
import com.getiton.android.app.core.data.NamedLanModulesViewModel;
import com.getiton.android.app.dialog.EditColorDialogFragment;
import com.getiton.android.app.rgb.color.SavedColor;
import com.getiton.android.app.rgb.color.SavedColorListitemView;
import com.getiton.android.app.ui.ViewUtils;
import com.getiton.android.app.ui.shadows.viewgroups.ShadowFrameLayout;
import com.getiton.android.app.ui.views.text.LgioEditText;
import com.getiton.android.app.ui.views.text.LgioTextView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.christophesmet.getiton.library.GetItOn;
import com.christophesmet.getiton.library.core.discovery.lan.repo.model.LanModule;
import com.christophesmet.getiton.library.logging.ILoggingService;
import com.christophesmet.getiton.library.modules.RGBV1Module;
import com.christophesmet.getiton.library.utils.GsonParser;
import com.christophesmet.ledsgetiton.R;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.functions.Func1;

/**
 * Created by christophesmet on 15/09/15.
 */

public class RgbV1Activity extends CoreActivity {

    private static final String PREFS_COLORS = "PREFS_COLORS";
    private static final String KEY_COLORS = "COLORSs";

    @InjectView(R.id.txt_title)
    LgioTextView mTxtTitle;
    @InjectView(R.id.txt_title_edit)
    LgioEditText mTxtTitleEdit;
    @InjectView(R.id.colorpicker)
    ColorPickerView mColorpicker;
    @InjectView(R.id.btn_power)
    ImageView mBtnPower;
    @InjectView(R.id.grd_colors)
    RecyclerView mGrdColors;
    @InjectView(R.id.frm_dragable)
    ShadowFrameLayout mFrmDragable;

    private int DEFAULT_BRIGHTNESS = 30;
    private static final String KEY_MAC_ID = "MAC_ID";
    private static final String KEY_ANIMATE_COLOR_IN = "ANIMATE_COLOR_IN";

    @Inject
    GetItOn mGetItOn;
    @Inject
    NamedLanModulesViewModel mNamedLanModulesViewModel;
    @Inject
    ILoggingService mLoggingService;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.valuebar)
    SeekBar mValuebar;

    View mBtnMicrophone;

    @Nullable
    private RGBV1Module mModule;

    private BindableRecyclerViewAdapter<SavedColor> mAdapter;
    private GridLayoutManager mGridLayoutManager;

    private AudioPulser mAudioPulser;

    //colors
    private ArrayList<SavedColor> mColors;
    private int mLastSelectedColor;

    public static Intent creatIntent(@NonNull Activity act, @NonNull String macId, @Nullable Integer animateColorIn) {
        Intent intent = new Intent(act, RgbV1Activity.class);
        intent.putExtra(KEY_MAC_ID, macId);
        intent.putExtra(KEY_ANIMATE_COLOR_IN, animateColorIn);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ApplicationController) getApplicationContext()).inject(this);
        setContentView(R.layout.rgb_activity);
        ButterKnife.inject(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateUp();
            }
        });
        initView();
        loadModule();
        mColorpicker.setDrawDebug(false);
        mToolbar.inflateMenu(R.menu.rgb_detail_menu);
        mBtnMicrophone = mToolbar.findViewById(R.id.menu_microphone);
    }


    private void initView() {
        mValuebar.setProgress(30);
        mGridLayoutManager = new GridLayoutManager(this, 5, LinearLayoutManager.VERTICAL, false);
        mGrdColors.setLayoutManager(mGridLayoutManager);
        mAdapter = new BindableRecyclerViewAdapter<>(this, R.layout.savedcolor_listitemview, new ArrayQueryable<>(getSavedColors()));
        mGrdColors.setAdapter(mAdapter);
        mGrdColors.requestDisallowInterceptTouchEvent(true);
        registerMeasure(mFrmDragable);
    }

    private void initSavedColorsView() {
        final float minY = mFrmDragable.getY();
        float visibleHintHeight = ViewUtils.convertDpToPixel(120, this);
        final float maxY = mFrmDragable.getY() + mFrmDragable.getMeasuredHeight() - visibleHintHeight;
        ViewUtils.makeVerticallyDragable(mFrmDragable, minY, maxY);
        mFrmDragable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.animate(mFrmDragable).withLayer().y(v.getY() == minY ? maxY : minY);
            }
        });
        mFrmDragable.setY(maxY);
    }

    private void registerMeasure(final View v) {
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                initSavedColorsView();
            }
        });
    }

    private void loadModule() {
        //Initial off
        showModuleOn(false, false);
        AndroidObservable.bindActivity(this, mGetItOn.getModuleForId(getIntent().getStringExtra(KEY_MAC_ID)))
                .subscribe(new Subscriber<LanModule>() {
                    @Override
                    public void onCompleted() {
                        if (mModule == null) {
                            onUnableToLoadModule();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(LanModule lanModule) {
                        onModuleLoaded(lanModule);
                    }
                });
    }

    private void onModuleLoaded(@NonNull LanModule module) {
        if (module instanceof RGBV1Module) {
            mModule = (RGBV1Module) module;
            showData(mModule);
            if (mAudioPulser == null) {
                //mAudioPulser = new AudioPulser(mLoggingService,mModule);
            }
            loadListeners();
        } else {
            onUnableToLoadModule();
        }
    }

    private void showData(@NonNull RGBV1Module module) {
        String name = mNamedLanModulesViewModel.getNameForModuleId(module.getMac());
        if (name != null) {
            mTxtTitle.setText(name);
            mTxtTitleEdit.setText(name);
        }
        AndroidObservable.bindActivity(this, checkAnimateIn()
                .singleOrDefault(false))
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean on) {
                        showModuleOn(on, true);
                    }
                });
    }

    private Observable<Boolean> checkAnimateIn() {
        if (getIntent() != null && getIntent().hasExtra(KEY_ANIMATE_COLOR_IN)) {
            int color = getIntent().getIntExtra(KEY_ANIMATE_COLOR_IN, 0);
            if (color != 0) {
                return changeModuleColorTcp(color, DEFAULT_BRIGHTNESS, true)
                        .map(new Func1<Response, Boolean>() {
                            @Override
                            public Boolean call(Response response) {
                                return true;
                            }
                        });
            }
        }
        return Observable.empty();
    }

    private void onUnableToLoadModule() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.detail_module_not_found_title)
                .setMessage(R.string.detail_module_not_found)
                .show();
    }

    private void loadListeners() {
        mColorpicker.setColorListener(new ColorPickerView.ColorListener() {
            @Override
            public void onColorSelected(int color) {
                mLastSelectedColor = color;
                changeModuleColorUdp(color, mValuebar.getProgress());
            }
        });

        mValuebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                changeModuleColorUdp(mLastSelectedColor, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTxtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTitleEdit(true);
            }
        });
        mTxtTitleEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveNewName(mTxtTitleEdit.getText().toString().trim());
                }
                return false;
            }
        });
        mBtnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOnOff();
            }

        });
        mAdapter.setItemClickListener(new BindableRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(@NonNull Object view, int position) {
                SavedColor color = findSavedColorForListitemView((View) view);
                if (color != null) {
                    onSavedColorPicked(color);
                }
            }
        });
        mAdapter.setItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SavedColor color = findSavedColorForListitemView(view);
                if (color != null) {
                    addOrUpdateColor(color);
                }
                return true;
            }
        });
        mBtnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioPulser != null) {
                    mAudioPulser.start();
                }
            }
        });
    }

    @Nullable
    private SavedColor findSavedColorForListitemView(@NonNull View v) {
        if (v instanceof SavedColorListitemView) {
            return ((SavedColorListitemView) v).getColor();
        }
        return null;
    }

    private void changeModuleColorUdp(int color, int brightness) {
        if (mModule != null) {
            int[] rgb = ColorUtils.splitColors(color);
            mModule.requestPwmByUdp(rgb[0], rgb[1], rgb[2], brightness);
        }
    }

    private Observable<Response> changeModuleColorTcp(int color, int brightness, boolean animate) {
        if (mModule != null) {
            int[] rgb = ColorUtils.splitColors(color);
            if (animate) {
                return mModule.requestPwmAnimateByTcp(rgb[0], rgb[1], rgb[2], brightness);
            } else {
                return mModule.requestPwmByTcp(rgb[0], rgb[1], rgb[2], brightness);
            }
        }
        return Observable.empty();
    }


    private void saveNewName(@NonNull String newName) {
        if (mModule != null) {
            mNamedLanModulesViewModel.addNewModule(newName, mModule.getMac());
            mTxtTitle.setText(newName);
            showTitleEdit(false);
        }
    }

    private void showTitleEdit(boolean show) {
        mTxtTitle.setVisibility(show ? View.GONE : View.VISIBLE);
        mTxtTitleEdit.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            mTxtTitleEdit.requestFocus();
        }
    }

    private Observable<Boolean> isScreenModuleOn() {
        //sync from module ?
        return Observable.just(mColorpicker.isEnabled());
    }

    private void showModuleOn(boolean on, boolean animate) {
        mColorpicker.setEnabled(on);
        mValuebar.setEnabled(on);
        float futureAlpha = on ? 1f : 0.2f;

        if (animate) {
            mColorpicker.animate().withLayer().alpha(futureAlpha);
            mValuebar.animate().withLayer().alpha(futureAlpha);
        } else {
            mColorpicker.setAlpha(futureAlpha);
            mValuebar.setAlpha(futureAlpha);
        }
    }

    private void toggleOnOff() {
        if (mModule == null) {
            return;
        }
        AndroidObservable.bindActivity(this,
                isScreenModuleOn()
                        .map(new Func1<Boolean, int[]>() {

                            @Override
                            public int[] call(Boolean on) {
                                if (on) {
                                    return new int[]{0, 0, 0, 0};
                                } else {
                                    int[] rgb = ColorUtils.splitColors(mLastSelectedColor);
                                    int[] rgba = Arrays.copyOf(rgb, rgb.length + 1);
                                    rgba[3] = mValuebar.getProgress();
                                    return rgba;
                                }
                            }
                        })
                        .flatMap(new Func1<int[], Observable<Response>>() {
                            @Override
                            public Observable<Response> call(int[] rgba) {
                                return mModule.requestPwmAnimateByTcp(rgba[0], rgba[1], rgba[2], rgba[3]);
                            }
                        }))
                .flatMap(new Func1<Response, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Response on) {
                        return isScreenModuleOn();
                    }
                })
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoggingService.log(e);
                    }

                    @Override
                    public void onNext(Boolean on) {
                        //Flip the local value, this can come out of sync. But is faster.
                        //maybe needed later ?
                        mLoggingService.log("Result on ?: " + !on);
                        showModuleOn(!on, true);
                    }
                });
    }

    private void onSavedColorPicked(@NonNull SavedColor color) {
        if (color.isAddNewColor()) {
            color.setColor(mColorpicker.getColor());
            addOrUpdateColor(color);
            return;
        }
        mLastSelectedColor = color.getColor();
        final int[] rgb = ColorUtils.splitColors(color.getColor());
        AndroidObservable.bindActivity(this, changeModuleColorTcp(color.getColor(), mValuebar.getProgress(), true))
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response response) {
                        onColorSet(rgb);
                    }
                });
    }

    private void addOrUpdateColor(@NonNull final SavedColor color) {
        EditColorDialogFragment dialog = EditColorDialogFragment.getInstance(color);
        dialog.show(getSupportFragmentManager(), "edit_color");
        dialog.setDeleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSavedColor(color.getId());
            }
        });
        dialog.setSaveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrAddColor(color);
            }
        });
    }

    private void onColorSet(int[] rgb) {
        showModuleOn(rgb[0] != 0 || rgb[1] != 0 || rgb[2] != 0, true);
    }

    private Integer[] getDefaultColors() {
        return new Integer[]
                {
                        0xFF0DFE95,
                        0xFFFFC003,
                        0xFFDE2239
                };
    }

    private void deleteSavedColor(int id) {
        //id is pos
        int pos = findColorPositionForId(id);
        if (pos != -1) {
            mColors.remove(pos);
            mAdapter.notifyItemRemoved(pos);
            saveCurrentColors();
        }
    }

    private int findColorPositionForId(int id) {
        for (int i = 0; i < mColors.size(); i++) {
            if (mColors.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void saveOrAddColor(@NonNull SavedColor updatedColor) {
        if (updatedColor.getId() == -1) {
            //new
            mColors.add(0, new SavedColor(mColors.size(), updatedColor.getColor()));
            mAdapter.notifyItemInserted(0);
        } else {
            int pos = findColorPositionForId(updatedColor.getId());
            if (pos != -1) {
                mColors.get(pos).setColor(updatedColor.getColor());
                mAdapter.notifyItemChanged(pos);
            }
        }
        //Save the current state
        saveCurrentColors();
    }

    private void saveCurrentColors() {
        Integer[] savedColors = new Integer[mColors.size() - 1];
        int counter = 0;
        for (int i = 0; i < mColors.size(); i++) {
            if (!mColors.get(i).isAddNewColor()) {
                savedColors[counter] = mColors.get(i).getColor();
                counter++;
            }
        }
        getSharedPreferences(PREFS_COLORS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_COLORS, GsonParser.get().GsonDataToString(savedColors))
                .apply();

    }

    private ArrayList<SavedColor> getSavedColors() {
        if (mColors != null) {
            return mColors;
        }
        mColors = new ArrayList<>();
        Integer[] colors;
        String jsonColors = getSharedPreferences(PREFS_COLORS, Context.MODE_PRIVATE).getString(KEY_COLORS, null);
        if (jsonColors == null) {
            colors = getDefaultColors();
        } else {
            colors = GsonParser.get().parseFromJsonString(jsonColors, Integer[].class);
            if (colors == null) {
                colors = getDefaultColors();
            }
        }

        for (int i = 0; i < colors.length; i++) {
            mColors.add(new SavedColor(i, colors[i]));
        }
        if (mColors.size() > 5) {
            mColors.add(4, new SavedColor(-1, true));
        } else {
            mColors.add(new SavedColor(-1, true));
        }

        return mColors;
    }
}