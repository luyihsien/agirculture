package tw.edu.ntu.app;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> list;

    public ListViewAdapter(Context context, ArrayList<String> list ){
        this.context = context;
        this.list  =list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_view, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.tv_name);
            textView.setText(list.get(position));
        }

        return convertView;
    }

}
