package com.hao.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TestDaemon {
    /**
     * 验证当无user thread，只有Daemon thread时，jvm退出
     * @throws Exception
     */
  public static void daemonDieExit() throws Exception {
    Thread t =
        new Thread(
            () -> {
              for (int i = 0; i < 100; i++) {
                System.out.println(i + "\tdamon:");
                try {
                  Thread.sleep(1000);
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            });
    t.setDaemon(true);
    t.start();
    Thread.sleep(3000);
    System.out.println("main exit");
    System.exit(0);
  }

    /**
     * 即使采用了daemon thread的线程池，当无user thread时，线程池内运行的daemon 任务也会被终止。
     * 再次说明ThreadPoolExecutor提供的线程池没有生成其他user thread
     * @throws Exception
     */
  public static void testDaemonThreadPool() throws Exception {
    final AtomicInteger inc = new AtomicInteger(1);
    ExecutorService es =
        Executors.newFixedThreadPool(
            10,
            (Runnable r) -> {
              Thread t =
                  new Thread(
                      Thread.currentThread().getThreadGroup(),
                      r,
                      "Daemon thread:" + inc.getAndIncrement() ,
                      0);
              t.setDaemon(true);
//              if (t.getPriority() != Thread.NORM_PRIORITY) {
//                t.setPriority(Thread.NORM_PRIORITY);
//              }
              return t;
            });
    for (int j = 0; j < 5; j++) {
      es.submit(
          () -> {
            for (int i = 0; i < 10; i++) {
              System.out.println(Thread.currentThread().getName() + " is running:" + i);
              try {
                Thread.sleep(1000L);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          });
    }
    System.out.println("main thread wait 3000ms");
    try {
      Thread.sleep(3000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("main thread end");
  }

  public static void main(String[] args) throws Exception {
    testDaemonThreadPool();
  }
}
