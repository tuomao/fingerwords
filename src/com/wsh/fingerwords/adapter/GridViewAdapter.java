package com.wsh.fingerwords.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wsh.fingerwords.R;

public class GridViewAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<ResolveInfo> resolveInfos;
	private ViewHolder viewHolder;
	
	public GridViewAdapter(Context context,ArrayList<ResolveInfo> resolveInfos){
		mContext=context;
		inflater=(LayoutInflater) mContext
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resolveInfos=resolveInfos;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return resolveInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ImageView imageView;
		ResolveInfo resolveInfo;
		if(arg1==null){
			arg1=inflater.inflate(R.layout.app_item, null);
			viewHolder=new ViewHolder();
			viewHolder.imageView=(ImageView)arg1.findViewById(R.id.image);
			viewHolder.textView=(TextView)arg1.findViewById(R.id.text);
			arg1.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)arg1.getTag();
		}
		resolveInfo=resolveInfos.get(arg0);
		viewHolder.imageView.setImageDrawable(resolveInfo.loadIcon(mContext.getPackageManager()));
		viewHolder.textView.setText("QQ");
		return arg1;
	}
	class ViewHolder{
		public ImageView imageView;
		public TextView textView;
	}
}
