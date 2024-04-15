# CSV Reader with Admin Units

## Project description
The project integrates the CSVReader and AdminUnit components, enabling the reading of administrative unit data from a CSV file, processing it and performing various operations such as filtering, sorting and querying based on different criteria.

## Classes Overview
**CSVReader**: This class facilitates reading CSV files. It provides methods to parse CSV files, handle headers as well as missing columns and retrieve data from specific columns.

**AdminUnit**: Represents an administrative unit with properties such as identifier, name, administrative level, population, area, density, parent unit, children units and bounding box.

**AdminUnitList**: Manages a list of administrative units. It provides methods for reading administrative unit data from a CSV file, listing administrative units, filtering, sorting and querying administrative units based on various criteria.

**AdminUnitQuery**: Enables querying administrative units by providing methods to set predicates, comparators, limits and offsets.

**BoundingBox**: Represents a bounding box for geographical areas. It provides methods to add points, check containment, intersection, calculate distances with haversine formula and retrieve center coordinates.

## Usage
```java
AdminUnitList list = new AdminUnitList();
list.read("src/data/admin-units.csv");

// query example
AdminUnitQuery query = new AdminUnitQuery()
    .selectFrom(list)
    .where(a -> a.area < 10000)
    .and(a -> a.name.endsWith("awa"))
    .sort((a, b) -> Double.compare(a.area, b.area))
    .limit(100);

query.execute().list(System.out);
```
## Summary
This project has provided me with a solid understanding of object-oriented programming, CSV parsing techniques, effective exception handling and advanced data manipulation using features like streams and lambdas.
