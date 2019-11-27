package com.happier.crow.children;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.happier.crow.R;

public class ChildrenInfoActivity extends AppCompatActivity {

    private ImageView ivHeader;
    private EditText etName;
    private EditText etAge;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private EditText etProfile;
    private Button btnSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_info);

        findViews();
    }

    private void findViews() {
        ivHeader = findViewById(R.id.m_iv_children_header_info);
        etName = findViewById(R.id.m_et_children_set_name);
        etAge = findViewById(R.id.m_et_children_set_age);
        rgGender = findViewById(R.id.m_rb_children_set_gender);
        rbMale = findViewById(R.id.m_rb_children_gender_male);
        rbFemale = findViewById(R.id.m_rb_children_gender_female);
        etProfile = findViewById(R.id.m_et_children_profile);
        btnSet = findViewById(R.id.m_btn_children_set_info);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
