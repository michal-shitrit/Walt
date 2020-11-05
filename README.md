For this exercise you will need to use the following technologies:

Spring Boot 
Spring data
Junit test

You will need to Implement Walt app system.

Walt is an on-line food delivery service. Walt works with different restaurants, each store has it: Name City Address

The company has driver list, for each driver the system saves: Name City When a customer order, they must provide: Name City Address Time of delivery Restaurant to deliver from, they can only choose restaurants in the city they live in

Write a service to assign available driver for delivery, driver should be picked if he/she lives in the same city of the restaurant & customer, he/she has no other delivery at the same time. If more than one driver is available assign it to the least busy driver according to the driver history
when assigning a delivery, save the distance from the restaurant to the customer, for this purpose the distance will be random number between 0-20 Km
Walt want to rank the drivers based on their total distance the did for deliveries. Please provide a detailed report to display the drivers name and the total distance of delivery order by total distance in descending order
Provide same report as No. 3 in specific city (Bonus – not a must)
Add Junit tests to test your work
Assumptions:

Each drive takes a full hour – it will start and end in a full hour (no need to calculate minutes)
Since distance is randomized on delivery, deliveries from same restaurant to same customer can be different in each delivery
If no available driver in city – provide proper error message
