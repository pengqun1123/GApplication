package com.example.administrator.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.TG.library.utils.LogUtils;
import com.example.administrator.gapplication.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created By pq
 * on 2019/3/21
 */
public class HostTemplAdapter extends RecyclerView.Adapter<HostTemplAdapter.ConsoleHolder> {
    private List<String> tipLists;
    private Context mContext;
    //    private int mFlag;
    private HostTemplAdapter.ItemClick itemClick;
    private String datID;

    public HostTemplAdapter(Context context) {
        this.mContext = context;
        tipLists = new ArrayList<>();
//        mFlag = flag;
    }

    public void setItemClick(HostTemplAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    //位置在哪

    public List<String> getData() {
        return tipLists;
    }

    public void addData(String tipStr) {
        if (!TextUtils.isEmpty(tipStr)) {
            tipLists.add(tipStr);
            notifyDataSetChanged();
        }
    }

    public void addData(List<String> newList) {
        if (newList != null && newList.size() > 0) {
            tipLists.addAll(newList);
            notifyDataSetChanged();
            newList.clear();
            Log.d("===TAG===", "--- 添加数据  ");
        }
    }

    public void removeData(String data) {
        tipLists.remove(data);
        notifyDataSetChanged();
    }

    public void clearData() {
//        if (tipLists.size() > 0) {
        tipLists.clear();
        notifyDataSetChanged();
//        }
    }

    @NonNull
    @Override
    public HostTemplAdapter.ConsoleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HostTemplAdapter.ConsoleHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.console_tip_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HostTemplAdapter.ConsoleHolder holder, final int position) {
//        if (mFlag == 1) {
        holder.consoleClearIcon.setVisibility(View.VISIBLE);
//        }
        int itemCount = getItemCount();
        LogUtils.d("---   数据的数量 :" + itemCount);
        holder.consoleTipTv.setText(tipLists.get(position));
        holder.consoleTipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择文件
                if (itemClick != null) {
                    //返回dat文件的名字
                    datID = tipLists.get(position);
                    itemClick.hostItemSelectFile(datID);
                }
            }
        });
        holder.consoleClearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择文件
                if (itemClick != null) {
                    //返回dat文件的名字
                    datID = tipLists.get(position);
                    itemClick.hostDelTempl(datID);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tipLists.size();
    }

    static class ConsoleHolder extends RecyclerView.ViewHolder {

        private final TextView consoleTipTv;
        private final ImageView consoleClearIcon;

        ConsoleHolder(View itemView) {
            super(itemView);
            consoleTipTv = itemView.findViewById(R.id.consoleTipTv);
            consoleClearIcon = itemView.findViewById(R.id.consoleClearIcon);
        }
    }

    public interface ItemClick {

        void hostItemSelectFile(String datFileName);

        void hostDelTempl(String datFileName);

    }

}
