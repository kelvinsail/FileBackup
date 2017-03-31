package com.yifan.sdcardbackuper.ui.main.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;

import com.yifan.utils.base.BaseDialogFragment;

/**
 * 关于app 弹窗DialogFragment
 *
 * Created by yifan on 2016/12/24.
 */
public class AboutDialogFragment extends BaseDialogFragment {

    public static final String TAG = "AboutDialogFragment";

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        return dialog;
    }
}
