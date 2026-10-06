@echo off
echo Starting TicketDaata Microservices...
echo.
echo (Service Registry/Eureka is no longer started - services talk to each
echo  other via plain localhost URLs now that discovery is Kubernetes-native
echo  for the k8s deployment; see k8s/README.md.)
echo.

echo Starting Auth Service...
start "Auth Service" cmd /k "cd /d D:\SDA_Project\TicketDaata\AuthService && mvnw.cmd spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Ticket Service...
start "Ticket Service" cmd /k "cd /d D:\SDA_Project\TicketDaata\ticketservice && mvnw.cmd spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Orders Service...
start "Orders Service" cmd /k "cd /d D:\SDA_Project\TicketDaata\OrdersService && mvnw.cmd spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting API Gateway...
start "API Gateway" cmd /k "cd /d D:\SDA_Project\TicketDaata\APIGateway && mvnw.cmd spring-boot:run"

echo.
echo All services are starting...
echo.
echo Auth Service: http://localhost:9001
echo Ticket Service: http://localhost:8082
echo Orders Service: http://localhost:9002
echo API Gateway: http://localhost:9003
echo.
echo Check individual terminal windows for startup progress.
pause
