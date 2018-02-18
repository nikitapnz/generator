import java.util.ArrayList;
import java.util.List;

public class Page {
    private int widthMax;
    private int heightMax;
    private List<Entry> entries;
    private int rowsLength;

    public Page(int widthMax, int heightMax) {
        this.widthMax = widthMax;
        this.heightMax = heightMax;
        entries = new ArrayList<Entry>();
        rowsLength = 0;
    }

    public boolean setEntry(Entry entry) {
        int entryRowsLength = entry.getNumOfRows();
        if (getHeightMax() < (entryRowsLength + rowsLength + entries.size()))
            return false;
        rowsLength += entryRowsLength;
        entries.add(entry);
        return true;
    }

    private String delimiterEntries(int k) {
        String res = "";
        for (int i = 0; i < k; i++)
            res += "-";
        return res;
    }


    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        String delim = delimiterEntries(getWidthMax());
        List<Entry> ent = getEntries();
        for (int i = 0; i < ent.size(); i++) {
            res.append(ent.get(i).toString() + delim + "\n");
        }
        return res.toString();
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public int getWidthMax() {
        return widthMax;
    }

    public void setWidthMax(int widthMax) {
        this.widthMax = widthMax;
    }

    public int getHeightMax() {
        return heightMax;
    }

    public void setHeightMax(int heightMax) {
        this.heightMax = heightMax;
    }
}
