package com.javarush.task.task19.task1918;

import java.io.IOException;
import java.util.*;

public class Solution {
    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        String fileName = reader.readLine();
//        reader.close();
//        BufferedReader fr = new BufferedReader(new FileReader(fileName));
        String line = "Info about Leela <span xml:lang=\"en\" lang=\"en\"><b><span>Turanga Leela<span>Leela</span></span></b></span><span>Super<span>Super</span></span><span>girl</span>";
//        while (fr.ready())
//            line += fr.readLine();
//        fr.close();

//        String tag = args[0];
        String tag = "span";
        String openTagSpace = "<" + tag + " ";
        String openTagArrow = "<" + tag + ">";
        String closeTag = "</" + tag + ">";

        int nesting = 0;
        List<Integer> insSpace = searchAll(openTagSpace, line);
        List<Integer> insArrow = searchAll(openTagArrow, line);
        List<Integer> outs = searchAll(closeTag, line);
        SortedMap<Integer, String> orderMap = new TreeMap<>();
        for (Integer position : insSpace)
            orderMap.put(position, "begin");
        for (Integer position : insArrow)
            orderMap.put(position, "begin");
        for (Integer position : outs)
            orderMap.put(position, "end");
        int endPosition;

        ArrayList<Integer> nest = new ArrayList<>();
        ArrayList<Integer> nestpos = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : orderMap.entrySet()) {
            if (entry.getValue().equals("begin")) {
                nesting++;
                nest.add(nesting);
                nestpos.add(entry.getKey());
            }
            if (entry.getValue().equals("end")) {
                endPosition = entry.getKey() + closeTag.length();
                nest.add(nesting);
                nestpos.add(endPosition);
                nesting--;
            }
        }

        int max = maxnum(nest);
        ArrayList<String> ss = new ArrayList<>();
        while (!nest.isEmpty()) {
            int i = 0, j;
            while (nest.get(i) != max && max > 1)
                i++;
            while (nest.get(i) != 1 && max > 1)
                i--;
            int begin = nestpos.get(i);
            j = i;
            i++;
            while (nest.get(i) != 1 && max > 1)
                i++;
            int end = nestpos.get(i);
            ss.add(line.substring(begin, end));
            for (int k = j; k <= i; k++) {
                int temp = nest.get(k);
                nest.set(k, --temp);
            }
            for (int k = nest.size() - 1; k >= 0; k--) {
                if (nest.get(k) == 0) {
                    nest.remove(k);
                    nestpos.remove(k);
                }
            }
            max = maxnum(nest);
        }
        for (String st : ss)
            System.out.println(st);
    }

    private static int maxnum(List<Integer> arr) {
        int max = 0;
        for (Integer i : arr) {
            if (i > max)
                max = i;
        }
        return max;
    }

    private static List<Integer> searchAll(String pattern, String text) {
        int M = pattern.length();
        int i = 0, N = text.length();
        List<Integer> list = new ArrayList<>();
        while (i < (N - M + 1)) {
            if (text.substring(i, i + M).equals(pattern))
                list.add(i);
            i++;
        }
        return list;
    }
}
