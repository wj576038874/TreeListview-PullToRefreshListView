package wenjie.winfo.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import wenjie.winfo.com.widget.treeview.Node;
import wenjie.winfo.com.widget.treeview.TreeListViewAdapter;

public class MainActivity extends AppCompatActivity {


    /**
     * 显示数据的listview
     */
    private PullToRefreshListView controlListview;
    /**
     * 管控区域列表数据的适配器
     */
    private ControlAreaAdapter<ControlArea> treeListViewAdapter;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("数据加载中...");
        dialog.show();
        // 初始化listView
        controlListview = (PullToRefreshListView) findViewById(R.id.listview_control_area);
        controlListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//设置只能下拉刷新
        init();//初始化
        //设置listview的点击事件
        controlListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(MainActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后加载时间:" + label);
//                dialog.show();
                new RefereshAsyncTask().execute("newData.json");//刷新数据
            }
        });
        new DataAsyncTask().execute("data.json");
    }


    private void init() {
        ILoadingLayout startLabels = controlListview.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在加载...");// 刷新时
        startLabels.setReleaseLabel("放开刷新");// 下来达到一定距离时，显示的提示
    }

    /**
     * 从assert文件夹中读取json文件，然后转化为json对象
     *
     * @throws Exception
     */
    public JSONObject getJsonDataFromAssets(Context context, String jsonFileName) throws Exception {
        JSONObject mJsonObj;
        StringBuilder sb = new StringBuilder();
        InputStream is = context.getAssets().open(jsonFileName);
        int len;
        byte[] buf = new byte[1024];
        while ((len = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, len, "UTF-8"));
        }
        is.close();
        mJsonObj = new JSONObject(sb.toString());
        return mJsonObj;
    }


    /**
     * 模拟异步加载数据
     */
    private class DataAsyncTask extends AsyncTask<String, Integer, List<ControlArea>> {

        @Override
        protected List<ControlArea> doInBackground(String... params) {
            try {
                List<ControlArea> controlAreas = new ArrayList<>();
                String jsonFileName = params[0];
                JSONObject controlAreaJosn = getJsonDataFromAssets(MainActivity.this, jsonFileName);
                int result = controlAreaJosn.getInt("result");
                if (result == 1) {
                    JSONArray jsonArray = controlAreaJosn.getJSONArray("data");
                    //海事处
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hscModel = jsonArray.getJSONObject(i);
                        ControlArea controlArea = JsonUtil.jsonToBean(hscModel.toString(), ControlArea.class);
                        controlAreas.add(controlArea);

                        //海事处下面的类型
                        JSONArray qyJsonArray = hscModel.getJSONArray("children");
                        for (int j = 0; j < qyJsonArray.length(); j++) {
                            JSONObject qyModel = qyJsonArray.getJSONObject(j);
                            ControlArea controlArea2 = JsonUtil.jsonToBean(qyModel.toString(), ControlArea.class);
                            controlAreas.add(controlArea2);

                            //类型下面的管控区域
                            JSONArray jtJsonArray = qyModel.getJSONArray("children");
                            for (int k = 0; k < jtJsonArray.length(); k++) {
                                JSONObject jtModel = jtJsonArray.getJSONObject(k);
                                ControlArea controlArea3 = JsonUtil.jsonToBean(jtModel.toString(), ControlArea.class);
                                controlAreas.add(controlArea3);
                            }
                        }
                    }
                }
                return controlAreas;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ControlArea> controlAreas) {
            super.onPostExecute(controlAreas);
            if (controlAreas != null) {
                dialog.dismiss();
                try {
                    treeListViewAdapter = new ControlAreaAdapter<>(controlListview, MainActivity.this, controlAreas, 0);
                    controlListview.setAdapter(treeListViewAdapter);
                    //每个item的点击事件
                    treeListViewAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {

                        @Override
                        public void onClick(Node node, int position) {
                            if (node.isLeaf()) {
                                if (node.isChecked())//取消
                                {
                                    String id = node.getId();
                                    String lx = node.getParent().getName();
                                    node.setChecked(false);
                                    treeListViewAdapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, "取消选中----id=" + id + "---lx=" + lx, Toast.LENGTH_SHORT).show();
                                } else {//选中
                                    String id = node.getId();
                                    String lx = node.getParent().getName();
                                    node.setChecked(true);
                                    treeListViewAdapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, "选中----id=" + id + "---lx=" + lx, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    //每个item前面的图标的点击事件
                    treeListViewAdapter.setOnMessageClickListenner(new ControlAreaAdapter.OnMessageClickListenner() {

                        @Override
                        public void onClik(String id, String lx) {
                            Toast.makeText(MainActivity.this, "id=" + id + "---lx=" + lx, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 模拟异步刷新数据
     */
    private class RefereshAsyncTask extends AsyncTask<String, Integer, List<ControlArea>> {

        @Override
        protected List<ControlArea> doInBackground(String... params) {
            try {
                List<ControlArea> controlAreas = new ArrayList<>();
                String jsonFileName = params[0];
                JSONObject controlAreaJosn = getJsonDataFromAssets(MainActivity.this, jsonFileName);
                int result = controlAreaJosn.getInt("result");
                if (result == 1) {
                    JSONArray jsonArray = controlAreaJosn.getJSONArray("data");
                    //海事处
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hscModel = jsonArray.getJSONObject(i);
                        ControlArea controlArea = JsonUtil.jsonToBean(hscModel.toString(), ControlArea.class);
                        controlAreas.add(controlArea);

                        //海事处下面的类型
                        JSONArray qyJsonArray = hscModel.getJSONArray("children");
                        for (int j = 0; j < qyJsonArray.length(); j++) {
                            JSONObject qyModel = qyJsonArray.getJSONObject(j);
                            ControlArea controlArea2 = JsonUtil.jsonToBean(qyModel.toString(), ControlArea.class);
                            controlAreas.add(controlArea2);

                            //类型下面的管控区域
                            JSONArray jtJsonArray = qyModel.getJSONArray("children");
                            for (int k = 0; k < jtJsonArray.length(); k++) {
                                JSONObject jtModel = jtJsonArray.getJSONObject(k);
                                ControlArea controlArea3 = JsonUtil.jsonToBean(jtModel.toString(), ControlArea.class);
                                controlAreas.add(controlArea3);
                            }
                        }
                    }
                }
                return controlAreas;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ControlArea> controlAreas) {
            super.onPostExecute(controlAreas);
            if (controlAreas != null) {
                dialog.dismiss();
                try {
                    treeListViewAdapter.referesh(controlAreas);
                    controlListview.onRefreshComplete();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
