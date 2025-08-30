@echo off
echo Starting TicketDaata Microservices...
echo.

echo Starting Service Registry...
start "Service Registry" cmd /k "cd /d c:\Users\rahma\Desktop\TIcketdaata\TicketDaata\ServiceRegistry && mvnw.cmd spring-boot:run"

timeout /t 15 /nobreak > nul

echo Starting Auth Service...
start "Auth Service" cmd /k "cd /d c:\Users\rahma\Desktop\TIcketdaata\TicketDaata\AuthService && mvnw.cmd spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Orders Service...
start "Orders Service" cmd /k "cd /d c:\Users\rahma\Desktop\TIcketdaata\TicketDaata\OrdersService && mvnw.cmd spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting API Gateway...
start "API Gateway" cmd /k "cd /d c:\Users\rahma\Desktop\TIcketdaata\TicketDaata\APIGateway && mvnw.cmd spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Ticket Service...
start "Ticket Service" cmd /k "cd /d c:\Users\rahma\Desktop\TIcketdaata\TicketDaata\ticketservice && mvnw.cmd spring-boot:run"

echo.
echo All services are starting...
echo.
echo Service Registry: http://localhost:8761
echo Auth Service: http://localhost:9001
echo Orders Service: http://localhost:9002
echo API Gateway: http://localhost:9003
echo Ticket Service: http://localhost:8082
echo.
echo Check individual terminal windows for startup progress.
pause
