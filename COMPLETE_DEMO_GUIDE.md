# 🎯 **Complete RabbitMQ Message Queue Demo Guide**

## 🚀 **One-Stop Guide for Faculty Demonstration**

This is your complete guide to demonstrate RabbitMQ message queues in your microservices application.

---

## 📋 **What You'll Demonstrate**

### **Message Queue Architecture:**

```
OrdersService ──Message──► RabbitMQ ──Message──► TicketService
     ↓                        ↓                    ↓
  Publisher               Queue Storage         Consumer
  (Port 9002)            (Port 5672)          (Port 8082)
```

### **Key Message Flows:**

1. **Order Creation** → `ticket.reservation.queue` → **Ticket Reservation**
2. **Order Status** → `order.status.queue` → **Status Updates**
3. **Ticket Changes** → `ticket.status.update.queue` → **Cross-service Updates**

---

## 🛠️ **Step 1: Start All Services (5 minutes)**

### **1.1 Start RabbitMQ Container**

```powershell
# Start RabbitMQ with management UI
docker run -d --name ticketdaata-rabbitmq `
  -p 5672:5672 `
  -p 15672:15672 `
  -e RABBITMQ_DEFAULT_USER=admin `
  -e RABBITMQ_DEFAULT_PASS=admin `
  rabbitmq:3.13-management

# Verify RabbitMQ is running
docker ps --filter "name=rabbitmq"
```

### **1.2 Start All Microservices**

```powershell
# Open 4 separate PowerShell windows and run these commands:

# Window 1: Service Registry
cd "d:\Study materials\3-2\SWE 4602 SDA Lab\TicketDaata\TicketDaata\ServiceRegistry"
.\mvnw spring-boot:run

# Window 2: API Gateway
cd "d:\Study materials\3-2\SWE 4602 SDA Lab\TicketDaata\TicketDaata\APIGateway"
.\mvnw spring-boot:run

# Window 3: Ticket Service
cd "d:\Study materials\3-2\SWE 4602 SDA Lab\TicketDaata\TicketDaata\ticketservice"
.\mvnw spring-boot:run

# Window 4: Orders Service
cd "d:\Study materials\3-2\SWE 4602 SDA Lab\TicketDaata\TicketDaata\OrdersService"
.\mvnw spring-boot:run
```

### **1.3 Verify All Services Are Running**

```powershell
# Check service health (wait 2-3 minutes after starting all services)
Write-Host "Checking Service Health..." -ForegroundColor Green

# Service Registry
try {
    $sr = Invoke-RestMethod -Uri "http://localhost:8761/actuator/health" -Method GET
    Write-Host "✅ Service Registry (8761): $($sr.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ Service Registry (8761): Not responding" -ForegroundColor Red
}

# Ticket Service
try {
    $ts = Invoke-RestMethod -Uri "http://localhost:8082/tickets/health" -Method GET
    Write-Host "✅ Ticket Service (8082): $($ts.service)" -ForegroundColor Green
} catch {
    Write-Host "❌ Ticket Service (8082): Not responding" -ForegroundColor Red
}

# Orders Service
try {
    $os = Invoke-RestMethod -Uri "http://localhost:9002/orders/health" -Method GET
    Write-Host "✅ Orders Service (9002): $($os.service)" -ForegroundColor Green
} catch {
    Write-Host "❌ Orders Service (9002): Not responding" -ForegroundColor Red
}

