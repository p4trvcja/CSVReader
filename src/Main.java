import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws Exception {
        adminUnits();
    }

    public static void adminUnits() throws Exception {
        AdminUnitList adminUnits = new AdminUnitList();
        adminUnits.read("admin-units.csv");
        adminUnits.list(System.out);
    }
}