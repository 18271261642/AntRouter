package com.jkcq.antrouter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jkcq.antrouter.R;
import com.jkcq.antrouter.bean.ClubBean;

/*
 *
 *
 * @author mhj
 * Create at 2019/2/18 15:52
 */public class SpinnerClubListAdapter extends BaseAdapter {

     private Context context;
     private ClubBean listBean;
     private LayoutInflater inflater;
     public SpinnerClubListAdapter(Context context,ClubBean clubBean){
       this.context = context;
       this.listBean = clubBean;
       inflater = LayoutInflater.from(context);
     }

    @Override
    public int getCount() {
        return listBean.getList().size();
    }

    @Override
    public Object getItem(int i) {
        return listBean.getList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.clublist_item_layout, null);
            holder.nameTv = (TextView) convertView.findViewById(R.id.list_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTv.setText(listBean.getList().get(i).getName());
        return convertView;
    }

    class ViewHolder {
        public TextView nameTv;
    }

}
