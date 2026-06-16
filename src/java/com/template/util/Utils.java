package com.template;

public class Utils {
  public static boolean isInteger(String str) {
    if (str == null) {
      return false;
    }
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
