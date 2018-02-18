import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Generator {
    public static void main(String[] args) throws IOException {
        if (args.length != 3)
            System.exit(0);
        Generator generator = new Generator();

        String settings = args[0]; //settings file
        String data = args[1]; // file with data
        String output = args[2]; // output file

        if (generator.inputSettings(settings))
            System.out.println("Настройки внесены успешно");
        else
            System.out.println("Файл настроек содержит ошибки");

        if (generator.inputData(data))
            System.out.println("Данные внесены успешно");
        else {
            System.out.println("Ошибка при внесении данных");
        }

        if (generator.createPages())
            System.out.println("Страницы созданы");
        else
            System.out.println("Ошибка в создании страницы");

        if (generator.saveTableToFile(output))
            System.out.println("Таблица сохранена успешно");
        else
            System.out.println("Ошибка сохранения");

    }

    private int widthTable;
    private int heightTable;
    private List<Object[]> settingsColumns;
    private List<Entry> entries;
    private List<Page> pages;

    private boolean saveTableToFile(String fileName) throws IOException {
        try {
            File fileDir = new File(fileName);
            Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileDir), "UTF16"));
            out.append(tableToString());
            out.flush();
            out.close();
            return true;
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private String tableToString() {
        StringBuffer res = new StringBuffer();
        for (Page page : getPages()) {
            res.append(page);
            res.append("~\n");
        }
        return res.toString();
    }

    private Entry createHeader() {
        List<Object[]> sett = getSettingsColumns();
        if (sett == null)
            return null;
        String[] mas = new String[sett.size()];
        for (int i = 0; i < sett.size(); i++) {
            mas[i] = (String) sett.get(i)[0];
        }
        return new Entry(sett, mas);
    }

    private boolean createPages() {
        List<Page> pag = new ArrayList<Page>();
        Entry header = createHeader();
        if (header == null)
            return false;
        Page page = new Page(getWidthTable(), getHeightTable());
        page.setEntry(header);
        pag.add(page);
        for (Entry entry : getEntries()) {
            if (!page.setEntry(entry)) {
                page = new Page(getWidthTable(), getHeightTable());
                page.setEntry(header);
                page.setEntry(entry);
                pag.add(page);
            }
        }
        setPages(pag);
        return true;
    }

    private boolean inputData(String fileName) {
        TsvParserSettings settings = new TsvParserSettings();
        TsvParser parser = new TsvParser(settings);
        try {
            List<String[]> allRows = parser.parseAll(new File(fileName), "UTF-16");

            List<Entry> ent = new ArrayList<Entry>();
            for (String[] mas : allRows) {
                ent.add(new Entry(getSettingsColumns(), mas));
            }
            setEntries(ent);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            return false;
        } catch (NullPointerException e) {
            System.out.println(e);
            return false;
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e);
            return false;
        }
        return true;
    }

    private boolean inputSettings(String fileName) {
        try {
            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList pageList = doc.getElementsByTagName("page");
            NodeList columnsList = doc.getElementsByTagName("column");
            if (!setPageSettings(pageList) || !setColumnsSettings(columnsList))
                return false;
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }


    private boolean setPageSettings(NodeList nList) {
        try {
            Element nNode = (Element) nList.item(0);
            int width = Integer.parseInt(nNode.getElementsByTagName("width").item(0).getTextContent());
            int height = Integer.parseInt(nNode.getElementsByTagName("height").item(0).getTextContent());
            if ((width <= 0) || (height <= 0))
                return false;
            setWidthTable(width);
            setHeightTable(height);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean setColumnsSettings(NodeList nList) {
        String columnName;
        int columnWidth;
        int sumWidth = 0;
        List<Object[]> sett = new ArrayList<Object[]>();
        Object[] objects;
        try {
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Element nNode = (Element) nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    columnName = nNode.getElementsByTagName("title").item(0).getTextContent();
                    columnWidth = Integer.parseInt(nNode.getElementsByTagName("width").item(0).getTextContent());
                    sumWidth += columnWidth;
                    objects = new Object[2];
                    objects[0] = columnName;
                    objects[1] = columnWidth;
                    sett.add(objects);
                }
            }
        } catch (Exception e) {
            return false;
        }

        if ((sumWidth + sett.size() * 3 + 1) != getWidthTable())
            return false;

        setSettingsColumns(sett);
        return true;
    }

    private int getWidthTable() {
        return widthTable;
    }

    private void setWidthTable(int widthTable) {
        this.widthTable = widthTable;
    }

    public int getHeightTable() {
        return heightTable;
    }

    private void setHeightTable(int heightTable) {
        this.heightTable = heightTable;
    }

    private void setSettingsColumns(List<Object[]> settingsColumns) {
        this.settingsColumns = settingsColumns;
    }

    private List<Object[]> getSettingsColumns() {
        return settingsColumns;
    }

    public List<Entry> getEntries() {
        if (entries == null)
            return new ArrayList<Entry>();
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}
