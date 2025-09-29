import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;

// Implement LRU cache -> normal (without thread safety)

class Node {
	Integer id;
	String data;
	Node previous;
	Node next;

	public Node(Integer id, String data){
		this.id = id;
		this.data = data;
	}

}


class DoublyLinkedList {
	private Node head;
	private Node tail;

	public DoublyLinkedList() {
		this.head = null;
		this.tail = null;
	}

	public Node getHead() {
		return head;
	}

	public Node addNode(int id, String data) {
		Node newNode = new Node(id, data);

		if(head == null){
			head = newNode;
			tail = newNode;
			head.previous = null;
			tail.next = null;

			return head;
		} else {
			tail.next = newNode;
			newNode.previous = tail;
			tail = newNode;
			tail.next = null;

			return tail;
		}
	}

	public Node addNode(Node node) {
		if(head == null){
			head = tail = node;
			head.next = null;
			tail.previous = null;

			return head;
		} else {
			tail.next = node;
			node.next = null;
			node.previous = tail;
			tail = node;

			return tail;
		}
	}

	public Node removeNode(Node node){
		if(node.next == null && node.previous == null){
			head = null;
			tail = null;
			return node;
		}

		if(node.previous == null){

			head = node.next;
			node.next = null;
			head.previous = null;

			return node;

		} else if(node.next == null){

			tail = node.previous;
			node.previous = null;
			tail.next = null;

			return node;

		} else {

			Node nextNode = node.next;
			Node previousNode = node.previous;
			previousNode.next = nextNode;
			nextNode.previous = previousNode;

			node.next = null;
			node.previous = null;

			return node;

		}
	}
}

class LRUCache {

	private Integer listSize;
	private Integer currentSize;
	private DoublyLinkedList list;
	private HashMap<Integer, Node> cache;

	// lock
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

	public LRUCache(int size){
		this.listSize = size;
		this.currentSize = 0;
		this.cache = new HashMap<>();
		this.list = new DoublyLinkedList();
	}

	public String get(int key){

		rwLock.writeLock().lock();

		try{
			if(!cache.containsKey(key)){
				return ("Value not found for key: " + key);
			}

			Node listNode = cache.get(key);
			String data = listNode.data;

			// rebalance dll for recency
			list.removeNode(listNode);
			list.addNode(listNode);

			return data;
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	public void put(int key, String value){

		rwLock.writeLock().lock();

		try {
			if(!cache.containsKey(key)){
			// check if we need to evict?
				if (currentSize == listSize){
					// need to evict
					Node head = list.getHead();

					list.removeNode(head);
					cache.remove(head.id);
					Node newNode = list.addNode(key, value);

					cache.put(key, newNode);
					return;
				} else {
					Node newNode = list.addNode(key, value);
					cache.put(key, newNode);

					currentSize += 1;
					return;
				}
			} else {
				// only update operation
				Node listNode = cache.get(key);
				listNode.data = value; // updating the data

				//rebalance the list for recency
				list.removeNode(listNode);
				list.addNode(listNode);

				return;
			}
		} finally {
			rwLock.writeLock().unlock();
		}
	}

}

public class BadThreads {

	public static void main(String args[]) throws Exception {

		LRUCache lruCache = new LRUCache(3);

		// lruCache.put(1, "hello");
		// lruCache.put(2, "world");
		// lruCache.get(2);

		// lruCache.put(3, "dummy");
		// lruCache.put(4, "who is evicted");

		// System.out.println(lruCache.get(2));
		// System.out.println(lruCache.get(1));
		// System.out.println(lruCache.get(3));

		// Create multiple threads that access cache concurrently
        Thread t1 = new Thread(() -> {
            for(int i = 0; i < 5; i++) {
                lruCache.put(i, "Thread1-" + i);
                System.out.println("T1 PUT: " + i);
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
        });

        Thread t2 = new Thread(() -> {
            for(int i = 0; i < 5; i++) {
                String value = lruCache.get(i);
                System.out.println("T2 GET: " + i + " = " + value);
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
        });

        Thread t3 = new Thread(() -> {
            for(int i = 5; i < 10; i++) {
                lruCache.put(i, "Thread3-" + i);
                System.out.println("T3 PUT: " + i);
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
        });

        // Start all threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for all to complete
        t1.join();
        t2.join();
        t3.join();

        System.out.println("\nFinal cache state:");

        System.out.println(lruCache.get(1));
        System.out.println(lruCache.get(2));
        System.out.println(lruCache.get(3));
        System.out.println(lruCache.get(4));
        System.out.println(lruCache.get(5));
        System.out.println(lruCache.get(6));
        System.out.println(lruCache.get(7));
        System.out.println(lruCache.get(8));
        System.out.println(lruCache.get(9));

	}

}
