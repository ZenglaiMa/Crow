package com.happier.crow.parent;

import android.content.res.XmlResourceParser;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.happier.crow.R;
import com.happier.crow.address.City;
import com.happier.crow.address.District;
import com.happier.crow.address.Province;
import com.happier.crow.constant.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ModifyParentInfoActivity extends AppCompatActivity {

    private EditText etParentName;
    private RadioButton rbGenderMale;
    private RadioButton rbGenderFemale;
    private EditText etParentAge;
    private Spinner spProvince;
    private Spinner spCity;
    private Spinner spArea;
    private EditText etDetailAddress;
    private Button btnSetInfo;

    private String parentName;
    private String parentAge;
    private int gender;
    private String detailAddress;

    private String provinceName;
    private String cityName;
    private String districtName;

    private Province province = null;
    private List<Province> list = new ArrayList<>();

    private ArrayAdapter<Province> arrayAdapter1;
    private ArrayAdapter<City> arrayAdapter2;
    private ArrayAdapter<District> arrayAdapter3;

    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 0;

    private static final String PARENT_SET_INFO_PATH = "/parent/setInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_parent_info);
        getSupportActionBar().setElevation(0);

        EventBus.getDefault().register(this);

        findViews();

        list = parseAddress();

        initSpinner();

        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = list.get(position);
                arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list.get(position).getCitys());
                spCity.setAdapter(arrayAdapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrayAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, province.getCitys().get(position).getDistricts());
                spArea.setAdapter(arrayAdapter3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provinceName = String.valueOf(spProvince.getSelectedItem());
                cityName = String.valueOf(spCity.getSelectedItem());
                districtName = String.valueOf(spArea.getSelectedItem());
                parentName = etParentName.getText().toString();
                if (rbGenderMale.isChecked()) {
                    gender = GENDER_MALE;
                } else if (rbGenderFemale.isChecked()) {
                    gender = GENDER_FEMALE;
                }
                parentAge = etParentAge.getText().toString();
                detailAddress = etDetailAddress.getText().toString();
                if (!TextUtils.isEmpty(parentName)) {
                    if (!TextUtils.isEmpty(parentAge)) {
                        if (!TextUtils.isEmpty(detailAddress)) {
                            setOrModify();
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入您的详细地址", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请输入您的年龄", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请输入您的姓名", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setOrModify() {
        OkHttpClient client = new OkHttpClient();
        int pid = getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0);
        FormBody body = new FormBody.Builder()
                .add("pid", String.valueOf(pid))
                .add("name", parentName)
                .add("gender", String.valueOf(gender))
                .add("age", parentAge)
                .add("province", provinceName)
                .add("city", cityName)
                .add("area", districtName)
                .add("detailAddress", detailAddress)
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + PARENT_SET_INFO_PATH)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                EventBus.getDefault().post(result);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleResult(String result) {
        if (result.equals("1")) {
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initSpinner() {
        arrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list);
        arrayAdapter2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list.get(0).getCitys());
        arrayAdapter3 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list.get(0).getCitys().get(0).getDistricts());
        spProvince.setAdapter(arrayAdapter1);
        spProvince.setSelection(0, true);
        spCity.setAdapter(arrayAdapter2);
        spCity.setSelection(0, true);
        spArea.setAdapter(arrayAdapter3);
        spArea.setSelection(0, true);
    }

    private List<Province> parseAddress() {
        List<Province> list = null;
        Province province = null;
        List<City> cities = null;
        City city = null;
        List<District> districts = null;
        District district;

        // 创建解析器，并制定解析的xml文件
        XmlResourceParser parser = getResources().getXml(R.xml.cities);
        try {
            int type = parser.getEventType();
            while (type != 1) {
                String tag = parser.getName();//获得标签名
                switch (type) {
                    case XmlResourceParser.START_DOCUMENT:
                        list = new ArrayList<>();
                        break;
                    case XmlResourceParser.START_TAG:
                        if ("p".equals(tag)) {
                            province = new Province();
                            cities = new ArrayList<>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                //获得属性的名和值
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("p_id".equals(name)) {
                                    province.setId(value);
                                }
                            }
                        }
                        if ("pn".equals(tag)) {//省名字
                            province.setName(parser.nextText());
                        }
                        if ("c".equals(tag)) {//城市
                            city = new City();
                            districts = new ArrayList<>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("c_id".equals(name)) {
                                    city.setId(value);
                                }
                            }
                        }
                        if ("cn".equals(tag)) {
                            city.setName(parser.nextText());
                        }
                        if ("d".equals(tag)) {
                            district = new District();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("d_id".equals(name)) {
                                    district.setId(value);
                                }
                            }
                            district.setName(parser.nextText());
                            districts.add(district);
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        if ("c".equals(tag)) {
                            city.setDistricts(districts);
                            cities.add(city);
                        }
                        if ("p".equals(tag)) {
                            province.setCitys(cities);
                            list.add(province);
                        }
                        break;
                    default:
                        break;
                }
                type = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    private void findViews() {
        etParentName = findViewById(R.id.m_et_parent_name);
        rbGenderMale = findViewById(R.id.m_rb_parent_gender_male);
        rbGenderFemale = findViewById(R.id.m_rb_parent_gender_female);
        etParentAge = findViewById(R.id.m_et_parent_age);
        spProvince = findViewById(R.id.m_sp_location_province);
        spCity = findViewById(R.id.m_sp_location_city);
        spArea = findViewById(R.id.m_sp_location_area);
        etDetailAddress = findViewById(R.id.m_et_detail_address);
        btnSetInfo = findViewById(R.id.m_btn_set_info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
