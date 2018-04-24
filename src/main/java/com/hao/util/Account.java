package com.hao.util;

public class Account {
  private String name;
  private int age;
  private long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Account() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Account)) return false;

    Account account = (Account) o;

    if (getAge() != account.getAge()) return false;
    if (getId() != account.getId()) return false;
    return getName() != null ? getName().equals(account.getName()) : account.getName() == null;
  }

  @Override
  public int hashCode() {
    int result = getName() != null ? getName().hashCode() : 0;
    result = 31 * result + getAge();
    result = 31 * result + (int) (getId() ^ (getId() >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Account{" + "name='" + name + '\'' + ", age=" + age + ", id=" + id + '}';
  }
}
