package com.bwie.shopcart.activity;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bwie.shopcart.R;
import com.bwie.shopcart.adapter.ShopCartAdapter;
import com.bwie.shopcart.bean.ShopCartBean;
import com.bwie.shopcart.event.OnResfreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车
         * 逻辑：
         * 展示逻辑 ：
         * ----》RecycleView展示
         * 处理逻辑
         * ----》
         * 1. 添加商品
         * 2. 减少商品
         * 3. 删除商品
         * 4. 商品选中状态改变
         * 5. 商铺选中状态改变
         * 6. 全选和全部选
         *
         * @author zhaoliang
        * @version 1.0
        * @create 2018/9/17
        */
public class MainActivity extends AppCompatActivity {


    private TextView tvShopCartSubmit, tvShopCartSelect, tvShopCartTotalNum;

    private RecyclerView rlvShopCart, rlvHotProducts;
    private ShopCartAdapter mShopCartAdapter;
    private LinearLayout llPay;
    private RelativeLayout rlHaveProduct;
    private List<ShopCartBean.CartlistBean> mAllOrderList = new ArrayList<>();
    private ArrayList<ShopCartBean.CartlistBean> mGoPayList = new ArrayList<>();
    private List<String> mHotProductsList = new ArrayList<>();
    private TextView tvShopCartTotalPrice;
    private int mCount, mPosition;
    private float mTotalPrice1;
    private boolean mSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShopCartSelect = findViewById(R.id.tv_shopcart_addselect);
        tvShopCartTotalPrice = findViewById(R.id.tv_shopcart_totalprice);
        tvShopCartTotalNum = findViewById(R.id.tv_shopcart_totalnum);

        rlHaveProduct = findViewById(R.id.rl_shopcart_have);
        rlvShopCart = findViewById(R.id.rlv_shopcart);
        llPay = findViewById(R.id.ll_pay);

        tvShopCartSubmit = findViewById(R.id.tv_shopcart_submit);

        rlvShopCart.setLayoutManager(new LinearLayoutManager(this));
        mShopCartAdapter = new ShopCartAdapter(this, mAllOrderList);
        rlvShopCart.setAdapter(mShopCartAdapter);

        //实时监控全选按钮
        mShopCartAdapter.setOnResfreshListener(new OnResfreshListener() {
            @Override
            public void onResfresh(boolean isSelect) {
                mSelect = isSelect;
                if (isSelect) {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_selected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                } else {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                }
                // 计算总价
                float mTotalPrice = 0;
                int mTotalNum = 0;
                mTotalPrice1 = 0;
                mGoPayList.clear();
                // 遍历所有商品 计算总价
                for (int i = 0; i < mAllOrderList.size(); i++)
                    if (mAllOrderList.get(i).getIsSelect()) {
                        mTotalPrice += Float.parseFloat(mAllOrderList.get(i).getPrice()) * mAllOrderList.get(i).getCount();
                        mTotalNum += 1;
                        mGoPayList.add(mAllOrderList.get(i));
                    }
                mTotalPrice1 = mTotalPrice;
                tvShopCartTotalPrice.setText("总价：" + mTotalPrice);
                tvShopCartTotalNum.setText("共" + mTotalNum + "件商品");
            }
        });

        //全选
        tvShopCartSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelect = !mSelect;
                if (mSelect) {
                    /* 全选 */
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_selected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                    for (int i = 0; i < mAllOrderList.size(); i++) {
                        mAllOrderList.get(i).setSelect(true);
                        mAllOrderList.get(i).setShopSelect(true);
                    }
                } else {
                    /* 全不选 */
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                    for (int i = 0; i < mAllOrderList.size(); i++) {
                        mAllOrderList.get(i).setSelect(false);
                        mAllOrderList.get(i).setShopSelect(false);
                    }
                }
                mShopCartAdapter.notifyDataSetChanged();

            }
        });

        initData();
        mShopCartAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化商店数据
     *
     * @author zhaoliang
     * @version 1.0
     * @create 2018/9/17
     */
    private void initData() {
        for (int i = 0; i < 3; i++) {
            ShopCartBean.CartlistBean sb = new ShopCartBean.CartlistBean();
            sb.setShopId(1);
            sb.setPrice("1999.0");
            sb.setDefaultPic("http://img2.3lian.com/2014/c7/25/d/40.jpg");
            sb.setProductName("小米MIX手机");
            sb.setShopName("小米旗舰店");
            sb.setColor("玫瑰金");
            sb.setCount(1);
            mAllOrderList.add(sb);
        }

        for (int i = 0; i < 2; i++) {
            ShopCartBean.CartlistBean sb = new ShopCartBean.CartlistBean();
            sb.setShopId(2);
            sb.setPrice("2999.0");
            sb.setDefaultPic("http://img2.3lian.com/2014/c7/25/d/40.jpg");
            sb.setProductName("三星note6");
            sb.setShopName("三星旗舰店");
            sb.setColor("玫瑰金");
            sb.setCount(1);
            mAllOrderList.add(sb);
        }
        isSelectFirst(mAllOrderList);
    }

    /**
     * 判断是否是商品的第一个商品 是第一个商品 需要显示商铺
     *
     * @author zhaoliang
     * @version 1.0
     * @create 2018/9/17
     */
    public static void isSelectFirst(List<ShopCartBean.CartlistBean> list) {
        // 1. 判断是否有商品 有商品 根据商品是否是第一个显示商铺
        if (list.size() > 0) {
            //头个商品一定属于它所在商铺的第一个位置，isFirst标记为1.
            list.get(0).setFirst(true);
            for (int i = 1; i < list.size(); i++) {
                //每个商品跟它前一个商品比较，如果Shopid相同isFirst则标记为2，
                //如果Shopid不同，isFirst标记为1.
                if (list.get(i).getShopId() == list.get(i - 1).getShopId()) {
                    list.get(i).setFirst(false);
                } else {
                    list.get(i).setFirst(true);
                }
            }
        }
    }
}

