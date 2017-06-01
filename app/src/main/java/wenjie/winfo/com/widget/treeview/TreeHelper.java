package wenjie.winfo.com.widget.treeview;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import wenjie.winfo.com.R;

/**
 * 
 * @author 00
 */
public class TreeHelper
{
	/**
	 * 传入我们的普通bean，转化为我们排序后的Node
	 * 
	 * @param datas
	 * @param defaultExpandLevel
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> List<Node> getSortedNodes(List<T> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException

	{
		List<Node> result = new ArrayList<Node>();
		// 将用户数据转化为List<Node>
		
		
		List<Node> nodes = convetData2Node(datas);
		// 拿到根节点
		List<Node> rootNodes = getRootNodes(nodes);
		// 排序以及设置Node间关系
		for (Node node : rootNodes)
		{
			addNode(result, node, defaultExpandLevel, 1);
		}
		return result;
	}

	/**
	 * 过滤出所有可见的Node
	 * 
	 * @param nodes
	 * @return
	 */
	public static List<Node> filterVisibleNode(List<Node> nodes)
	{
		List<Node> result = new ArrayList<Node>();

		for (Node node : nodes)
		{
			// 如果为跟节点，或者上层目录为展开状态
			if (node.isRoot() || node.isParentExpand())
			{
				setNodeIcon(node);
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * 将我们的数据转化为树的节点 
	 * 我们的数据有isChecked是否被选中的属性 来回显用户之前的选择，所以在这里我们吧 数据转转成node节点数据  带上手否被选中isChecked这样的话 
	 * 我们只需要专心操作我们的数据就好了  不用再管node，node和我们的数据是关联起来的，数据变化node也会变化 
	 * 所以我们加上TreeNodeChecked注解 同id，pid和label一样 利用注解和反射将数据的isChecked的值赋值给node 
	 * @param datas
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static <T> List<Node> convetData2Node(List<T> datas)
			throws IllegalArgumentException, IllegalAccessException

	{
		List<Node> nodes = new ArrayList<Node>();
		Node node = null;

		for (T t : datas)
		{
			String id = null;
			String pId = null;
			boolean isChecked = false;
			String label = null;
			
			Class<? extends Object> clazz = t.getClass();
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field f : declaredFields)
			{
				if (f.getAnnotation(TreeNodeId.class) != null)
				{
					f.setAccessible(true);
					id = (String) f.get(t);
				}
				if (f.getAnnotation(TreeNodePid.class) != null)
				{
					f.setAccessible(true);
					pId = (String) f.get(t);
				}
				if (f.getAnnotation(TreeNodeLabel.class) != null)
				{
					f.setAccessible(true);
					label = (String) f.get(t);
				}
				if(f.getAnnotation(TreeNodeChecked.class) != null){
					f.setAccessible(true);
					isChecked = f.getBoolean(t);
				}
				if (id != null && pId != null && label != null)
				{
					break;
				}
			}
			node = new Node(id, pId, label,isChecked);
			nodes.add(node);
		}

		/**
		 * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
		 */
		for (int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			for (int j = i + 1; j < nodes.size(); j++)
			{
				Node m = nodes.get(j);
				if (m.getpId().equals( n.getId()))
				{
					n.getChildren().add(m);
					m.setParent(n);
				} else if (m.getId().equals(n.getpId()))
				{
					m.getChildren().add(n);
					n.setParent(m);
				}
			}
		}

		// 设置图片
		for (Node n : nodes)
		{
			setNodeIcon(n);
		}
		return nodes;
	}

	private static List<Node> getRootNodes(List<Node> nodes)
	{
		List<Node> root = new ArrayList<Node>();
		for (Node node : nodes)
		{
			if (node.isRoot())
				root.add(node);
		}
		return root;
	}

	/**
	 * 把一个节点上的所有的内容都挂上去
	 */
	private static void addNode(List<Node> nodes, Node node,
			int defaultExpandLeval, int currentLevel)
	{

		nodes.add(node);
		if (defaultExpandLeval >= currentLevel)
		{
			node.setExpand(true);
		}

		if (node.isLeaf())
			return;
		for (int i = 0; i < node.getChildren().size(); i++)
		{
			addNode(nodes, node.getChildren().get(i), defaultExpandLeval,
					currentLevel + 1);
		}
	}

	/**
	 * 设置节点的图标
	 * 
	 * @param node
	 */
	private static void setNodeIcon(Node node)
	{
		if (node.getLevel() ==0 && node.isExpand())
		{
			node.setIcon(R.mipmap.control_botom);
		} else if (node.getLevel() ==0 && !node.isExpand())
		{
			node.setIcon(R.mipmap.control_right);
		}
		else if(node.getLevel() ==1 && node.isExpand()){
			node.setIcon(R.mipmap.node_ec);
		}else if(node.getLevel() ==1 && !node.isExpand()){
			node.setIcon(R.mipmap.node_ex);
		}
		else
			node.setIcon(R.mipmap.control_dec);
	}

}
