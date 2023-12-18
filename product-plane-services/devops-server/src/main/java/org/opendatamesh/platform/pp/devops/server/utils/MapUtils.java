package org.opendatamesh.platform.pp.devops.server.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MapUtils {

    public static final int findMaxTaskNumber(Map<String, ?> map) {

        int maxTaskNumber = 0;
        Pattern pattern = Pattern.compile("task(\\d+)");

        for (String key : map.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                int currentTaskNumber = Integer.parseInt(matcher.group(1));
                maxTaskNumber = Math.max(maxTaskNumber, currentTaskNumber);
            }
        }

        return maxTaskNumber;

    }

}
