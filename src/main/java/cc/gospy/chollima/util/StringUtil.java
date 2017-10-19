package cc.gospy.chollima.util;

public class StringUtil {
    public static String concatAutoIncreasedSuffix(String name) {
        int leftBracePos = name.lastIndexOf('(');
        int rightBracePos = name.lastIndexOf(')');
        if (rightBracePos - leftBracePos > 1 && leftBracePos > 0) {
            try {
                int mark = Integer.parseInt(name.substring(leftBracePos + 1, rightBracePos));
                if (mark > 0 && name.endsWith(")")) {
                    name = name.substring(0, leftBracePos + 1)
                            .concat(String.valueOf(mark + 1)).concat(name.substring(rightBracePos));
                } else {
                    name = name.concat(" (1)");
                }
            } catch (Exception e) {
                name = name.concat(" (1)");
            }
        } else {
            name = name.concat(" (1)");
        }
        return name;
    }
}
