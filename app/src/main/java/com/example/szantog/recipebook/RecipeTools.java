package com.example.szantog.recipebook;

public class RecipeTools {

    private static String removeGlutanation(String text) {
        if (text.length() < 4) {
            return text;
        }
        if (text.substring(text.length() - 3, text.length()).equals("val")
                || text.substring(text.length() - 3, text.length()).equals(
                "vel")) {
            return text.substring(0, text.length() - 3);
        } else if (text.charAt(text.length() - 4) == text
                .charAt(text.length() - 3)
                && (text.substring(text.length() - 2, text.length()).equals(
                "al") || text.substring(text.length() - 2,
                text.length()).equals("el"))) {
            return text.substring(0, text.length() - 3);
        } else if (text.substring(text.length() - 2, text.length())
                .equals("os")) {
            return text.substring(0, text.length() - 2);
        } else {
            return text;
        }
    }

    public static float compareStrings(String str1, String str2) {

        if (str1.equals(str2)) {
            return 1;
        } else if (str1.length() == 0 || str2.length() == 0) {
            return 0;
        }

        String[] arr1 = str1.replaceAll("-", " ").split(" ");
        String[] arr2 = str2.replaceAll("-", " ").split(" ");

        int hit = 0;

        for (String text : arr1) {
            if (str2.contains(removeGlutanation(text))) {
                hit++;
            }
        }

        for (String text : arr2) {
            if (str1.contains(removeGlutanation(text))) {
                hit++;
            }
        }
        return (float) hit / (arr1.length + arr2.length);
    }
}
