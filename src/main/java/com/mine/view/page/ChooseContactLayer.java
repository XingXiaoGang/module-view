package com.mine.view.page;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ext.R;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.innso.utils.thread.ThreadPool;
import com.mine.view.page.layer.Layer;
import com.mine.view.page.layer.LayerManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by xingxiaogang on 2016/7/11.
 * 选择联系人用的
 */
public class ChooseContactLayer extends Layer implements View.OnClickListener, OnIconLoadListener<ContactUtils.ContactInfoBean>, View.OnKeyListener {

    private static final boolean DEBUG = UnreadConfig.DEBUG;
    private static final String TAG = "test.ChooseContactDialog";
    private ListView mListView;
    private List<ContactUtils.ContactInfoBean> mContacts;
    private ThreadPool mThreadPool;

    private HashMap<String, ImageView> mViewBundle = new HashMap<>();
    private HashMap<Long, Bitmap> mDrawableBundle = new HashMap<>();

    public ChooseContactLayer(LayerManager layerManager) {
        super(layerManager);
    }

    @Override
    protected WindowManager.LayoutParams onCreateLayoutParams() {
        WindowManager.LayoutParams lp = createDefaultLayoutParams();
        lp.windowAnimations = R.style.translate_from_right_anim_style;
        return lp;
    }

    @Override
    public View onCreateView(Intent intent) {
        View rootView = View.inflate(getContext(), R.layout.choose_contact_dialog, null);
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setFocusable(true);
        mListView.setOnKeyListener(this);
        rootView.setOnKeyListener(this);
        rootView.setOnClickListener(this);
        mContacts = ContactUtils.getContacts(getContext());

        //状态栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            View statusBarView = rootView.findViewById(R.id.view_fake_top_inset);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
            layoutParams.height = GeneralUtils.getStatusBarHeight(getContext());
        }

        mThreadPool = ThreadPool.getInstance();
        mListView.setAdapter(new Adapter(getContext()));

        if (DEBUG) {
            Log.d(TAG, "ChooseContactDialog: data:" + mContacts.size());
        }
        return rootView;
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public boolean onKeyEvent(int keycode) {
        return false;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onClick(View view) {
        if (DEBUG) {
            Log.d(TAG, "onClick: ");
        }
        ContactUtils.ContactInfoBean bean = null;
        if (view.getTag(R.id.tag_1) != null) {
            bean = (ContactUtils.ContactInfoBean) view.getTag(R.id.tag_1);
            //通知选择结果
            NotificationBus.getMessageBus().post(new NotificationEvent(NotificationEvents.CHOOSE_CONTACT_RES, bean));
            if (DEBUG) {
                Log.d(TAG, "onClick: ContactInfoBean:" + bean);
            }
        }
        finish();
    }

    @Override
    public void onValueLoaded(ContactUtils.ContactInfoBean info, Bitmap bitmap) {
        if (info != null && bitmap != null) {
            ImageView imageView = mViewBundle.get(info.phone);
            if (imageView != null) {
                mDrawableBundle.put(info.contactId, bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getContext(), "KEYCODE_BACK", Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        return false;
    }

    private class Adapter extends BaseAdapter {

        private Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (DEBUG) {
                Log.d(TAG, "getCount: data:" + mContacts.size());
            }
            return mContacts.size();
        }

        @Override
        public Object getItem(int i) {
            return mContacts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder = null;
            if (view != null) {
                holder = (Holder) view.getTag();
            } else {
                view = View.inflate(context, R.layout.choose_contact_list_item, null);
                holder = new Holder();
                holder.imageView = (ImageView) view.findViewById(R.id.contact_image);
                holder.textViewName = (TextView) view.findViewById(R.id.contact_displayname);
                holder.textViewPhone = (TextView) view.findViewById(R.id.contact_phone);
                view.setTag(holder);
            }
            //更新
            holder.update((ContactUtils.ContactInfoBean) getItem(i));
            view.setTag(R.id.tag_1, (ContactUtils.ContactInfoBean) getItem(i));
            view.setOnClickListener(ChooseContactLayer.this);
            return view;
        }
    }

    private class Holder {
        private TextView textViewName;
        private TextView textViewPhone;
        private ImageView imageView;

        public void update(ContactUtils.ContactInfoBean info) {
            if (info != null) {
                if (info.contactId > 0) {
                    Bitmap bitmap = mDrawableBundle.get(info.contactId);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        mViewBundle.put(info.phone, imageView);
                        mThreadPool.submit(new ContactIconLoader(info, ChooseContactLayer.this));
                    }
                    textViewName.setText(info.name);
                    textViewPhone.setText(info.phone);
                } else {
                    imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.contact_avatar));
                }
            } else {
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.contact_avatar));
                textViewName.setText("");
                textViewPhone.setText("");
            }
        }
    }

    private class ContactIconLoader implements Runnable {

        public ContactUtils.ContactInfoBean info;
        private OnIconLoadListener<ContactUtils.ContactInfoBean> listener;

        public ContactIconLoader(ContactUtils.ContactInfoBean info, OnIconLoadListener listener) {
            this.info = info;
            this.listener = listener;
        }

        @Override
        public void run() {
            if (listener != null) {
                Bitmap bitmap = ContactUtils.getContactPhoto(getContext(), info.contactId);
                listener.onValueLoaded(info, bitmap);
            }
        }

    }
}
