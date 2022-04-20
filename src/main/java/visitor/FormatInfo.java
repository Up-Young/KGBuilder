package visitor;

public class FormatInfo {
    public static String validChar = " ?,.QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890_()<>[]-:%";

    public static String formatClassName(String ori) {
        StringBuilder formatName = new StringBuilder();

        for (int i = 0; i < ori.length(); i++) {
            char c = ori.charAt(i);
            if (!validChar.contains(c + "")) {
                return "";
            }
            if (c == ' ') {
                continue;
            }
            if(c == '(')
                return formatName.toString();
            formatName.append(c);
        }
        return formatName.toString();


    }


}
