
//通过将hashCode值右移16位，然后与原值异或的操作可以保证高位和低位对均匀分布都能起到一定作用
static final int hash(Object key) {
	int h;
	return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
/**
 * Associates the specified value with the specified key in this map.
 * If the map previously contained a mapping for the key, the old
 * value is replaced.
 *
 * @param key key with which the specified value is to be associated
 * @param value value to be associated with the specified key
 * @return the previous value associated with {@code key}, or
 *         {@code null} if there was no mapping for {@code key}.
 *         (A {@code null} return can also indicate that the map
 *         previously associated {@code null} with {@code key}.)
 */
public V put(K key, V value) {
	return putVal(hash(key), key, value, false, true);
}

/**
 * Implements Map.put and related methods.
 *
 * @param hash hash for key
 * @param key the key
 * @param value the value to put
 * @param onlyIfAbsent if true, don't change existing value
 * @param evict if false, the table is in creation mode.  如果evict是false的话，则处于修改模式
 * @return previous value, or null if none
 */
//真正的添加元素的代码
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,boolean evict) {
	Node<K,V>[] tab; Node<K,V> p; int n, i; //初始变量
	if ((tab = table) == null || (n = tab.length) == 0)  //如果table为空的话，则直接resize()扩容，也就是hashMap是在put阶段才真正new数组对象
		n = (tab = resize()).length;               // n 为table的长度
	if ((p = tab[i = (n - 1) & hash]) == null)
		tab[i] = newNode(hash, key, value, null);   //如果说新的键值对检查到的位置为null的话，直接放到table的first位置
	else {                							 //否则的话说明当前的i的位置已经有Node了
		Node<K,V> e; K k;                                                           //此时p指向first Node
		if (p.hash == hash &&((k = p.key) == key || (key != null && key.equals(k))))   //始终对第一个先判断 key 的 hashCode相同
			e = p;
		else if (p instanceof TreeNode)                                                //如果此时first已经是树了，则调用树的添加节点                                
			e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
		else {                                                                          //既不是第一个，也不是红黑树的头的话就顺序遍历
			for (int binCount = 0; ; ++binCount) {    //计数循环 注意是++binCount
				if ((e = p.next) == null) {
					p.next = newNode(hash, key, value, null);     //已经找到了末尾，直接添加在末尾
					if (binCount >= TREEIFY_THRESHOLD - 1) // 默认 8 - 1 = 7 再加一个就是8 
						treeifyBin(tab, hash);        //尝试变化为树型
					break;
				}
				if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))  //找到了目标节点，跳出
					break;
				p = e;
			}
		}
		if (e != null) { // existing mapping for key          尾部插入的话，e = p.next =null
			V oldValue = e.value;
			if (!onlyIfAbsent || oldValue == null)
				e.value = value;
			afterNodeAccess(e);
			return oldValue;
		}
	}
	++modCount;     //快速失败机制，修改的时候就要++modCount
	if (++size > threshold)           //注意put后进行size的判断，如果size+1 大于 负载因子*容量的话，就要重新扩容
		resize();
	afterNodeInsertion(evict);      //后续处理，为空  
	return null;
}

/**
 * Initializes or doubles table size.  If null, allocates in
 * accord with initial capacity target held in field threshold.
 * Otherwise, because we are using power-of-two expansion, the
 * elements from each bin must either stay at same index, or move
 * with a power of two offset in the new table.
 *
 * @return the table
 */
