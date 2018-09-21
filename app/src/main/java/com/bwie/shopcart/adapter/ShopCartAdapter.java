package com.bwie.shopcart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bwie.shopcart.R;
import com.bwie.shopcart.activity.MainActivity;
import com.bwie.shopcart.bean.ShopCartBean;
import com.bwie.shopcart.event.OnResfreshListener;
import com.bwie.shopcart.holder.ShopCartHolder;

import java.util.List;

/**
 * shop cardAdapter
 *
 * @author zhaoliang
 * @version 1.0
 * @create 2018/9/17
 */
public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartHolder> {

    private Context context;
    private List<ShopCartBean.CartlistBean> data;

    public ShopCartAdapter(Context context, List<ShopCartBean.CartlistBean> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ShopCartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopCartHolder(LayoutInflater.from(context).inflate(R.layout.shop_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ShopCartHolder holder, final int position) {
        /* 商品图片 */
        Glide.with(context).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1537181886761&di=576079fc797f5c11e552783c84a49b6c&imgtype=0&src=http%3A%2F%2Fpic22.nipic.com%2F20120702%2F2129594_171228709393_2.jpg").into(holder.ivShopCartClothPic);
        /* 商品的基本信息 */
        holder.tvShopCartClothColor.setText("颜色：" + data.get(position).getColor());
        holder.tvShopCartClothSize.setText("尺寸：" + data.get(position).getSize());
        holder.tvShopCartClothName.setText(data.get(position).getProductName());
        holder.tvShopCartShopName.setText(data.get(position).getShopName());
        holder.tvShopCartClothPrice.setText("¥" + data.get(position).getPrice());
        holder.etShopCartClothNum.setText(data.get(position).getCount() + "");

        /* 显示前面的选中状态 */
        if (data.get(position).getIsSelect()) {
            holder.ivShopCartClothSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_selected));
        } else {
            holder.ivShopCartClothSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_unselected));
        }

        if (data.get(position).getIsShopSelect()) {
            holder.ivShopCartShopSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_selected));
        } else {
            holder.ivShopCartShopSel.setImageDrawable(context.getResources().getDrawable(R.drawable.shopcart_unselected));
        }

        /* 判断是否显示商铺 */
        if (position > 0) {
            /* 判断是否是同一个商铺的商品 */
            if (data.get(position).getShopId() == data.get(position - 1).getShopId()) {
                holder.llShopCartHeader.setVisibility(View.GONE);
            } else {
                holder.llShopCartHeader.setVisibility(View.VISIBLE);
            }
        } else {
            holder.llShopCartHeader.setVisibility(View.VISIBLE);
        }

        /* 判断是否全选并计算 */
        if (mOnResfreshListener != null) {
            boolean isSelect = false;
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).getIsSelect()) {
                    isSelect = false;
                    break;
                } else {
                    isSelect = true;
                }
            }
            mOnResfreshListener.onResfresh(isSelect);
        }

        /* 商品数量加 */
        holder.ivShopCartClothAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.get(position).setCount(data.get(position).getCount() + 1);
                notifyDataSetChanged();
            }
        });

        /* 商品数量减 */
        holder.ivShopCartClothMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).getCount() > 1) {
                    data.get(position).setCount(data.get(position).getCount() - 1);
                    notifyDataSetChanged();
                }
            }
        });

        /* 删除操作 */
        holder.ivShopCartClothDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                //重新排序，标记所有商品不同商铺第一个的商品位置
                MainActivity.isSelectFirst(data);
                notifyDataSetChanged();
            }
        });

        /* 单个商品 选中状态 */
        holder.ivShopCartClothSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.get(position).setSelect(!data.get(position).getIsSelect());
                //通过循环找出不同商铺的第一个商品的位置
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isFirst()) {
                        //遍历去找出同一家商铺的所有商品的勾选情况
                        for (int j = 0; j < data.size(); j++) {
                            //如果是同一家商铺的商品，并且其中一个商品是未选中，那么商铺的全选勾选取消
                            if (data.get(j).getShopId() == data.get(i).getShopId() && !data.get(j).getIsSelect()) {
                                data.get(i).setShopSelect(false);
                                break;
                            } else {
                                //如果是同一家商铺的商品，并且所有商品是选中，那么商铺的选中全选勾选
                                data.get(i).setShopSelect(true);
                            }
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        /* 商铺选中状态 */
        holder.ivShopCartShopSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).isFirst()) {
                    // 商铺选中状态执反
                    data.get(position).setShopSelect(!data.get(position).getIsShopSelect());
                    // 改变商品的选中状态和商铺一样
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getShopId() == data.get(position).getShopId()) {
                            data.get(i).setSelect(data.get(position).getIsShopSelect());
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    // 刷新的接口
    private OnResfreshListener mOnResfreshListener;

    public void setOnResfreshListener(OnResfreshListener mOnResfreshListener) {
        this.mOnResfreshListener = mOnResfreshListener;
    }
}
