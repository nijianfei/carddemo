package com.jobcard.demo;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class LinkedBlockingDequeTest {
    /**
     * 无参构造函数默认Integer.MAX_VALUE初始化容量
     *
     * @Param
     */
    @Test
    public void testConstruct0() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        for (int i = 0; i < 10000; i++) {
            testObj.add(i);
        }
        System.out.println(testObj.toString());
        System.out.println(testObj.size());
        for (int i = 0; i < 3; i++) {
            System.out.println(testObj.getFirst() + "  --  "+ testObj.getLast() + "\n");
        }
        for (int i = 0; i < 3; i++) {
            System.out.println(testObj.removeFirst() + "  --  "+ testObj.removeLast() + "\n");
        }
        System.out.println(testObj.toString());
        System.out.println(testObj.size());
    }

    /**
     * 指定大小的双向队列
     *
     * @Param
     */
    @Test
    public void testConstruct1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque(3);
        System.out.println(testObj.toString());
    }

    /**
     * 通过集合初始化双向链表
     *
     * @Param
     */
    @Test
    public void testConstruct2() throws Exception {
        Set set = new HashSet();
        set.add(3);
        LinkedBlockingDeque testObj = new LinkedBlockingDeque(set);
        System.out.println(testObj.toString());
    }

    /**
     * 从队列头部开始添加
     *
     * @Param
     */
    @Test
    public void testAddFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.addFirst(3333);
        testObj.addFirst(3333111);
        testObj.addFirst(333322);
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getLast());
        System.out.println(testObj.getLast());
        System.out.println(testObj.getLast());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());

    }

    /**
     * 从队列尾部开始添加
     *
     * @Param
     */
    @Test
    public void testAddLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.addLast(3333);
        testObj.addLast(3333111);
        testObj.addLast(333322);
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getLast());
    }

    /**
     * 和addFirst处理一样，只是返回是否正确处理:true,false
     *
     * @Param
     */
    @Test
    public void testOfferFirst1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.offerFirst(3333));
        testObj.offerFirst(3333111);
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getLast());
    }

    /**
     * 和addLast处理一样，只是返回是否正确处理:true,false
     *
     * @Param
     */
    @Test
    public void testOfferLast1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.offerLast(3333));
        testObj.offerLast(3333111);
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getLast());
    }

    /**
     * 从队列头部开始放，会等待
     *
     * @Param
     */
    @Test
    public void testPutFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque(3);
        testObj.putFirst(1);
        testObj.putFirst(2);
        testObj.addFirst(4);
        testObj.putFirst(3);
        System.out.println(testObj.getFirst());
    }

    /**
     * 从队列尾部开始放，会等待
     *
     * @Param
     */
    @Test
    public void testPutLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.putLast(1);
        testObj.putLast(2);
        System.out.println(testObj.getFirst());
    }

    /**
     * 从队列头部开始放
     *
     * @Param
     */
    @Test
    public void testOfferFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerFirst(1, 1L, TimeUnit.SECONDS);
        testObj.offerFirst(2, 1L, TimeUnit.SECONDS);
        System.out.println(testObj.getFirst());
    }

    /**
     * 从队列尾部开始放
     *
     * @Param
     */
    @Test
    public void testOfferLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(1, 1L, TimeUnit.SECONDS);
        testObj.offerLast(2, 1L, TimeUnit.SECONDS);
        System.out.println(testObj.getFirst());
    }

    /**
     * 从头部开始移除
     *
     * @Param
     */
    @Test
    public void testRemoveFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.removeFirst());
        System.out.println(testObj.removeFirst());
        System.out.println(testObj.getFirst());
    }

    /**
     * 从头部开始移除并返回原值
     *
     * @Param
     */
    @Test
    public void testRemoveLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.removeLast());
        System.out.println(testObj.removeLast());
        System.out.println(testObj.getLast());
    }

    /**
     * 从队列头部开始取，取完删除
     *
     * @Param
     */
    @Test
    public void testPollFirst1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.pollFirst());
        System.out.println(testObj.pollFirst());
        System.out.println(testObj.pollFirst());
    }

    /**
     * 从队列尾部开始取，取完删除
     *
     * @Param
     */
    @Test
    public void testPollLast1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.pollLast());
        System.out.println(testObj.pollLast());
        System.out.println(testObj.pollLast());
    }

    /**
     * 取队列中第一个元素
     *
     * @Param
     */
    @Test
    public void testTakeFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.takeFirst());
        System.out.println(testObj.takeFirst());
        System.out.println(testObj.takeFirst());
    }

    /**
     * 取队列中最后一个元素
     *
     * @Param
     */
    @Test
    public void testTakeLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.takeLast());
        System.out.println(testObj.takeLast());
        System.out.println(testObj.takeLast());
    }

    /**
     * 取第一个元素，等待一秒
     *
     * @Param
     */
    @Test
    public void testPollFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.pollFirst(1, TimeUnit.SECONDS));
        System.out.println(testObj.pollFirst(1, TimeUnit.SECONDS));
        System.out.println(testObj.pollFirst(1, TimeUnit.SECONDS));
    }

    /**
     * 取最后一个元素，等待一秒
     *
     * @Param
     */
    @Test
    public void testPollLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.pollLast(1, TimeUnit.SECONDS));
        System.out.println(testObj.pollLast(1, TimeUnit.SECONDS));
        System.out.println(testObj.pollLast(1, TimeUnit.SECONDS));
    }

    /**
     * 取第一个元素，没有抛异常
     *
     * @Param
     */
    @Test
    public void testGetFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
    }

    /**
     * 取最后一个元素，没有抛异常
     *
     * @Param
     */
    @Test
    public void testGetLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.getLast());
        System.out.println(testObj.getLast());
        System.out.println(testObj.getLast());
    }

    /**
     * 不移除元素，取第一个元素，没有返回null
     *
     * @Param
     */
    @Test
    public void testPeekFirst() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.peekFirst());
        System.out.println(testObj.peekFirst());
        System.out.println(testObj.peekFirst());
    }

    /**
     * 不移除元素，取最后一个元素,没有返回null
     *
     * @Param
     */
    @Test
    public void testPeekLast() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offerLast(3333);
        testObj.offerLast(3333111);
        System.out.println(testObj.peekLast());
        System.out.println(testObj.peekLast());
        System.out.println(testObj.peekLast());
    }

    /**
     * 移除第一个指定元素
     *
     * @Param
     */
    @Test
    public void testRemoveFirstOccurrence() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.push(3333);
        testObj.push(3333);
        testObj.push(3333111);
        System.out.println(testObj.removeFirstOccurrence(3333));
        System.out.println(testObj.removeFirstOccurrence(3333));
        System.out.println(testObj.removeFirstOccurrence(3333111));
        System.out.println(testObj.getFirst());
    }

    /**
     * 移除最后一个指定元素
     *
     * @Param
     */
    @Test
    public void testRemoveLastOccurrence() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.push(3333);
        testObj.push(3333);
        testObj.push(3333111);
        System.out.println(testObj.removeLastOccurrence(3333));
        System.out.println(testObj.removeLastOccurrence(3333));
        System.out.println(testObj.removeLastOccurrence(3333111));
        System.out.println(testObj.getLast());
    }

    /**
     * 默认从尾部添加并返回boolean表示添加是否成功
     * 和offer处理一样
     *
     * @Param
     */
    @Test
    public void testAdd() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.add(3333));
        System.out.println(testObj.add(3333111));
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
    }

    /**
     * 默认从尾部添加并返回boolean表示添加是否成功
     *
     * @Param
     */
    @Test
    public void testOffer1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.offer(3333));
        System.out.println(testObj.offer(3333111));
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
    }

    /**
     * 默认从队尾开始放
     *
     * @Param
     */
    @Test
    public void testPut() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.put(3333);
        testObj.put(2222);
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
    }

    /**
     * 默认从队尾开始放,等待一秒
     *
     * @Param
     */
    @Test
    public void testOffer() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.offer(3333, 1, TimeUnit.SECONDS));
        System.out.println(testObj.offer(3333111, 1, TimeUnit.SECONDS));
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
    }

    /**
     * 默认从头部开始移除
     *
     * @Param
     */
    @Test
    public void testRemove1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.offer(3333, 1, TimeUnit.SECONDS));
        System.out.println(testObj.offer(3333111, 1, TimeUnit.SECONDS));
        System.out.println(testObj.remove());
        System.out.println(testObj.remove());
        System.out.println(testObj.getFirst());
        System.out.println(testObj.getFirst());
    }

    /**
     * 默认从头部开始弹出
     *
     * @Param
     */
    @Test
    public void testPoll1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.offer(3333, 1, TimeUnit.SECONDS));
        System.out.println(testObj.offer(3333111, 1, TimeUnit.SECONDS));
        System.out.println(testObj.poll());
        System.out.println(testObj.poll());
        System.out.println(testObj.poll());
    }

    /**
     * 默认从头部开始取
     *
     * @Param
     */
    @Test
    public void testTake() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        new Thread(()-> {
            try {
                Thread.sleep(10000);
                testObj.add(3333222);
                System.out.println("offer："+testObj.size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        System.out.println(testObj.offer(3333, 1, TimeUnit.SECONDS));
        System.out.println(testObj.offer(3333111, 1, TimeUnit.SECONDS));
        System.out.println("初始化："+testObj.size());
        System.out.println(testObj.take());
        System.out.println("take1："+testObj.size());
        System.out.println(testObj.take());
        System.out.println("take2："+testObj.size());
        System.out.println(testObj.take());
        System.out.println("take3："+testObj.size());
    }

    /**
     * 默认从头部开始取出，并移除，没有返回null
     * 1秒
     *
     * @Param
     */
    @Test
    public void testPoll() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.add(3333));
        System.out.println(testObj.add(3333111));
        System.out.println(testObj.poll(1, TimeUnit.SECONDS));
        System.out.println(testObj.poll(1, TimeUnit.SECONDS));
        System.out.println(testObj.poll(1, TimeUnit.SECONDS));
    }

    /**
     * 内部使用getFirst，取头部元素不移除
     *
     * @Param
     */
    @Test
    public void testElement() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.add(3333));
        System.out.println(testObj.add(3333111));
        System.out.println(testObj.element());
        System.out.println(testObj.element());
        System.out.println(testObj.element());
    }

    /**
     * 默认从头部开始取出，不移除
     *
     * @Param
     */
    @Test
    public void testPeek() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.add(3333));
        System.out.println(testObj.add(3333111));
        System.out.println(testObj.peek());
        System.out.println(testObj.peek());
        System.out.println(testObj.peek());
    }

    /**
     * 查询剩余容量
     *
     * @Param
     */
    @Test
    public void testRemainingCapacity() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque(3);
        System.out.println(testObj.add(3333));
        System.out.println(testObj.add(3333111));
        System.out.println(testObj.remainingCapacity());
    }

    /**
     * 将队列中元素一次性拷贝到指定集合中，并返回大小
     *
     * @Param
     */
    @Test
    public void testDrainTo1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.put(321);
        List s = new ArrayList();
        System.out.println(testObj.size());
        System.out.println(testObj.drainTo(s));
        System.out.println(s);
    }

    /**
     * 将队列中元素一次性拷贝到指定集合中，并返回大小
     *
     * @Param
     */
    @Test
    public void testDrainTo() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.put(321321);
        List s = new ArrayList();
        System.out.println(testObj.size());
        System.out.println(testObj.drainTo(s, 3));
        System.out.println(s);
    }

    /**
     * 内部使用addfirst，从头部开始添加
     *
     * @Param
     */
    @Test
    public void testPush() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.push(3333);
        testObj.push(3333111);
        System.out.println(testObj.element());
        System.out.println(testObj.element());
        System.out.println(testObj.element());
    }

    /**
     * 使用removeFirst，移除并返回头部元素,没有返回null
     *
     * @Param
     */
    @Test
    public void testPop() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.add(3333));
        System.out.println(testObj.add(3333111));
        System.out.println(testObj.pop());
        System.out.println(testObj.pop());
        System.out.println(testObj.pop());
    }

    /**
     * 从头部开始移除，并返回移除的值，没有则抛异常
     *
     * @Param
     */
    @Test
    public void testRemove() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.add(3333));
        System.out.println(testObj.add(3333111));
        System.out.println(testObj.remove());
        System.out.println(testObj.remove());
        System.out.println(testObj.remove());
    }

    /**
     * 队列中元素数量
     *
     * @Param
     */
    @Test
    public void testSize() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.push(3333);
        testObj.push(3333111);
        System.out.println(testObj.size());
    }

    /**
     * 从头部开始查找是否包含指定元素
     *
     * @Param
     */
    @Test
    public void testContains() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        LinkedBlockingDeque testObj1 = new LinkedBlockingDeque();
        testObj.push(3333);
        testObj.push(testObj1);
        testObj.push(3333111);
        System.out.println(testObj.contains(testObj1));
    }

    /**
     * 打印地址
     *
     * @Param
     */
    @Test
    public void testToArray1() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offer(123);
        System.out.println(testObj.toArray());
    }

    /**
     * 将队列中元素转换到相同类型数组中
     *
     * @Param
     */
    @Test
    public void testToArray() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offer("3213");
        Object[] objects = testObj.toArray(new String[testObj.size()]);
        System.out.println(objects);
    }

    /**
     * toString
     *
     * @Param
     */
    @Test
    public void testToString() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        System.out.println(testObj.toString());
    }

    /**
     * 移除所有
     *
     * @Param
     */
    @Test
    public void testClear() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offer(123);
        testObj.clear();
        System.out.println(testObj.poll());
    }

    /**
     * 使用iterator遍历队列中元素
     *
     * @Param
     */
    @Test
    public void testIterator() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offer("3213");
        Iterator iterator = testObj.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    /**
     * 使用倒叙iterator遍历队列中元素
     *
     * @Param
     */
    @Test
    public void testDescendingIterator() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offer("3213");
        testObj.offer("3111");
        testObj.offer("3222");
        testObj.offer("3333");
        Iterator iterator = testObj.descendingIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    /**
     * 通过拆分器遍历队列中所有元素
     *
     * @Param
     */
    @Test
    public void testSpliterator() throws Exception {
        LinkedBlockingDeque testObj = new LinkedBlockingDeque();
        testObj.offer("3213");
        testObj.offer("3111");
        testObj.offer("3222");
        testObj.offer("3333");
        Spliterator<String> spliterator = testObj.spliterator();
        spliterator.forEachRemaining(s -> System.out.println(s));
    }

}
