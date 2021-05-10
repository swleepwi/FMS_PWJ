package com.pwi3.fms_;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView TV_Header;
    Button btn_upload;
    Button btn_deleteAll;
    ListView LV_Country;
    SimpleAdapter ADAhere;
    EditText etRack;
    EditText etCarton;
    TextView tvLayCode;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    String userID;

    public int getLayer_cd() {
        return layer_cd;
    }

    public int getCntDBOK() {
        return cntDBOK;
    }

    public void setCntDBOK(int cntDBOK) {
        this.cntDBOK = cntDBOK;
    }

    int cntDBOK;

    public int getCntDBFAIL() {
        return cntDBFAIL;
    }

    public void setCntDBFAIL(int cntDBFAIL) {
        this.cntDBFAIL = cntDBFAIL;
    }

    public int getCntUpload() {
        return cntUpload;
    }

    public void setCntUpload(int cntUpload) {
        this.cntUpload = cntUpload;
    }

    int cntUpload;
    int cntDBFAIL;

    public void setLayer_cd(int layer_cd) {
        this.layer_cd = layer_cd;
    }

    public String getLayout_cd() {
        return layout_cd;
    }

    public void setLayout_cd(String layout_cd) {
        this.layout_cd = layout_cd;
    }

    public void setFlagERROR(int flagERROR) {
        this.flagERROR = flagERROR;
    }

    int flagERROR;
    String layout_cd;
    int layer_cd;
    Handler handler = new Handler();
    private final String url = "http://192.168.40.175:8081";
    private WebView webView;
    private boolean transfer = true;
    ProgressDialog customProgressDialog;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customProgressDialog = new ProgressDialog(MainActivity.this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.addJavascriptInterface(new AndroidBridge(), "android");
        webView.getSettings().setDomStorageEnabled(true);
        webView.setVisibility(View.VISIBLE);
        //웹뷰 ProgressDialog
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                customProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                customProgressDialog.dismiss();
            }
        });

        setCntDBFAIL(0);
        setCntDBOK(0);
        setCntUpload(0);
        TV_Header = (TextView) findViewById(R.id.TV_Header);
        LV_Country = (ListView) findViewById(R.id.LV_Country);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_deleteAll = (Button) findViewById(R.id.btn_deleteAll);
        tvLayCode = (TextView) findViewById(R.id.tvLayCode);

        etRack = (EditText) findViewById(R.id.etRack);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        etCarton = (EditText) findViewById(R.id.etCarton);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://192.168.40.175:8081/m/fms_home.php");
                webView.setVisibility(View.VISIBLE);
            }
        });

        LV_Country.setFocusable(false);
        btn_upload.setFocusable(false);
        btn_deleteAll.setFocusable(false);


        etCarton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //Carton 입력 시 16글자가 되면 자동 입력
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().getBytes().length >= 16) {
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if ((etCarton.getText().length() == 16) && (etRack.getText().length() == 6)) {
                        LV_Country.setAdapter(null);
                        saveInTxt(etRack.getText().toString(), etCarton.getText().toString(), getLayer_cd());
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        etCarton.setText("");
                        etCarton.requestFocus();
                    } else if (etRack.getText().length() < 6) {
                        etRack.setText("");
                        etRack.requestFocus();
                        Toast.makeText(MainActivity.this, "WRONG RACK NO", Toast.LENGTH_SHORT).show();
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                    } else if (etCarton.getText().length() < 16) {
                        etCarton.setText("");
                        etCarton.requestFocus();
                        Toast.makeText(MainActivity.this, "WRONG CARTON NO", Toast.LENGTH_SHORT).show();
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                    } else {
                        etCarton.setText("");
                        etCarton.requestFocus();
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }
        });

        setLayer_cd(1); //처음 토글버튼의 값
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
        //layer_cd 토글버튼
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, final int i) {
                switch (i) {
                    case R.id.radio1:
                        setLayer_cd(1);
                        break;
                    case R.id.radio2:
                        setLayer_cd(2);
                        break;
                    case R.id.radio3:
                        setLayer_cd(3);
                        break;
                }
            }
        });

        //////처음 화면 보일 때 DB도 같이 보여줌
        MyTable mMyTable = new MyTable(MainActivity.this);
        ArrayList<HashMap<String, String>> dataList = mMyTable.getData();
        Collections.reverse(dataList); //역순 재배치
        String[] fromwhere = {"COUNT_NO", "RACK_NO", "CARTON_BARCODE", "LAYER_NO", "MODIFY_DATE", "SUCCESS_CODE"};
        int[] viewswhere = {R.id.tvcountno, R.id.tvrackno, R.id.tvcartonno, R.id.tvlayerno, R.id.tvdate, R.id.tvsuccesscode};
        ADAhere = new SimpleAdapter(MainActivity.this, dataList, R.layout.listtemplate, fromwhere, viewswhere);
        LV_Country.setAdapter(ADAhere);
        ADAhere.notifyDataSetChanged();

        //Listview item 클릭 시
        LV_Country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) ADAhere.getItem(position);
                String ID = (String) obj.get("CARTON_BARCODE");
                Toast.makeText(MainActivity.this, ID, Toast.LENGTH_SHORT).show();

            }
        });


        //Delete 버튼
        btn_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
        setFlagERROR(0);//0=NOERROR
        //Upload 버튼
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadDialog();
            }
        });


    }//-----------------------------------------------------------------------------------End of onCreate-----------------------------------------------------------------------------

    //0=count /1=rack/ 2=ctn / 3=layer/ 4=date / 5=code
    private void saveInTxt(String rackNo, String cartonNo, int layerNo) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date dateNow = new Date(System.currentTimeMillis());
        String date = formatter.format(dateNow);
        MyTable mMyTable = new MyTable(this);
        if (mMyTable.checkDuplicate(rackNo, cartonNo, String.valueOf(layerNo)) == 0) {
            mMyTable.insert(rackNo, cartonNo, String.valueOf(layerNo), date, "-");
        } else mMyTable.updateTime(rackNo, cartonNo, String.valueOf(layerNo), date);
        ArrayList<HashMap<String, String>> dataList = mMyTable.getData();

        Collections.reverse(dataList); //역순 재배치
        String[] fromwhere = {"COUNT_NO", "RACK_NO", "CARTON_BARCODE", "LAYER_NO", "MODIFY_DATE", "SUCCESS_CODE"};
        int[] viewswhere = {R.id.tvcountno, R.id.tvrackno, R.id.tvcartonno, R.id.tvlayerno, R.id.tvdate, R.id.tvsuccesscode};
        ADAhere = new SimpleAdapter(MainActivity.this, dataList, R.layout.listtemplate, fromwhere, viewswhere);
        LV_Country.setAdapter(ADAhere);
        ADAhere.notifyDataSetChanged();
    }

    public void upload(ArrayList<HashMap<String, String>> dataList) {

        MyTable mMyTable = new MyTable(MainActivity.this);
        setCntUpload(0);
        setCntDBOK(0);
        setCntDBFAIL(0);
        for (int i = 0; i < dataList.size(); i++) {

            if (!dataList.get(i).get("SUCCESS_CODE").equals("SUCCEED")) {
                String layout = getLayout_cd();
                String rack = dataList.get(i).get("RACK_NO");
                String carton = dataList.get(i).get("CARTON_BARCODE");
                String layer = dataList.get(i).get("LAYER_NO");
                String id = getUserID();

                ThreadB b = new ThreadB(layout, rack, carton, layer, id);
                b.start();

                synchronized (b) {
                    try {
                        System.out.println("Waiting for b to complete...");
                        b.wait(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        b.result = 0;
                    }

                    if (b.result == 0) {
                        System.out.println(b.result + " ---------------------------------------------");
                        mMyTable.updateCode(rack, carton, layer, "FAILED");
                        error();
                    } else {
                        ok();
                        mMyTable.updateCode(rack, carton, layer, "SUCCEED");
                    }
                }

                setCntUpload(getCntUpload() + 1);

                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        if (getCntUpload() == 0) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "NOTHING TO SEND", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    showDatasendDialog();
                }
            });


        }
        customProgressDialog.dismiss();
    }

    //==========================================================================AndroidBridge===============================================================================
    private class AndroidBridge {
        @JavascriptInterface
        public void layout(String layoutcd) {
            handler.post(new Runnable() {
                public void run() {
                    switch (layoutcd) {
                        case "16449":
                            tvLayCode.setText("F1-1");
                            setLayout_cd("040101");
                            break;
                        case "16450":
                            tvLayCode.setText("F1-2");
                            setLayout_cd("040102");
                            break;
                        case "16513":
                            tvLayCode.setText("F3-1");
                            setLayout_cd("040201");
                            break;
                        case "16514":
                            tvLayCode.setText("F3-2");
                            setLayout_cd("040202");
                            break;
                        case "16769":
                            tvLayCode.setText("DPOC");
                            setLayout_cd("040601");
                            break;
                        case "16771":
                            tvLayCode.setText("F6");
                            setLayout_cd("040603");
                            break;
                        case "16772":
                            tvLayCode.setText("SF");
                            setLayout_cd("040604");
                            break;
                        case "16773":
                            tvLayCode.setText("XCARTON");
                            setLayout_cd("040605");
                            break;
                        case "16774":
                            tvLayCode.setText("XSABLON");
                            setLayout_cd("040606");
                            break;
                    }
                    etRack.requestFocus();
                }
            });
        }

        @JavascriptInterface
        public void RackToCarton() {
            handler.post(new Runnable() {
                public void run() {
                    webView.setVisibility(View.GONE);
                }
            });
        }

        @JavascriptInterface
        public void sendID(String userIDDB) {
            handler.post(new Runnable() {
                public void run() {
                    setUserID(userIDDB);
                }
            });
        }
    }

    //==========================================================================AndroidBridge===============================================================================
    //데이터가 잘 보내졌으면 OK, Fail이면 ERROR
    public void ok() {
        final TextView tvDBCNT = (TextView) findViewById(R.id.tvDBCNT);
        handler.post(new Runnable() {
            public void run() {
                setCntDBOK(getCntDBOK() + 1);
                setFlagERROR(0);
                tvDBCNT.setText(String.valueOf(getCntDBOK()));
            }
        });
    }

    public void error() {
        handler.post(new Runnable() {
            public void run() {

                Toast.makeText(MainActivity.this, "ERRORRRRRRRRRRRRR", Toast.LENGTH_SHORT).show();
                setCntDBFAIL(getCntDBFAIL() + 1);
            }
        });
    }

    //=========================================================================================================================================================
    //데이터가 다 보내진 뒤 결과를 보여주는 Dialog
    void showDatasendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total data sent");
        builder.setMessage("Total: " + getCntUpload() + "  HASIL: " + getCntDBOK() + " GAGAL: " + getCntDBFAIL());
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        etRack.setText("");
                        etCarton.setText("");
                        etRack.requestFocus();
                    }
                });
        builder.show();


        MyTable mMyTable2 = new MyTable(MainActivity.this);
        ArrayList<HashMap<String, String>> dataList2 = mMyTable2.getData();
        Collections.reverse(dataList2); //역순 재배치
        String[] fromwhere = {"COUNT_NO", "RACK_NO", "CARTON_BARCODE", "LAYER_NO", "MODIFY_DATE", "SUCCESS_CODE"};
        int[] viewswhere = {R.id.tvcountno, R.id.tvrackno, R.id.tvcartonno, R.id.tvlayerno, R.id.tvdate, R.id.tvsuccesscode};
        ADAhere = new SimpleAdapter(MainActivity.this, dataList2, R.layout.listtemplate, fromwhere, viewswhere);
        LV_Country.setAdapter(ADAhere);
        ADAhere.notifyDataSetChanged();
        setCntUpload(0);
        setCntDBOK(0);
        setCntDBFAIL(0);
    }

    //DELETE 버튼 클릭 시 나오는 Dialog
    void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DELETE");
        builder.setMessage("ARE YOU SURE DELETE ALL?");
        builder.setPositiveButton("DELETE ALL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MyTable mMyTable = new MyTable(MainActivity.this);
                        mMyTable.deleteAll();
                        ArrayList<HashMap<String, String>> dataList = mMyTable.getData();
                        Collections.reverse(dataList); //역순 재배치
                        String[] fromwhere = {"COUNT_NO", "RACK_NO", "CARTON_BARCODE", "LAYER_NO", "MODIFY_DATE", "SUCCESS_CODE"};
                        int[] viewswhere = {R.id.tvcountno, R.id.tvrackno, R.id.tvcartonno, R.id.tvlayerno, R.id.tvdate, R.id.tvsuccesscode};
                        ADAhere = new SimpleAdapter(MainActivity.this, dataList, R.layout.listtemplate, fromwhere, viewswhere);
                        LV_Country.setAdapter(ADAhere);
                        ADAhere.notifyDataSetChanged();
                        setCntUpload(0);
                    }
                });
        builder.setNegativeButton("DELETE SUCCEED ONLY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MyTable mMyTable = new MyTable(MainActivity.this);
                        mMyTable.deleteSUCCEED();
                        ArrayList<HashMap<String, String>> dataList = mMyTable.getData();
                        Collections.reverse(dataList); //역순 재배치
                        String[] fromwhere = {"COUNT_NO", "RACK_NO", "CARTON_BARCODE", "LAYER_NO", "MODIFY_DATE", "SUCCESS_CODE"};
                        int[] viewswhere = {R.id.tvcountno, R.id.tvrackno, R.id.tvcartonno, R.id.tvlayerno, R.id.tvdate, R.id.tvsuccesscode};
                        ADAhere = new SimpleAdapter(MainActivity.this, dataList, R.layout.listtemplate, fromwhere, viewswhere);
                        LV_Country.setAdapter(ADAhere);
                        ADAhere.notifyDataSetChanged();
                    }
                });
        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    //Upload 버튼 클릭 시 나오는 Dialog
    void showUploadDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("UPLOAD");
        builder.setMessage("UPLOAD TO DATABASE?");
        builder.setPositiveButton("UPLOAD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        customProgressDialog.show();
                        MyTable mMyTable = new MyTable(MainActivity.this);
                        ArrayList<HashMap<String, String>> dataList = mMyTable.getData();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                upload(dataList);
                            }
                        }).start();

                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }
}

class ThreadB extends Thread {
    int result;
    String layout;
    String rack;
    String carton;
    String layer;
    String id;

    public ThreadB(String layout_cd, String rackno, String carno, String layno, String userid) {
        layout = layout_cd;
        rack = rackno;
        carton = carno;
        layer = layno;
        id = userid;
    }

    @Override
    public void run() {
        synchronized (this) {
            uploadDB upload = new uploadDB();
            result = upload.query(layout, rack, carton, layer, id);
            notify();
        }
    }
}