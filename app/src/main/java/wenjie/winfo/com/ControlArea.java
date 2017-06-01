package wenjie.winfo.com;

import java.io.Serializable;
import java.util.List;

import wenjie.winfo.com.widget.treeview.TreeNodeChecked;
import wenjie.winfo.com.widget.treeview.TreeNodeId;
import wenjie.winfo.com.widget.treeview.TreeNodeLabel;
import wenjie.winfo.com.widget.treeview.TreeNodePid;

/**
 * @项目名: gdmsaec-app
 * @包名: com.winfo.gdmsaec.app.domain.controlarea
 * @类名: ControlArea
 * @创建者: wenjie
 * @创建时间: 2015-11-18	下午1:22:01
 * @描述: 管控区域列表模型
 * @svn版本: $Rev: 1768 $
 * @更新人: $Author: wenjie $
 * @更新时间: $Date: 2016-03-22 16:42:54 +0800 (Tue, 22 Mar 2016) $
 * @更新描述: TODO
 */
public class ControlArea implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TreeNodeId
    private String id;

    /**
     * 父节点的名称
     */
    @TreeNodePid
    private String pId;

    /**
     * 显示的名称
     */
    @TreeNodeLabel
    private String text;

    /**
     * 是否被选中
     */
    @TreeNodeChecked
    private boolean isChecked;

    private List<ControlArea> children;

    public List<ControlArea> getChildren() {
        return children;
    }

    public void setChildren(List<ControlArea> children) {
        this.children = children;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
