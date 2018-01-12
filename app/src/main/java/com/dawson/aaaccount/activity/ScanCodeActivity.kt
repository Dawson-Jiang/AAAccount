package com.dawson.aaaccount.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast

import com.dawson.aaaccount.R
import com.dawson.qrlibrary.CaptureActivity

class ScanCodeActivity : CaptureActivity() {
    private var lastScanResult = ""
    //    protected ImageView ivBack;
    //    protected TextView tvTitle;

    //    @Override
    //    public void setContentView(int layoutResID) {
    //        super.setContentView(R.layout.activity_qr_scan);
    //    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        initTitle();
    }
    //
    //    private void initTitle() {
    //        findViewById(tvLeft).setVisibility(View.GONE);
    //        ivBack = (ImageView) findViewById(R.id.ivBack);
    //        findViewById(tvRight).setVisibility(View.GONE);
    //        findViewById(ivRight).setVisibility(View.GONE);
    //        tvTitle = (TextView) findViewById(R.id.tvTitle);
    //
    //        ivBack = (ImageView) findViewById(R.id.ivBack);
    //        tvTitle = (TextView) findViewById(R.id.tvTitle);
    //        ivBack.setOnClickListener(view -> finish());
    //        tvTitle.setText("扫描二维码");
    //    }

    override fun handleDecode(result: String) {
        super.handleDecode(result)
        if (TextUtils.isEmpty(result)) {
            handler.sendEmptyMessage(R.id.restart_preview)
            return
        }
        val res: String
        if (result.startsWith("familyId")) {
            res = result.substring(result.indexOf(":") + 1)
        } else {
            if (lastScanResult != result)
                Toast.makeText(this, "不支持的格式", Toast.LENGTH_SHORT).show()
            lastScanResult = result
            handler.sendEmptyMessage(R.id.restart_preview)
            return
        }
        playBeepSoundAndVibrate()
        val retIntent = Intent()
        retIntent.putExtra(CaptureActivity.RET_KEY, res)
        setResult(RESULT_OK, retIntent)
        finish()
    }
}
