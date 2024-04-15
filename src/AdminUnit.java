import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class AdminUnit {
    String name;
    int adminLevel;
    double population;
    double area;
    double density;
    AdminUnit parent;
    List<AdminUnit> children = new ArrayList<>();
    BoundingBox bbox = new BoundingBox();

    AdminUnit() {}

    //setter
    public void setName(String name) {this.name = name;}
    public void setAdminLevel(int adminLevel) {this.adminLevel = adminLevel;}
    public void setPopulation(double population) {this.population = population;}
    public void setArea(double area) {this.area = area;}
    public void setDensity(double density) {this.density = density;}
    public void setParent(AdminUnit parent) {this.parent = parent;}
    public void setBbox(BoundingBox bbox) {this.bbox = bbox;}

    //getter
    public String getName() {return name;}
    public int getAdminLevel() {return adminLevel;}
    public double getPopulation() {return population;}
    public double getArea() {return area;}
    public double getDensity() {return density;}


    public String toString() {
        final DecimalFormat df = new DecimalFormat("0.00");
        return "Name: {"+ name + "}, admin_level: {" + adminLevel + "}, population: {" + df.format(population) + "}, area: {" + area
                + "}, density: {"+ density + "}, bbox: {" + bbox.toString() + "}";
    }
}