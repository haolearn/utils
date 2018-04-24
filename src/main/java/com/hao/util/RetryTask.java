package com.hao.util;

import java.util.concurrent.*;

/**
 * 带有重试策略的多线程任务封装
 *
 * @param <K>
 * @param <V>
 */
public class RetryTask<K, V> implements Callable<RetryTask<K, V>> {
  private K request; // 請求報文.
  private V response; // 結果報文.
  private int retryCount; // 重试次数.
  private RetryTaskHandler<K, V> handler; // 请求处理器.
  private boolean timeout = false; // 是否超時.
  private boolean reachMax = false; // 是否達到最大重試次數.
  private long retryInterval = 0L; // ms ,两次重试最小间隔，0不限.
  private int maxRetry = 0; // 最大允许尝试次数，0不限.
  private long lastTimeStamp = 0L; // unix timestamp,上次请求时间戳.

  public RetryTask(K request, RetryTaskHandler<K, V> handler) {
    this.request = request;
    this.handler = handler;
  }

  public RetryTask(K request) {
    this(request, null);
  }

  public void setRequest(K request) {
    this.request = request;
  }

  public void setResponse(V response) {
    this.response = response;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }

  public void setTimeout(boolean timeout) {
    this.timeout = timeout;
  }

  public boolean isReachMax() {
    return reachMax;
  }

  public void setReachMax(boolean reachMax) {
    this.reachMax = reachMax;
  }

  public long getRetryInterval() {
    return retryInterval;
  }

  public void setRetryInterval(long retryInterval) {
    this.retryInterval = retryInterval;
  }

  public int getMaxRetry() {
    return maxRetry;
  }

  public void setMaxRetry(int maxRetry) {
    this.maxRetry = maxRetry;
  }

  public long getLastTimeStamp() {
    return lastTimeStamp;
  }

  public void setLastTimeStamp(long lastTimeStamp) {
    this.lastTimeStamp = lastTimeStamp;
  }

  private boolean isDone() {
    return !isTimeout() || reachMax;
  }

  @Override
  public RetryTask call() throws Exception {
    try {
      ++retryCount;
      if (maxRetry > 0L && retryCount > maxRetry) {
        reachMax = true;
        throw new TimeoutException("over max retry!");
      }
      long _now = System.currentTimeMillis();
      if (retryInterval > 0L && _now - lastTimeStamp < retryInterval) {
        Thread.sleep(retryInterval - _now + lastTimeStamp);
        retryInterval = retryInterval + retryInterval / 2; // 默认时间间隔半步增加.
      }
      // TODO 模拟超时情况.
      if (Math.random() > 0.8) {
        throw new TimeoutException("timeout!");
      }
      // 发送业务请求
      response = handler.call(request);

      timeout = false;
    } catch (TimeoutException e) {
      System.out.println(request + "\ttimeout:" + retryCount + "\t" + e.getMessage());
      timeout = true;
    } finally {
      lastTimeStamp = System.currentTimeMillis();
    }
    return this;
  }

  public K getRequest() {
    return this.request;
  }

  public V getResponse() {
    return response;
  }

  public boolean isTimeout() {
    return timeout;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public static void main(String[] args) throws Exception {
    ExecutorService re = Executors.newFixedThreadPool(10);
    // 模拟两种业务共用线程池，根据RetryTask<K,V>区分不同类封装任务.
    RetryTaskHandler<String, String> handler_a = (String req) -> "reply to " + req;
    RetryTaskHandler<Integer, Integer> handler_b = (Integer req) -> req.intValue() + 1;
    int THREADS = 1000;
    try {
      CompletionService<RetryTask<String, String>> cs_a = new ExecutorCompletionService<>(re);
      CompletionService<RetryTask<Integer, Integer>> cs_b = new ExecutorCompletionService<>(re);
      for (int i = 0; i < THREADS; i++) {
        RetryTask<String, String> task_a = new RetryTask<>("a_thread_" + i, handler_a);
        task_a.retryInterval = 500L;
        task_a.maxRetry = 10;
        cs_a.submit(task_a);

        RetryTask<Integer, Integer> task_b = new RetryTask<>(i, handler_b);
        task_b.retryInterval = 100L;
        task_b.maxRetry = 10;
        cs_b.submit(task_b);
      }
      int cnt = 0;
      while (cnt < THREADS) {
        //        Future<RetryTask<String, String>> f_a = cs_a.take(); // 阻塞获取任务处理结果.
        //       Future<RetryTask<String, String>> f_a = cs.poll(10L,TimeUnit.SECONDS);// 查询结果超时策略 .

        // 业务a.
        Future<RetryTask<String, String>> f_a = cs_a.poll(); // 无结果返回null.
        if (null != f_a) {
          RetryTask t_a = f_a.get();
          if (!t_a.isDone()) {
            cs_a.submit(t_a);
          } else {
            ++cnt;
            System.out.println(
                "a_TASK\t"
                    + t_a.getRequest()
                    + "\t"
                    + t_a.retryCount
                    + "\t completed!"
                    + t_a.getResponse());
          }
        }
        // 业务b.
        Future<RetryTask<Integer, Integer>> f_b = cs_b.poll();
        if (null != f_b) {
          RetryTask t_b = f_b.get();
          if (!t_b.isDone()) {
            cs_b.submit(t_b);
          } else {
            ++cnt;
            System.out.println(
                "b_TASK\t"
                    + t_b.getRequest()
                    + "\t"
                    + t_b.retryCount
                    + "\t completed!"
                    + t_b.getResponse());
          }
        }
      }
    } finally {
      re.shutdown();
    }
  }
}
