package util;

import java.util.List;

/**
 * Created by slgu1 on 12/19/15.
 */
public class VecUtil {
    public static void add(List <Double> list1, List <Double> list2) {
        int l = list1.size();
        for (int i = 0; i < l; ++i)
            list1.set(i, list1.get(i) + list2.get(i));
    }
    public static void norm(List <Double> list1) {
        double res = 0;
        int l = list1.size();
        for (int i = 0; i < l; ++i)
            res += list1.get(i) * list1.get(i);
        if (res == 0)
            return;
        res = Math.sqrt(res);
        for (int i = 0; i < l; ++i)
            list1.set(i, list1.get(i) / res);
    }
}
