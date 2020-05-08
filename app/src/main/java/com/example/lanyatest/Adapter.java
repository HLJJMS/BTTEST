package com.example.lanyatest;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.clj.fastble.data.BleDevice;

public class Adapter extends BaseQuickAdapter<BleDevice, BaseViewHolder> {
    public Adapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BleDevice item) {
        helper.setText(R.id.txt,item.getName());
        helper.setText(R.id.mac,item.getMac());
    }
}
