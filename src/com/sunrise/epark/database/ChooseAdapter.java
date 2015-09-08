/**
 *ChooseAdapter.java
 *classes:com.sunrise.epark.database.ChooseAdapter
 *李敏 Create at 2015年7月29日 下午1:26:38
 */
package com.sunrise.epark.database;

import java.util.List;




import com.sunrise.epark.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 *com.sunrise.epark.database.ChooseAdapter
 * @author 李敏
 * create at 2015年7月29日 下午1:26:38
 * @param <Holder>
 * @param <Holder>
 */
import android.widget.TextView;

@SuppressLint({ "ResourceAsColor", "InflateParams" }) public class ChooseAdapter extends BaseAdapter {
	private List<String> l;
	private Context context;
	private int selectItem = -1;

	public ChooseAdapter(Context context, List<String> l) {
		this.context = context;
		this.l = l;
	}

	@Override
	public int getCount() {
		return l.size();
	}

	@Override
	public Object getItem(int position) {
		return l.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.choose_item,
					null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.name.setText(l.get(position).toString());
		if (position == selectItem) {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.click));
		} else {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.defult));
		}
		return convertView;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	class Holder {
		TextView name;
	}

}