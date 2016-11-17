package com.logishub.mobile.launcher.v5.DATA;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.R;

public class SubMenuAdapter extends ArrayAdapter
{
    Context mContext;
    Integer[] mSubMenuImage;
    String[] mSubMenuName;
    String mMenuType;

    public SubMenuAdapter(Context context, Integer[] subMenuImages, String[] subMenuNames, String menuType)
    {
        super(context, 0);
        this.mContext = context;
        this.mSubMenuImage = subMenuImages;
        this.mSubMenuName = subMenuNames;
        this.mMenuType = menuType;
    }

    @Override
    public int getCount() {
        return mSubMenuName.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        ImageView ivSubMenu;
        TextView tvSubMenu;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        Holder holder=new Holder();

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        row = inflater.inflate(R.layout.grid_row, parent, false);

        holder.ivSubMenu=(ImageView) row.findViewById(R.id.iv_menu_image);
        holder.tvSubMenu=(TextView) row.findViewById(R.id.tv_menu_name);
        holder.ivSubMenu.setImageResource(mSubMenuImage[position]);
        holder.tvSubMenu.setText(mSubMenuName[position]);
        holder.ivSubMenu.setColorFilter(0xff777777);

        return row;
    }
}