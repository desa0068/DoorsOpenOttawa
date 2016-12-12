package com.algonquincollege.desa0068.doorsopenottawa.custom;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.algonquincollege.desa0068.doorsopenottawa.R;

/**
 * Created by vaibhavidesai on 2016-12-10.
 */

public class CustomEditText  extends EditText implements View.OnTouchListener {

    private TextWatcher mTextWatcher;

    public CustomEditText(Context context) {
        super(context);
        setup();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    public void setup() {



        //set the keyboard to have first letter capital on each sentence
        if (getInputType() == InputType.TYPE_CLASS_TEXT) {
            setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!getText().toString().trim().isEmpty()) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_black_24dp, 0);
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        };

        setOnTouchListener(this);
        addTextChangedListener(mTextWatcher);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null &&
                    event.getRawX() >= (getRight() -
                            getCompoundDrawables()[2].getBounds().width() -
                            16)) {
                setText("");
                return false;
            }
        }
        return false;
    }
}
