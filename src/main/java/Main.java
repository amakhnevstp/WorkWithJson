import com.google.gson.*;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        //Первая задача
        System.out.println("Первая задача");

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "./src/main/resources/data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        list.stream().forEach(System.out::println);
        String json = listToJson(list);
        writeString(json, "./src/main/resources/data.json");

        //Вторая задача
        System.out.println("Вторая задача");
        List<Employee> listXML = parseXML("./src/main/resources/data.xml");
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "./src/main/resources/dataXML.json");

        //Третья задача
        System.out.println("Третья задача");
        List<Employee> jsonPar = readString("./src/main/resources/dataXML.json");
        jsonPar.stream().forEach(System.out::println);

    }

    private static List<Employee> readString(String s) throws IOException {
        List<Employee> employees = new ArrayList<>();

        FileReader fr= new FileReader(s);
        Scanner scan = new Scanner(fr);
        while (scan.hasNextLine()) {
            String jsonOutput = scan.nextLine();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Employee employee = gson.fromJson(jsonOutput, Employee.class);
            employees.add(employee);
        }
        fr.close();

        return employees;
    }

    private static List<Employee> parseXML(String s) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(s));
        Node root = doc.getDocumentElement();
        System.out.println("Корневой элемент: " + root.getNodeName());
        List<Employee> list = parse(root);

        return list;
    }

    private static List<Employee> parse(Node node) {
        List<Employee> list = new ArrayList<Employee>();

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            NodeList node_List = node_.getChildNodes();
            if (node_.getNodeName() != "#text") {

                Map<String, String> emp = new HashMap<String, String>();

                for (int j = 0; j < node_List.getLength(); j++) {
                    if (Node.ELEMENT_NODE == node_.getNodeType()) {
                        Node nodeEl_ = node_List.item(j);
                        if (nodeEl_.getNodeName() != "#text") {
                            emp.put(nodeEl_.getNodeName(), nodeEl_.getTextContent());
                        }
                    }
                }

                if (emp.size() == 5) {
                    Employee employee = new Employee(Long.parseLong(emp.get("id")), emp.get("firstName"), emp.get("lastName"), emp.get("country"), Integer.parseInt(emp.get("age")));
                    list.add(employee);
                }
            }
        }
        return list;
    }

    private static void writeString(String json, String fileName) throws IOException {
        FileWriter nFile = new FileWriter(fileName);
        nFile.write(json);
        nFile.close();
    }

    private static String listToJson(List<Employee> list) {

        StringBuilder sb = new StringBuilder();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        for (Employee employee : list) {
            sb.append(gson.toJson(employee)).append("\n");
        }

        return sb.toString();
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        List<Employee> list = new ArrayList<Employee>();

        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setColumnMapping(columnMapping);
        strategy.setType(Employee.class);
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            csv.parse().stream().forEach(list::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}

