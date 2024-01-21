# bluejayDeliveryBe

To run this application fisrt of all you have to create a database in PostgreSQL by the name authentication_system
and update usernaame and password.

spring.datasource.url=jdbc:postgresql://localhost:5432/authentication_system
spring.datasource.username=postgres
spring.datasource.password=1234
.............................................................................

after this usint POSTMAN hit below given curls one by onel̥ for results.

This curl takes a excel file as input and saves all the entiries of this file into database. It is the very first and required step to get results.

#curl--> post the excel sheet into the database (postgreSQL)
..............................................................
curl --location 'http://localhost:8080/api/v1/excel/' \
--form 'file=@"/C:/Users/Dell/Downloads/Assignment_Timecard.xlsx"'

#curl--> get employees who have worked seven consetive days
............................................................
curl --location 'http://localhost:8080/api/v1/excel/getEmp' \
--data '''

#curl--> get employees  who have shift gap greater than 1 and less than 10
...............................................................................
curl --location 'http://localhost:8080/api/v1/excel/getEmployeeWithShiftGap'

#curl--> get employees who have worked 14 hrs in a single shift
...................................................................
curl --location 'http://localhost:8080/api/v1/excel/getEmpSingleShift'
