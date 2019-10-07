static class Entry<K,V> extends HashMap.Node<K,V> {
	Entry<K,V> before, after;                      //内部的Entry类继承了HashMap的Node内部类
	Entry(int hash, K key, V value, Node<K,V> next) {
		super(hash, key, value, next);
	}
}

//
//构造函数
//
public LinkedHashMap(int initialCapacity, float loadFactor) {
	super(initialCapacity, loadFactor);
	accessOrder = false;
}

public LinkedHashMap(int initialCapacity) {
	super(initialCapacity);
	accessOrder = false;
}

public LinkedHashMap() {
	super();
	accessOrder = false;
}

public LinkedHashMap(Map<? extends K, ? extends V> m) {
	super();
	accessOrder = false;
	putMapEntries(m, false);
}

public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {    //accessOrder设置为true的时候，每次访问结束的时候会将这个元素放在数组的尾部
	super(initialCapacity, loadFactor);
	this.accessOrder = accessOrder;
}

//
//遍历是否包含某元素
//
public boolean containsValue(Object value) {
	for (LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after) {   //从头向尾遍历
		V v = e.value;
		if (v == value || (value != null && value.equals(v)))
			return true;
	}
	return false;
}


//获取时间为O(1)
public V get(Object key) {
	Node<K,V> e;
	if ((e = getNode(hash(key), key)) == null)
		return null;
	if (accessOrder)
		afterNodeAccess(e);
	return e.value;
}


//子类重写父类的方法newNode，put方法直接使用父类HashMap的
//newNode()方法子类重写
//重写增加了链表的尾添方法linkNodeLast()
Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
	LinkedHashMap.Entry<K,V> p =
		new LinkedHashMap.Entry<>(hash, key, value, e);
	linkNodeLast(p);
	return p;
}
	
private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
        LinkedHashMap.Entry<K,V> last = tail;
        tail = p;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
    }