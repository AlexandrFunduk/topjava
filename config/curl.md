Test curl request


MealGetAll	
curl -v -X GET http://localhost:8080/topjava/rest/meals/

MealGetById	
curl -v -X GET http://localhost:8080/topjava/rest/meals/100008

MealUpdate	
curl -v -X PUT http://localhost:8080/topjava/rest/meals/100008 -H "Content-Type: application/json" -d "{\"id\":\"100008\",\"dateTime\":\"2020-03-03T10:11:00\",\"description\":\"Обед 3\",\"calories\":1331}" 

MealDelete	
curl -v -X DELETE http://localhost:8080/topjava/rest/meals/100008

MealCreate	
curl -v -d "{\"dateTime\":\"2020-01-10T10:11:00\",\"description\":\"Обед 2\",\"calories\":1111}" -H "Content-Type: application/json" http://localhost:8080/topjava/rest/meals/

MealFilter1	
curl -v -X GET "http://localhost:8080/topjava/rest/meals/filter?endDate=2020-01-30&endTime=22:00"

MealFilter2	
curl -v -X GET "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&endDate=2020-01-31&startTime=10:00&endTime=22:00"

