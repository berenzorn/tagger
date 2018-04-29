
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;

public class TaggerJS {

    public static Set<String> stringset = new HashSet<>();

    public static void main(String[] args) throws IOException {
        String line = "Info about Leela <span xml:lang=\"en\" lang=\"en\"><b><span>Turanga Leela</span></b></span><span>Super</span><span>girl</span>";
        String tag = "span";
        SortedMap<Integer, String> orderMap;
        orderMap = mapreader(line, tag);
        String closeTag = "</" + tag + ">";
//
//        Document html = Jsoup.parse(line);
//        Elements elements = html.getElementsByTag(tag);
//        for (Element element : elements)
//            System.out.println(element);
//
        int nesting = 0, begin = 0, end, max = 0;
        SortedMap<String, Integer> substr = new TreeMap<>();
        for (Map.Entry<Integer, String> entry : orderMap.entrySet()) {
            if (entry.getValue().equals("begin")) {
                if (nesting == 0)
                    begin = entry.getKey();
                nesting++;
                max = max < nesting? nesting : max;
            }
            if (entry.getValue().equals("end")) {
                nesting--;
                if (nesting == 0) {
                    end = entry.getKey() + closeTag.length();
                    substr.put(line.substring(begin, end), max);
                    max = 0;
                }
            }
        }
        for (Map.Entry<String, Integer> entry : substr.entrySet()) {
            stringset.add(entry.getKey());
            divider(entry.getKey(), tag);
        }
        List<String> stringList = new ArrayList<>();
        max = checkmax(stringset, tag);
        while (!stringset.isEmpty()) {
            stringList.add(getmax(stringset, tag, max));
            max = checkmax(stringset, tag);
        }
        for (String fi : stringList)
            System.out.println(fi);
    }

    private static List<String> sortlen(List<String> list){
        List<String> list1 = new ArrayList<>(list);
        for (int i = 1; i < list1.size(); i++) {
            if (list1.get(i).length() > list1.get(i - 1).length()) {
                list1.set(i - 1, list1.get(i));
                list1.remove(i + 1);
            }
        }
        return list1;
    }

    private static int checkmax(Set<String> set, String tag) {
        int nesting = 0, max = 0;
        for (String entry : set) {
            SortedMap<Integer, String> orderMap;
            orderMap = mapreader(entry, tag);
            for (Map.Entry<Integer, String> mapentry : orderMap.entrySet()) {
                if (mapentry.getValue().equals("begin")) {
                    nesting++;
                    max = max < nesting ? nesting : max;
                }
                if (mapentry.getValue().equals("end"))
                    nesting--;
            }
        }
        return max;
    }

    private static String getmax(Set<String> set, String tag, int max) {
        int nesting = 0;
        for (String entry : set) {
            SortedMap<Integer, String> orderMap;
            orderMap = mapreader(entry, tag);
            for (Map.Entry<Integer, String> mapentry : orderMap.entrySet()) {
                if (mapentry.getValue().equals("begin")) {
                    nesting++;
                    if (nesting == max) {
                        stringset.remove(entry);
                        return entry;
                    }
                }
                if (mapentry.getValue().equals("end"))
                    nesting--;
            }
        }
        return null;
    }


    private static void divider (String str, String tag) {
        SortedMap<Integer, String> orderMap;
        String closeTag = "</" + tag + ">";
        orderMap = mapreader(str, tag);
        while (!orderMap.isEmpty()) {
            orderMap = mapreader(str, tag);
            List<Integer> keys = new ArrayList<>(orderMap.keySet());
            if(keys.size() > 2) {
                stringset.add(str.substring(keys.get(0), keys.get(1)) + str.substring(keys.get(keys.size() - 2) + closeTag.length()));
                orderMap.remove(keys.get(0));
                orderMap.remove(keys.get(keys.size() - 1));
                stringset.add(str.substring(keys.get(1), keys.get(keys.size() - 2) + closeTag.length()));
                str = str.substring(keys.get(1), keys.get(keys.size() - 2) + closeTag.length());
            }
            else {
                stringset.add(str.substring(keys.get(0), keys.get(1) + closeTag.length()));
                orderMap.remove(keys.get(0));
                orderMap.remove(keys.get(1));
                str = "";
            }
        }
    }

    private static SortedMap<Integer, String> mapreader (String str, String tag) {
        SortedMap<Integer, String> orderMap = new TreeMap<>();

        String openTagSpace = "<" + tag + " ";
        String openTagArrow = "<" + tag + ">";
        String closeTag = "</" + tag + ">";

        List<Integer> insSpace = searchAll(openTagSpace, str);
        List<Integer> insArrow = searchAll(openTagArrow, str);
        List<Integer> outs = searchAll(closeTag, str);
        for (Integer position : insSpace)
            orderMap.put(position, "begin");
        for (Integer position : insArrow)
            orderMap.put(position, "begin");
        for (Integer position : outs)
            orderMap.put(position, "end");

        return orderMap;
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
