package cn.jamie.dlscorridor.core.util;

/**
 * 版本号算法
 *
 * @author jamieLu
 * @create 2024-03-30
 */
public class VersionUtil {
    /**
     * 比较2个版本号大小 字母按字典顺序 数字小于字典
     * @param version1 v1
     * @param version2 v2
     * @return int
     */
    public static int compareVersion(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int maxLength = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLength; i++) {
            String part1 = i < parts1.length ? parts1[i] : "0";
            String part2 = i < parts2.length ? parts2[i] : "0";

            int result = comparePart(part1, part2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private static int comparePart(String part1, String part2) {
        boolean isNumeric1 = part1.matches("\\d+");
        boolean isNumeric2 = part2.matches("\\d+");

        if (isNumeric1 && isNumeric2) {
            return compareNumericParts(part1, part2);
        } else if (isNumeric1) {
            return 1; // Numeric parts come before non-numeric
        } else if (isNumeric2) {
            return -1;
        } else {
            return part1.compareTo(part2);
        }
    }

    private static int compareNumericParts(String part1, String part2) {
        long num1 = Long.parseLong(part1);
        long num2 = Long.parseLong(part2);
        return Long.compare(num1, num2);
    }
}
