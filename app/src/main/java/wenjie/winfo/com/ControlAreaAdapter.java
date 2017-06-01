package wenjie.winfo.com;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import wenjie.winfo.com.widget.treeview.Node;
import wenjie.winfo.com.widget.treeview.TreeListViewAdapter;

/**
 * 管控区域的列表适配器
 *
 * @param <T>
 * @author 00
 */
public class ControlAreaAdapter<T> extends TreeListViewAdapter<T> {

    private OnCheckBoxClickListener onCheckBoxClickListener;

    public interface OnCheckBoxClickListener {
        void onClik(String id, String lx, boolean isChecked);
    }

    public void setOnCheckBoxClickListener(OnCheckBoxClickListener onCheckBoxClickListener) {
        this.onCheckBoxClickListener = onCheckBoxClickListener;
    }

    private OnMessageClickListenner onMessageClickListenner;

    public interface OnMessageClickListenner {
        void onClik(String id, String lx);
    }

    public void setOnMessageClickListenner(OnMessageClickListenner onMessageClickListenner) {
        this.onMessageClickListenner = onMessageClickListenner;
    }

    public ControlAreaAdapter(PullToRefreshListView  mTree, Context context, List<T> datas,
                              int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
    }

    @Override
    public View getConvertView(final Node node, final int position, View convertView,
                               ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.control_area_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.treenode_icon);
            viewHolder.label = (TextView) convertView.findViewById(R.id.treenode_label);
            viewHolder.ivCheckBox = (ImageView) convertView.findViewById(R.id.id_control_icon);
            viewHolder.line = convertView.findViewById(R.id.control_line);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setVisibility(View.VISIBLE);
        viewHolder.icon.setImageResource(node.getIcon());

        if (node.isChecked() && node.isLeaf()) {
            viewHolder.ivCheckBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivCheckBox.setVisibility(View.GONE);
        }

        if(node.isLeaf()){
            viewHolder.line.setVisibility(View.VISIBLE);
        }else{
            viewHolder.line.setVisibility(View.GONE);
        }

        /**
         * 这里需要把viewHolder.checkBox 赋值给 CheckBox 不然内部类无法访问  因为内部类访问必须为final的 而viewHolder不能是final 所以 吧他复制出来给
         * 另外一个checkbox拿来使用
         * 并且 这里我们不能用checkbox的 （setOnCheckedChangeListener）选中状态改变的监听方法
         * 因为我们需要的是  在checkbox 点击为选中的时候 才调用接口查询数据，不然在lsitView中checkbox复用时，会一直调用setOnCheckedChangeListener
         * 这个事件，只要被选中了  就会执行请求，不符合要求
         */
//		final ImageView box = viewHolder.checkBox;
//		viewHolder.checkBox.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				boolean isChecked = box.isChecked();
////				node.setChecked(isChecked);
//				try {
//					String id = node.getId();
////					List<ControlArea> db_data = db.findAll(Selector.from(ControlArea.class).where(WhereBuilder.b("id", "=", id)));
////					ControlArea mControlArea = db_data.get(0);
//					//把已勾选的的选项保存在ControlArea中
////					mControlArea.setChecked(isChecked);
////					db.update(mControlArea, WhereBuilder.b("id", "=", id), "isChecked");
//					String lx = node.getParent().getName();
//					onCheckBoxClickListener.onClik(id ,lx,true);
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});

        if (node.isLeaf()) {
            viewHolder.icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String id = node.getId();
                    String lx = node.getParent().getName();
                    onMessageClickListenner.onClik(id, lx);
                }
            });
        } else {
            viewHolder.icon.setOnClickListener(null);
        }

//		viewHolder.checkBox.setChecked(node.isChecked());
        //onCheckBoxClickListener1.onClik(node.getId(),node.isChecked());
        if (node.isRoot()) {
            viewHolder.label.setTextSize(16);
        } else if (node.isLeaf()) {
            viewHolder.label.setTextSize(14);
        } else {
            viewHolder.label.setTextSize(15);
        }
        viewHolder.label.setText(node.getName());

        return convertView;
    }

    private class ViewHolder {
        public ImageView ivCheckBox;
        ImageView icon;
        TextView label;
        View line;
//		CheckBox checkBox;
    }

    public void referesh(List<T> datas) throws IllegalAccessException {
        this.datas = datas;
        referesh();
    }
}
