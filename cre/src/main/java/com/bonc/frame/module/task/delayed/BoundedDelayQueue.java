package com.bonc.frame.module.task.delayed;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有边界的延迟队列
 * <p>
 * DelayedQueue 无边界
 * <p>
 * 竞态条件
 * 是否有数据 available
 * <p>
 * 方法
 * add/offer/put 直接放入，唤醒take
 * <p>
 * poll/poll(timeout) 非阻塞，没有过期的返回null
 * <p>
 * take 没有数据阻塞，获取过期元素
 * <p>
 * drainTo 非阻塞，只返回所有已过期的元素
 * <p>
 * 改造：
 * BoundedDelayQueue 有边界的延迟队列
 * <p>
 * 竞态条件
 * 是否已满 full
 * <p>
 * 方法
 * put 未满直接放入，已满，阻塞
 * <p>
 * drainTo 未满时，非阻塞，只返回所有已过期的元素
 * 已满，全部取出，唤醒 put
 * <p>
 * 暂时仅支持 put/drainTo
 *
 * @author yedunyao
 * @date 2019/7/12 16:09
 */
public class BoundedDelayQueue<E extends Delayed> extends AbstractQueue<E>
        implements BlockingQueue<E> {

    private final int capacity;

    private transient final ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<E> q;

    private Thread leader = null;

    private final Condition full = lock.newCondition();

    public BoundedDelayQueue(int capacity) {
        this.capacity = capacity;
        q = new PriorityQueue<E>(capacity);
    }

    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.size() == capacity;
        } finally {
            lock.unlock();
        }
    }

    public boolean add(E e) {
        return offer(e);
    }

    public boolean offer(E e) {
        throw new UnsupportedOperationException();
    }

    // put 未满直接放入, 已满，阻塞
    public void put(E e) throws InterruptedException {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (q.size() == capacity)
                full.await();

            q.offer(e);
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(E e, long timeout, TimeUnit unit) {
        return offer(e);
    }

    public E poll() {
        throw new UnsupportedOperationException();
    }

    public E take() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.peek();
        } finally {
            lock.unlock();
        }
    }

    public int drainTo(Collection<? super E> c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (q.size() == capacity) {
                int n = 0;
                for (int i = 0; i < capacity; i++) {
                    c.add(q.poll());
                    ++n;
                }
                full.signal();
                return n;
            } else {
                int n = 0;
                for (; ; ) {
                    E first = q.peek();
                    if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0)
                        break;
                    c.add(q.poll());
                    ++n;
                }
                return n;
            }
        } finally {
            lock.unlock();
        }
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            q.clear();
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.remove(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remainingCapacity() {
        throw new UnsupportedOperationException();
    }

    private static void checkNotNull(Object v) {
        if (v == null)
            throw new NullPointerException();
    }
}
