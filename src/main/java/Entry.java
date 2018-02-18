import java.util.ArrayList;
import java.util.List;

public class Entry {
    private List<List<String>> row;
    private List<Object[]> settings;

    Entry(List<Object[]> list, String[] mas) {
        row = new ArrayList<List<String>>();
        settings = list;
        createRows(list, mas);
    }


    private void createRows(List<Object[]> list, String[] mas) {
        int len;
        String[] split;
        for (int i = 0; i < list.size(); i++) {
            len = (Integer) list.get(i)[1];
            split = mas[i].split("(?=([^0-9a-zA-Zа-яА-Я-]+))");
            row.add(splitWord(len, split));
        }
    }

    private List<String> splitWord(int len, String[] split) {
        int left = len;
        List<String> rows = new ArrayList<String>();
        for (String line : split) {
            if ((left == len) && (left >= line.length())) {
                rows.add(line);
                left -= line.length();
            } else if (left >= (line.length())) {
                rows.set(rows.size() - 1, rows.get(rows.size() - 1) + line);
                left -= line.length();
            } else if (len >= (line.length())) {
                line = line.replace(" ", "");
                left = len - line.length();
                rows.add(line);
            } else {
                line = line.replace(" ", "");
                while (line.length() >= len) {
                    rows.add(line.substring(0, len));
                    line = line.substring(len);
                    if (len > line.length() && line.length() != 0) {
                        rows.add(line);
                        left = len - line.length();
                    }
                }

            }
        }
        return rows;
    }

    private List<List<String>> getRow() {
        return row;
    }

    int getNumOfRows() {
        List<List<String>> list = getRow();
        int max = list.get(0).size();
        for (int i = 1; i < list.size(); i++)
            if (max < list.get(i).size())
                max = list.get(i).size();
        return max;
    }

    private String getSpaces(int m) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < m; i++)
            res.append(" ");
        return res.toString();
    }

    @Override
    public String toString() {
        int maxRows = getNumOfRows();
        List<List<String>> rows = getRow();
        List<Object[]> settings = getSettings();
        StringBuffer result = new StringBuffer();
        int widthSettings, wordLength;
        String word;
        for (int i = 0; i < maxRows; i++) {
            for (int j = 0; j < rows.size(); j++) {
                widthSettings = (Integer) settings.get(j)[1];
                if ((rows.get(j).size() - 1 >= i) && (rows.get(j).get(i) != null)) {
                    word = rows.get(j).get(i);
                    wordLength = word.length();
                    result.append("| " + word + getSpaces(widthSettings - wordLength));
                } else {
                    result.append("| " + getSpaces(widthSettings));
                }
                result.append(" ");
            }
            result.append("|\n");
        }
        return result.toString();
    }

    private List<Object[]> getSettings() {
        return settings;
    }
}
