# REST API example application

The REST API to the example app is described below.

## Get list of all meals
### Request
`GET /rest/meals/`
	
    curl -v -X GET http://localhost:8080/topjava/rest/meals/

### Response

    HTTP/1.1 200 
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Mon, 23 Nov 2020 16:31:48 GMT
    Keep-Alive: timeout=20
    Connection: keep-alive
    
    [{"id":100008,"dateTime":"2020-01-31T20:00:00","description":"Ужин","calories":510,"excess":true},{"id":100007,"dateTime":"2020-01-31T13:00:00","description":"Обед","calories":1000,"excess":true},{"id":100006,"dateTime":"2020-01-31T10:00:00","description":"Завтрак","calories":500,"excess":true},{"id":100005,"dateTime":"2020-01-31T00:00:00","description":"Еда на граничное значение","calories":100,"excess":true},{"id":100004,"dateTime":"2020-01-30T20:00:00","description":"Ужин","calories":500,"excess":false},{"id":100003,"dateTime":"2020-01-30T13:00:00","description":"Обед","calories":1000,"excess":false},{"id":100002,"dateTime":"2020-01-30T10:00:00","description":"Завтрак","calories":500,"excess":false}]

## Get meal by id
### Request
`GET /rest/meals/id`

    curl -v -X GET http://localhost:8080/topjava/rest/meals/100008

### Response

    HTTP/1.1 200 
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Mon, 23 Nov 2020 16:34:24 GMT
    Keep-Alive: timeout=20
    Connection: keep-alive
    
    {"id":100008,"dateTime":"2020-01-31T20:00:00","description":"Ужин","calories":510,"user":null}

## Update meal by id
### Request
`PUT /rest/meals/id`
	
    curl -v -X PUT http://localhost:8080/topjava/rest/meals/100008 -H "Content-Type: application/json" -d "{\"id\":\"100008\",\"dateTime\":\"2020-03-03T10:11:00\",\"description\":\"Обед 3\",\"calories\":1331}" 

### Response

    HTTP/1.1 204 
    Date: Mon, 23 Nov 2020 16:35:32 GMT
    Keep-Alive: timeout=20
    Connection: keep-alive

## Delete meal by id
### Request
`DELETE /rest/meals/id`

    curl -v -X DELETE http://localhost:8080/topjava/rest/meals/100008

### Response

    HTTP/1.1 204 
    Date: Mon, 23 Nov 2020 16:36:07 GMT
    Keep-Alive: timeout=20
    Connection: keep-alive

## Create meal
### Request
`POST /rest/meals/`

    curl -v -d "{\"dateTime\":\"2020-01-10T10:11:00\",\"description\":\"Обед 2\",\"calories\":1111}" -H "Content-Type: application/json" http://localhost:8080/topjava/rest/meals/

### Response

    HTTP/1.1 201 
    Location: http://localhost:8080/topjava/rest/meals/100011
    Content-Length: 0
    Date: Mon, 23 Nov 2020 16:37:48 GMT
    Keep-Alive: timeout=20
    Connection: keep-alive
    
    {"id":100012,"dateTime":"2020-12-30T13:10:00","description":"Обед 2","calories":1100,"user":null}

## Filter meals
Request parameters is optional
### Request
`POST /rest/meals/filter?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd&startTime=HH:mm&endTime=HH:mm"`

    curl -v -X GET "http://localhost:8080/topjava/rest/meals/filter?endDate=2020-01-30&endTime=22:00"

### Response

    HTTP/1.1 200 
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Mon, 23 Nov 2020 16:04:03 GMT
    Keep-Alive: timeout=20
    Connection: keep-alive

    [{"id":100004,"dateTime":"2020-01-30T20:00:00","description":"Ужин","calories":500,"excess":false},{"id":100003,"dateTime":"2020-01-30T13:00:00","description":"Обед","calories":1000,"excess":false},{"id":100002,"dateTime":"2020-01-30T10:00:00","description":"Завтрак","calories":500,"excess":false}]
