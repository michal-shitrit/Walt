package com.walt;

import com.walt.dao.DeliveryRepository;
import com.walt.dao.DriverRepository;
import com.walt.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaltServiceImpl implements WaltService {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Override
    public Delivery createOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryTime) {

        Driver driver = findAvailableDriverInCity(customer.getCity(), deliveryTime);
        Delivery delivery = new Delivery(driver, restaurant, customer, deliveryTime);
        deliveryRepository.save(delivery);

        return delivery;
    }

    private Driver findAvailableDriverInCity(City city, Date deliveryTime) {

        List<Driver> allAvailableDrivers = driverRepository.findAllDriversByCity(city).stream()
            .filter(driver -> deliveryRepository.findDeliveryByDriverAndDeliveryTime(driver, deliveryTime) == null).collect(Collectors.toList());

        Driver driver = allAvailableDrivers.stream()
                .min((Comparator.comparingDouble(this::getTotalDistanceByDriver)))
                .orElse(null);

        if (driver == null){
            throw new RuntimeException(String.format("No available driver was found in %s at %d", city.getName(), deliveryTime.toInstant()));
        }

        return  driver;
    }


    private  Double getTotalDistanceByDriver(Driver driver){
        return deliveryRepository.findAllByDriver(driver).stream().mapToDouble(o -> o.getDistance()).sum();
    }

    @Override
    public List<DriverDistance> getDriverRankReport() {

        List<DriverDistance> distanceByDriver = deliveryRepository.findTotalDistanceByDriver().stream()
                .sorted(Comparator.comparing(DriverDistance::getTotalDistance).reversed())
                .collect(Collectors.toList());

        return distanceByDriver;
    }

    @Override
    public List<DriverDistance> getDriverRankReportByCity(City city) {
        return null;
    }
}
