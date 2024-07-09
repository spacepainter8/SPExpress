package student;

//import rs.etf.sab.*;
import java.math.BigDecimal;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.*;


public class StudentMain {

    public static void main(String[] args) {
        bs200051_CityOperations cityOperations = new bs200051_CityOperations(); // Change this to your implementation.
        bs200051_DistrictOperations districtOperations = new bs200051_DistrictOperations(); // Do it for all classes.
        bs200051_CourierOperations courierOperations = new bs200051_CourierOperations(); // e.g. = new MyDistrictOperations();
        bs200051_CourierRequestOperations courierRequestOperation = new bs200051_CourierRequestOperations();
        bs200051_GeneralOperations generalOperations = new bs200051_GeneralOperations();
        bs200051_UserOperations userOperations = new bs200051_UserOperations();
        bs200051_VehicleOperations vehicleOperations = new bs200051_VehicleOperations();
        bs200051_PackageOperations packageOperations = new bs200051_PackageOperations();
//
        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
//        student.PublicModuleTest publicModuleTest = new PublicModuleTest(cityOperations, courierOperations, courierRequestOperation, districtOperations, generalOperations, packageOperations, userOperations, vehicleOperations);
//        publicModuleTest.publicOne();
// paket 11 voznja 10
//          userOperations.insertUser("sp", "Sofija", "Brzakovic", "asoudhauishduiahhauishd1");
//            vehicleOperations.insertVehicle("asddaduh21", 0, BigDecimal.ZERO);
//            courierOperations.insertCourier("sp", "asddaduh21");
//             courierOperations.deleteCourier("sp");
    }
}