final Node<K,V>[] resize() {
	Node<K,V>[] oldTab = table;                       //扩容的初始操作
	int oldCap = (oldTab == null) ? 0 : oldTab.length;
	int oldThr = threshold;
	int newCap, newThr = 0;
	if (oldCap > 0) {                                //大于0的数组的扩容
		if (oldCap >= MAXIMUM_CAPACITY) {             //如果旧的长度已经最大，则保持最大直接返回
			threshold = Integer.MAX_VALUE;
			return oldTab;
		}
		else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY)   //新数组的大小为原来两倍，并且并不能超过最大值
			newThr = oldThr << 1; // double threshold
	}
	else if (oldThr > 0) // initial capacity was placed in threshold     //如果旧的大小是0
		newCap = oldThr;
	else {               // zero initial threshold signifies using defaults   初始化参数
		newCap = DEFAULT_INITIAL_CAPACITY;
		newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
	}
	if (newThr == 0) {
		float ft = (float)newCap * loadFactor;
		newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ? (int)ft : Integer.MAX_VALUE);
	}
	threshold = newThr;
	@SuppressWarnings({"rawtypes","unchecked"})
	Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];    //生成新的数组
	table = newTab;
	if (oldTab != null) {
		for (int j = 0; j < oldCap; ++j) {
			Node<K,V> e;
			if ((e = oldTab[j]) != null) {
				oldTab[j] = null;
				if (e.next == null)
					newTab[e.hash & (newCap - 1)] = e;
				else if (e instanceof TreeNode)
					((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
				else { // preserve order
					Node<K,V> loHead = null, loTail = null;
					Node<K,V> hiHead = null, hiTail = null;
					Node<K,V> next;
					do {
						next = e.next;
						if ((e.hash & oldCap) == 0) {
							if (loTail == null)
								loHead = e;
							else
								loTail.next = e;
							loTail = e;
						}
						else {
							if (hiTail == null)
								hiHead = e;
							else
								hiTail.next = e;
							hiTail = e;
						}
					} while ((e = next) != null);
					if (loTail != null) {
						loTail.next = null;
						newTab[j] = loHead;
					}
					if (hiTail != null) {
						hiTail.next = null;
						newTab[j + oldCap] = hiHead;
					}
				}
			}
		}
	}
	return newTab;
}

/**
 * Replaces all linked nodes in bin at index for given hash unless
 * table is too small, in which case resizes instead.
 */
 //当数组太小的时候就不会将链变为树，而会选择扩容
final void treeifyBin(Node<K,V>[] tab, int hash) {
	int n, index; Node<K,V> e;
	if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)    //MIN_TREEIFY_CAPACITY 最小的变为树的数组长度，默认为64
		resize();
	else if ((e = tab[index = (n - 1) & hash]) != null) {      //否则定位到需要变为红黑树的index
		TreeNode<K,V> hd = null, tl = null;
		do {
			TreeNode<K,V> p = replacementTreeNode(e, null);
			if (tl == null)
				hd = p;
			else {
				p.prev = tl;
				tl.next = p;
			}
			tl = p;
		} while ((e = e.next) != null);
		if ((tab[index] = hd) != null)
			hd.treeify(tab);
	}
}

public V remove(Object key) {    //移除
	Node<K,V> e;
	return (e = removeNode(hash(key), key, null, false, true)) == null ?null : e.value;   //调用removeNode方法移除节点
}

/**
 * Implements Map.remove and related methods.
 *
 * @param hash hash for key
 * @param key the key
 * @param value the value to match if matchValue, else ignored
 * @param matchValue if true only remove if value is equal
 * @param movable if false do not move other nodes while removing
 * @return the node, or null if none
 */
final Node<K,V> removeNode(int hash, Object key, Object value,boolean matchValue, boolean movable) {
	Node<K,V>[] tab;
	Node<K,V> p;
	int n, index;
	if ((tab = table) != null && (n = tab.length) > 0 &&(p = tab[index = (n - 1) & hash]) != null) {  //判断数组中的头节点是否为空
		Node<K,V> node = null, e;
		K k;
		V v;
		if (p.hash == hash &&((k = p.key) == key || (key != null && key.equals(k))))  //判断头结点是不是要移除的元素
			node = p;
		else if ((e = p.next) != null) {     //e为第二个节点
			if (p instanceof TreeNode)           //p如果为树的根，则进入树删除
				node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
			else {                           //否则遍历链表，找到接下来的满足key的节点
				do {
					if (e.hash == hash &&((k = e.key) == key ||(key != null && key.equals(k)))) {
						node = e;
						break;
					}
					p = e;
				} while ((e = e.next) != null);
			}
		}
		//判断是否找到Node
		if (node != null && (!matchValue || (v = node.value) == value ||(value != null && value.equals(v)))) {
			if (node instanceof TreeNode)
				((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);  //在树中就移除树中的
			else if (node == p)
				tab[index] = node.next;    //否则如果是链表头就上移一下链表
			else
				p.next = node.next;   //否则就直接移除
			++modCount;
			--size;
			afterNodeRemoval(node);
			return node;
		}
	}
	return null;
}