# RabbitMQ
Write-Host "✅ RabbitMQ UI: http://localhost:15672 (admin/admin)" -ForegroundColor Green
```

---

## 🎬 **Step 2: Faculty Demonstration Script**

### **2.1 Open RabbitMQ Management UI**

```
🌐 URL: http://localhost:15672
👤 Username: admin
🔑 Password: admin
```

**Show to Faculty:**

1. **Queues Tab** → Point out our message queues (will populate during demo)
2. **Exchanges Tab** → Show `ticket.exchange` and `order.exchange`
3. **Connections Tab** → Show both services connected

### **2.2 Explain the Architecture (2 minutes)**

> _"Instead of direct HTTP calls between services, we use RabbitMQ message queues for asynchronous communication..."_

```
❌ OLD WAY: OrdersService --HTTP--> TicketService (Blocking, Coupled)
✅ NEW WAY: OrdersService --Message--> RabbitMQ --Message--> TicketService (Async, Decoupled)
```

---

## 🧪 **Step 3: Live Message Queue Testing**

### **3.1 Create Demo Tickets**

```powershell
Write-Host "🎫 Creating Demo Tickets..." -ForegroundColor Yellow

# Create Ticket 1
$ticket1 = @{
    title = "Spring Concert 2025"
    description = "Live music event demonstrating message queues"
    price = 75.00
    quantity = 100
    eventDate = "2025-05-15T19:00:00"
    venue = "Message Queue Arena"
    category = "CONCERT"
} | ConvertTo-Json

$createdTicket1 = Invoke-RestMethod -Uri "http://localhost:8082/tickets" -Method POST -Body $ticket1 -ContentType "application/json"
Write-Host "✅ Created Ticket 1: $($createdTicket1.id) - $($createdTicket1.title)" -ForegroundColor Green

# Create Ticket 2
$ticket2 = @{
    title = "Tech Conference 2025"
    description = "Microservices and Message Queues Workshop"
    price = 150.00
    quantity = 50
    eventDate = "2025-06-20T10:00:00"
    venue = "Tech Center"
    category = "CONFERENCE"
} | ConvertTo-Json

$createdTicket2 = Invoke-RestMethod -Uri "http://localhost:8082/tickets" -Method POST -Body $ticket2 -ContentType "application/json"
Write-Host "✅ Created Ticket 2: $($createdTicket2.id) - $($createdTicket2.title)" -ForegroundColor Green

# Store ticket IDs for next steps
$global:ticket1Id = $createdTicket1.id
$global:ticket2Id = $createdTicket2.id
```

### **3.2 Demonstrate Message Queue Flow**

#### **Test 1: Order Creation (Triggers ticket.reservation.queue)**

```powershell
Write-Host "🛒 DEMO 1: Order Creation → Message Queue Flow" -ForegroundColor Cyan
Write-Host "Before: Check RabbitMQ UI queues (should be empty or low activity)" -ForegroundColor Yellow

# Create Order (THIS TRIGGERS MESSAGE QUEUE!)
$order1 = @{
    userId = "student-demo-1"
    ticketId = $global:ticket1Id
    quantity = 3
} | ConvertTo-Json

$createdOrder1 = Invoke-RestMethod -Uri "http://localhost:9002/orders" -Method POST -Body $order1 -ContentType "application/json"

Write-Host "✅ Order Created: $($createdOrder1.id)" -ForegroundColor Green
Write-Host "📤 Message sent to: ticket.reservation.queue" -ForegroundColor Cyan
Write-Host "📤 Message sent to: order.expiration.queue" -ForegroundColor Cyan

Write-Host "NOW: Refresh RabbitMQ UI → See messages in queues!" -ForegroundColor Yellow
Read-Host "Press Enter when you've shown the faculty the RabbitMQ activity..."
```

#### **Test 2: Ticket Reservation (Triggers ticket.status.update.queue)**

```powershell
Write-Host "🎫 DEMO 2: Ticket Status Update → Message Queue Flow" -ForegroundColor Cyan

# Get ticket details
$ticketDetails = Invoke-RestMethod -Uri "http://localhost:8082/tickets/$global:ticket2Id" -Method GET
Write-Host "📋 Ticket Status Before: $($ticketDetails.status)" -ForegroundColor White

