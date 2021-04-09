package com.nowcoder.community;

// 测试阻塞队列

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();

    }

}


class Producer implements Runnable{
    private BlockingQueue<Integer> queue ; // 在实例化线程时把阻塞队列传进来
    public Producer(BlockingQueue<Integer> queue){
        this.queue = queue;

    }
    @Override
    public void run() {
        try {
            for(int i =0;i<100;i++){
                Thread.sleep(20);   // 企业生产数据
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产:"+queue.size());
            }


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}


class Consumer implements Runnable{

    private BlockingQueue<Integer> queue ; // 在实例化线程时把阻塞队列传进来
    public Consumer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true){
                Thread.sleep(new Random().nextInt(1000)); // 模拟用户真实消费数据场景
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费:"+queue.size());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
