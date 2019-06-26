package com.sd.tgfinger.adapters;

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


import com.sd.tgfinger.gapplication.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created By pq
 * on 2019/3/5
 */
public class ConsoleTipAdapter extends RecyclerView.Adapter<ConsoleTipAdapter.ConsoleHolder> {

    private List<String> tipLists;
    private Context mContext;
//    private int mFlag;
    private ItemClick itemClick;
    private String datID;

    public ConsoleTipAdapter(Context context/*, int flag*/) {
        this.mContext = context;
        tipLists = new ArrayList<>();
//        mFlag = flag;
    }

    public void setItemClick(ItemClick itemClick) {
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

    public void clearData() {
//        if (tipLists.size() > 0) {
            tipLists.clear();
            notifyDataSetChanged();
//        }
    }

    @NonNull
    @Override
    public ConsoleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConsoleHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.console_tip_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConsoleHolder holder, final int position) {
//        if (mFlag == 1) {
            holder.consoleClearIcon.setVisibility(View.GONE);
//        }
//        int itemCount = getItemCount();
//        LogUtils.d("---   数据的数量 :"+itemCount);
        holder.consoleTipTv.setText(tipLists.get(position));
//        holder.consoleTipTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mFlag == 1) {
//                    //选择文件
//                    if (itemClick != null) {
//                        //返回dat文件的名字
//                        datID = tipLists.get(position);
//                        itemClick.itemSelectFile(datID);
//                        Toast.makeText(mContext, "DAtIdL" + datID + "  ---pos:"
//                                + position, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
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

        void itemSelectFile(String datFileName);

    }

}