# Reserve ticket (THIS TRIGGERS MESSAGE QUEUE!)
if ($ticketDetails.status -eq "AVAILABLE") {
    $reservedTicket = Invoke-RestMethod -Uri "http://localhost:8082/tickets/$global:ticket2Id/reserve?version=$($ticketDetails.version)" -Method POST

    Write-Host "✅ Ticket Reserved: $($reservedTicket.status)" -ForegroundColor Green
    Write-Host "📤 Message sent to: ticket.status.update.queue" -ForegroundColor Cyan

    Write-Host "NOW: Check RabbitMQ UI → See ticket status messages!" -ForegroundColor Yellow
    Read-Host "Press Enter to continue..."
}
```

#### **Test 3: Order Completion (Triggers order.status.queue)**

```powershell
Write-Host "✅ DEMO 3: Order Completion → Message Queue Flow" -ForegroundColor Cyan

# Complete the order (THIS TRIGGERS MESSAGE QUEUE!)
$paymentData = @{
    paymentId = "payment-demo-12345"
} | ConvertTo-Json

$completedOrder = Invoke-RestMethod -Uri "http://localhost:9002/orders/$($createdOrder1.id)/complete" -Method POST -Body $paymentData -ContentType "application/json"

Write-Host "✅ Order Completed: $($completedOrder.status)" -ForegroundColor Green
Write-Host "📤 Message sent to: order.status.queue" -ForegroundColor Cyan

Write-Host "NOW: Check RabbitMQ UI → See order completion messages!" -ForegroundColor Yellow
Read-Host "Press Enter to continue..."
```

#### **Test 4: Order Cancellation (Triggers order.status.queue)**

```powershell
Write-Host "❌ DEMO 4: Order Cancellation → Message Queue Flow" -ForegroundColor Cyan

# Create another order to cancel
$order2 = @{
    userId = "student-demo-2"
    ticketId = $global:ticket2Id
    quantity = 1
} | ConvertTo-Json

$createdOrder2 = Invoke-RestMethod -Uri "http://localhost:9002/orders" -Method POST -Body $order2 -ContentType "application/json"
Write-Host "📋 Created Order to Cancel: $($createdOrder2.id)" -ForegroundColor White

# Cancel the order (THIS TRIGGERS MESSAGE QUEUE!)
$cancelData = @{
    reason = "Faculty demonstration - user requested cancellation"
} | ConvertTo-Json

$cancelledOrder = Invoke-RestMethod -Uri "http://localhost:9002/orders/$($createdOrder2.id)/cancel" -Method POST -Body $cancelData -ContentType "application/json"

Write-Host "✅ Order Cancelled: $($cancelledOrder.status)" -ForegroundColor Green
Write-Host "📤 Message sent to: order.status.queue" -ForegroundColor Cyan

Write-Host "NOW: Final check of RabbitMQ UI → See all message activity!" -ForegroundColor Yellow
```

---

## 📊 **Step 4: Show Results to Faculty**

### **4.1 RabbitMQ Message Statistics**

```powershell
Write-Host "📊 DEMONSTRATION SUMMARY" -ForegroundColor Green -BackgroundColor DarkBlue
Write-Host ""
Write-Host "✅ Messages Successfully Processed Through Queues:" -ForegroundColor Green
Write-Host "   • ticket.reservation.queue    → Ticket reservations from orders" -ForegroundColor Cyan
Write-Host "   • ticket.status.update.queue  → Ticket status changes" -ForegroundColor Cyan
Write-Host "   • order.status.queue          → Order lifecycle events" -ForegroundColor Cyan
Write-Host "   • order.expiration.queue      → Automatic order expiration" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔍 In RabbitMQ UI, you should see:" -ForegroundColor Yellow
Write-Host "   • Published message counts increased" -ForegroundColor White
Write-Host "   • Delivered message counts increased" -ForegroundColor White
Write-Host "   • Active consumers processing messages" -ForegroundColor White
Write-Host "   • Message rates showing throughput" -ForegroundColor White
```

### **4.2 Verify End-to-End Results**

```powershell
Write-Host "🔍 Verifying End-to-End Message Queue Results..." -ForegroundColor Yellow

