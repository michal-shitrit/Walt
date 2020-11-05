package com.walt;

import com.walt.dao.*;
import com.walt.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WaltTest {

    @TestConfiguration
    static class WaltServiceImplTestContextConfiguration {

        @Bean
        public WaltService waltService() {
            return new WaltServiceImpl();
        }
    }

    @Autowired
    WaltService waltService;

    @Resource
    CityRepository cityRepository;

    @Resource
    CustomerRepository customerRepository;

    @Resource
    DriverRepository driverRepository;

    @Resource
    DeliveryRepository deliveryRepository;

    @Resource
    RestaurantRepository restaurantRepository;

    @BeforeEach()
    public void prepareData(){

        City city1 = new City("city1");
        City city2 = new City("city2");
        City city3 = new City("city3");
        City city4 = new City("city4");

        cityRepository.save(city1);
        cityRepository.save(city2);
        cityRepository.save(city3);
        cityRepository.save(city4);

        Driver driver1 = new Driver("driver1", city1);
        Driver driver2 = new Driver("driver2", city1);
        Driver driver3 = new Driver("driver3", city2);

        driverRepository.save(driver1);
        driverRepository.save(driver2);
        driverRepository.save(driver3);

        Customer customer1 = new Customer("name1", city1, "address");
        Customer customer2 = new Customer("name2", city1, "address");
        Customer customer3 = new Customer("name3", city2, "address");
        Customer customer4 = new Customer("name4", city3, "address");

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        customerRepository.save(customer4);

        Restaurant restaurant1 = new Restaurant("rest1", city1, "add");
        Restaurant restaurant2 = new Restaurant("rest2", city2, "add");
        Restaurant restaurant3 = new Restaurant("rest3", city3, "add");

        restaurantRepository.save(restaurant1);
        restaurantRepository.save(restaurant2);
        restaurantRepository.save(restaurant3);
    }

    @Test
    public void testBasics(){

        assertEquals(((List<City>) cityRepository.findAll()).size(),4);
        assertEquals((driverRepository.findAllDriversByCity(cityRepository.findByName("city1")).size()), 2);
    }

    @Test
    public void testCreateSingleOrder(){

        Customer customer3 = customerRepository.findByName("name3");
        Restaurant restaurant2 = restaurantRepository.findByName("rest2");
        Date date  = new Date();
        Driver driver3 = driverRepository.findByName("driver3");

        Delivery delivery = waltService.createOrderAndAssignDriver(customer3,restaurant2, date);
        assertEquals(driver3.getId(), delivery.getDriver().getId());
    }

    @Test
    public void testCreateTwoDeliveries(){
        Customer customer1 = customerRepository.findByName("name1");
        Customer customer2 = customerRepository.findByName("name2");

        Restaurant restaurant1 = restaurantRepository.findByName("rest1");
        Date date  = new Date();

        Delivery delivery = waltService.createOrderAndAssignDriver(customer1,restaurant1, date);

        Driver driver1 = driverRepository.findByName("driver1");
        Driver driver2 = driverRepository.findByName("driver2");
        Long expectedSecondDRiver;

        if (delivery.getDriver().getId().equals(driver1.getId())){
            expectedSecondDRiver = driver2.getId();
        }else {
            expectedSecondDRiver = driver1.getId();
        }

        Delivery delivery2 = waltService.createOrderAndAssignDriver(customer2,restaurant1, date);
        assertEquals(expectedSecondDRiver, delivery2.getDriver().getId());
    }

    @Test
    public void testCreateTwoDeliveriesOneBusy(){
        Customer customer1 = customerRepository.findByName("name1");
        Customer customer2 = customerRepository.findByName("name2");

        Restaurant restaurant1 = restaurantRepository.findByName("rest1");
        LocalDateTime date  = LocalDateTime.of(2020, Month.DECEMBER, 1,22, 0);

        Delivery delivery = waltService.createOrderAndAssignDriver(customer1,restaurant1, Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

        Driver driver1 = driverRepository.findByName("driver1");
        Driver driver2 = driverRepository.findByName("driver2");
        Long expectedSecondDriver;

        if (delivery.getDriver().getId().equals(driver1.getId())){
            expectedSecondDriver = driver2.getId();
        }else {
            expectedSecondDriver = driver1.getId();
        }

        date = date.plusHours(1);
        Delivery delivery2 = waltService.createOrderAndAssignDriver(customer2,restaurant1, Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
        assertEquals(expectedSecondDriver, delivery2.getDriver().getId());

        date = date.plusHours(2);
        Delivery delivery3 = waltService.createOrderAndAssignDriver(customer2,restaurant1, Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

        if (delivery.getDistance() > delivery2.getDistance()){
            expectedSecondDriver = delivery2.getDriver().getId();
        }else{
            expectedSecondDriver = delivery.getDriver().getId();
        }

        assertEquals(expectedSecondDriver, delivery3.getDriver().getId());

        waltService.getDriverRankReport().stream().forEach(e -> {
            System.out.println(e.getDriver().getName() + ":"  + e.getTotalDistance());
        });
    }
}
