package com.hao.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.util.Objects;

public class Utils {
  public static <T> boolean isIn(T v, T... a) {
    for (T t : a) {
      if (v == t || v != null && v.equals(t)) {
        return true;
      }
    }
    return false;
  }

  public static <T> boolean notIn(T v, T... a) {
    return !isIn(v, a);
  }

  /**
   * 返回空元素的索引
   * @param v
   * @param <T>
   * @return
   */
  public static <T> int blankIdx(T... v) {
    for (int i = 0; i < v.length; i++) {
      if (Objects.isNull(v)) {
        return i;
      }
    }
    return -1;
  }

  /**
   ** Checks that an Iterable is both non-null and non-empty. This method does not check individual
   * elements in the Iterable, it just checks that the Iterable has at least one element.
   * @param argument
   * @param message
   * @param args
   * @param <S>
   * @param <T>
   * @return
   */
  public static <S, T extends Iterable<S>> T checkNotBlank(T argument, String message, Object... args) {
    Preconditions.checkNotNull(argument, message, args);
    Preconditions.checkArgument(!Iterables.isEmpty(argument), message, args);
    return argument;
  }
  public static void main(String[] args) {
    System.out.println(Utils.isIn(1, 1, null, 3));
    System.out.println(Utils.notIn(1, 0, 2, 3));
  }
}