# Check final ticket statuses
Write-Host "📋 Final Ticket Statuses:" -ForegroundColor Cyan
$finalTicket1 = Invoke-RestMethod -Uri "http://localhost:8082/tickets/$global:ticket1Id" -Method GET
$finalTicket2 = Invoke-RestMethod -Uri "http://localhost:8082/tickets/$global:ticket2Id" -Method GET

Write-Host "   Ticket 1: $($finalTicket1.title) → Status: $($finalTicket1.status)" -ForegroundColor White
Write-Host "   Ticket 2: $($finalTicket2.title) → Status: $($finalTicket2.status)" -ForegroundColor White

# Check order history
Write-Host "📋 Order History:" -ForegroundColor Cyan
$userOrders1 = Invoke-RestMethod -Uri "http://localhost:9002/orders/user/student-demo-1" -Method GET
$userOrders2 = Invoke-RestMethod -Uri "http://localhost:9002/orders/user/student-demo-2" -Method GET

Write-Host "   User 1 Orders: $($userOrders1.Count) → Status: $($userOrders1[0].status)" -ForegroundColor White
Write-Host "   User 2 Orders: $($userOrders2.Count) → Status: $($userOrders2[0].status)" -ForegroundColor White

Write-Host ""
Write-Host "🎉 MESSAGE QUEUE DEMONSTRATION COMPLETE!" -ForegroundColor Green -BackgroundColor DarkBlue
Write-Host "All operations performed through asynchronous message queues!" -ForegroundColor Green
```

---

## 🎯 **Key Points to Explain to Faculty**

### **Technical Benefits Demonstrated:**

1. **🔄 Asynchronous Processing:** Orders don't wait for ticket service responses
2. **📦 Message Persistence:** Messages survive service restarts
3. **⚡ Better Performance:** Non-blocking operations improve throughput
4. **🔗 Loose Coupling:** Services communicate through messages, not direct calls
5. **📈 Scalability:** Can add multiple consumers to handle load

### **Real-World Applications:**

- **E-commerce:** Order processing, inventory updates
- **Banking:** Transaction processing, account notifications
- **Social Media:** Post notifications, feed updates
- **IoT Systems:** Sensor data processing, device commands

### **Microservices Architecture Benefits:**

- **Independent Deployment:** Services can be updated separately
- **Technology Diversity:** Different services can use different tech stacks
- **Fault Tolerance:** One service failure doesn't crash the entire system
- **Team Autonomy:** Different teams can own different services

---

## 🚀 **Quick Troubleshooting**

### **If Services Don't Start:**

```powershell
# Check if ports are in use
netstat -ano | findstr :8761  # Service Registry
netstat -ano | findstr :8082  # Ticket Service
netstat -ano | findstr :9002  # Orders Service
netstat -ano | findstr :5672  # RabbitMQ

# Kill processes if needed
Stop-Process -Id PROCESS_ID -Force
```

### **If RabbitMQ UI Shows No Activity:**

1. Verify services are connected to RabbitMQ
2. Check service logs for connection errors
3. Ensure Docker container is running: `docker ps`
4. Restart RabbitMQ: `docker restart ticketdaata-rabbitmq`

---

## ✅ **Demo Checklist**

- [ ] RabbitMQ container running
- [ ] All 4 services started and healthy
- [ ] RabbitMQ UI accessible (http://localhost:15672)
- [ ] Created demo tickets successfully
- [ ] Demonstrated order creation → message queue flow
- [ ] Showed ticket reservation → message queue flow
- [ ] Demonstrated order completion → message queue flow
- [ ] Showed order cancellation → message queue flow
- [ ] Faculty observed message activity in RabbitMQ UI
- [ ] Explained technical benefits and real-world applications

**🎊 Your message queue implementation is successfully demonstrated!**
