private static final int DEFAULT_CAPACITY = 10;

/**
 * Shared empty array instance used for empty instances.
 */
private static final Object[] EMPTY_ELEMENTDATA = {};

/**
 * Shared empty array instance used for default sized empty instances. We
 * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
 * first element is added.
 */
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

/**
 * The array buffer into which the elements of the ArrayList are stored.
 * The capacity of the ArrayList is the length of this array buffer. Any
 * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
 * will be expanded to DEFAULT_CAPACITY when the first element is added.
 */
transient Object[] elementData; // non-private to simplify nested class access

/**
 * The size of the ArrayList (the number of elements it contains).
 *
 * @serial
 */
private int size;

/**
 * Constructs an empty list with the specified initial capacity.
 *
 * @param  initialCapacity  the initial capacity of the list
 * @throws IllegalArgumentException if the specified initial capacity
 *         is negative
 */
public ArrayList(int initialCapacity) {
	if (initialCapacity > 0) {     //>0的话直接生成initialCapacity大小的数组
		this.elementData = new Object[initialCapacity];
	} else if (initialCapacity == 0) {
		this.elementData = EMPTY_ELEMENTDATA;      //0的话生成空的数组
	} else {                                       //否则不合法
		throw new IllegalArgumentException("Illegal Capacity: "+
										   initialCapacity);
	}
}

/**
 * Constructs an empty list with an initial capacity of ten.
 */
public ArrayList() {
	this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;     //不传入值的话，为空数组
}

/**
 * Constructs a list containing the elements of the specified
 * collection, in the order they are returned by the collection's
 * iterator.
 *
 * @param c the collection whose elements are to be placed into this list
 * @throws NullPointerException if the specified collection is null
 */
public ArrayList(Collection<? extends E> c) {
	elementData = c.toArray();
	if ((size = elementData.length) != 0) {
		// defend against c.toArray (incorrectly) not returning Object[]
		// (see e.g. https://bugs.openjdk.java.net/browse/JDK-6260652)
		if (elementData.getClass() != Object[].class)
			elementData = Arrays.copyOf(elementData, size, Object[].class);
	} else {
		// replace with empty array.
		this.elementData = EMPTY_ELEMENTDATA;
	}
}
/**
 * Increases the capacity of this {@code ArrayList} instance, if
 * necessary, to ensure that it can hold at least the number of elements
 * specified by the minimum capacity argument.
 *
 * @param minCapacity the desired minimum capacity
 */
public void ensureCapacity(int minCapacity) {       //显式扩容，防止多次扩容影响效率
	if (minCapacity > elementData.length && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA && minCapacity <= DEFAULT_CAPACITY)) {
		modCount++;
		grow(minCapacity);
	}
}

/**
 * The maximum size of array to allocate (unless necessary).
 * Some VMs reserve some header words in an array.
 * Attempts to allocate larger arrays may result in
 * OutOfMemoryError: Requested array size exceeds VM limit
 */
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

/**
 * Increases the capacity to ensure that it can hold at least the
 * number of elements specified by the minimum capacity argument.
 *
 * @param minCapacity the desired minimum capacity
 * @throws OutOfMemoryError if minCapacity is less than zero
 */
private Object[] grow(int minCapacity) {        //扩容，借助Arrays.copyOf()
	return elementData = Arrays.copyOf(elementData, newCapacity(minCapacity));
}

private Object[] grow() {
	return grow(size + 1);
}

/**
 * Returns a capacity at least as large as the given minimum capacity.
 * Returns the current capacity increased by 50% if that suffices.
 * Will not return a capacity greater than MAX_ARRAY_SIZE unless
 * the given minimum capacity is greater than MAX_ARRAY_SIZE.
 *
 * @param minCapacity the desired minimum capacity
 * @throws OutOfMemoryError if minCapacity is less than zero
 */
private int newCapacity(int minCapacity) {
	// overflow-conscious code
	int oldCapacity = elementData.length;    //旧数组的的大小
	int newCapacity = oldCapacity + (oldCapacity >> 1);   //新数组的大小为原来的1.5倍
	if (newCapacity - minCapacity <= 0) {       
		if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)   //未显式指定大小的情况下  
			return Math.max(DEFAULT_CAPACITY, minCapacity);      //返回传入值和默认值中的较大值，未显示指定指定大小则为10
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return minCapacity;                  //指定为0的大小的数组，此时返回1
	}
	return (newCapacity - MAX_ARRAY_SIZE <= 0)   //判断不会越界
		? newCapacity
		: hugeCapacity(minCapacity);
}

private static int hugeCapacity(int minCapacity) {   //保证不会越界
	if (minCapacity < 0) // overflow
		throw new OutOfMemoryError();
	return (minCapacity > MAX_ARRAY_SIZE)
		? Integer.MAX_VALUE
		: MAX_ARRAY_SIZE;
}