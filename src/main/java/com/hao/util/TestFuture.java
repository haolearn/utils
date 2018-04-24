package com.hao.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFuture {

  public static void testFutureRunnable(ExecutorService executor) throws Exception {
    Future<?> future =
        executor.submit(
            () -> {
              try {
                Thread.sleep(10000);
              } catch (InterruptedException e) {
                System.out.println("Epic fail.");
              }
            });
    System.out.println("Waiting for task to finish..");
    future.get();
    System.out.println("Task finished!");
  }

  private static void testFutureListLambda(ExecutorService executor) {
    List<Future<String>> futures = new ArrayList<>();
    final AtomicInteger a = new AtomicInteger(0);
    final Random r = new Random();
    for (int i = 0; i < 10; i++) {
      final int id = a.getAndAdd(1);
      futures.add(
          executor.submit(
              () -> {
                Thread.sleep(2000);
                if (id == 2) {
                  System.out.println(id + "\t is blocking!!!!!!!!");
                  Thread.sleep(Integer.MAX_VALUE);
                } else {
                  Thread.sleep(r.nextInt(10) * 1000);
                }
                System.out.println(id+" is called!!");
                  Thread.sleep(1000);
                return id + "\ti'm done:"+System.currentTimeMillis();
              }));
    }

    futures.forEach(
        f -> {
          try {
            //              System.out.println(f.get(5000, TimeUnit.MILLISECONDS)); // 读取超时控制.
            System.out.println("1:"+f.get());
            System.out.println("2:"+f.get());// 可以重复读取结果.
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  private static void testFutureList(ExecutorService executor) {
    List<Future<String>> futures = new ArrayList<>();
    final AtomicInteger a = new AtomicInteger(0);
    final Random r = new Random();
    for (int i = 0; i < 5; i++) {
      final int id = a.getAndAdd(1);
      futures.add(
          executor.submit(
              new Callable<String>() {
                @Override
                public String call() throws Exception {
                  if (id == 2) { // 模拟请求被阻塞.
                    System.out.println(id + "\t is blocking!!!!!!!!");
                    Thread.sleep(Integer.MAX_VALUE);
                  } else {
                    Thread.sleep(r.nextInt(10) * 1000);
                  }
                  return id + "\ti'm done";
                }
              }));
    }
    futures.forEach(
        f -> {
          try {
            //              System.out.println(f.get(5000, TimeUnit.MILLISECONDS));
            System.out.println(f.get());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  public static void main(String[] args) throws Exception {
    //    ExecutorService executor = Executors.newCachedThreadPool();
    ExecutorService executor =
        new ThreadPoolExecutor(0, 30, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    try {
//      testFutureRunnable(executor);
      //        testFutureList(executor);
      testFutureListLambda(executor);
    } finally {
      executor.shutdown();
    }
  }
}
