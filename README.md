# Concurrent Ride-Sharing System — Low-Level Design

## Problem Statement

Design and implement a **concurrent ride-sharing system** similar to Uber/Ola.

The system allows:

* Riders to request rides.
* Drivers to become available/unavailable.
* The system to match riders with nearby available drivers.
* Drivers to accept or reject ride requests.
* Riders to cancel rides.
* Drivers to start and complete rides.
* Multiple riders to request rides concurrently.
* Multiple drivers to receive and respond to ride requests concurrently.

Your design must be **thread-safe** and should correctly handle race conditions.

---

# Requirements

## 1. Rider

A rider should have:

```text
id
name
currentLocation
```

A rider can:

* Request a ride.
* Cancel a requested ride.
* View the status of their ride.

A rider should not be able to have more than **one active ride** at a time.

---

# 2. Driver

A driver should have:

```text
id
name
vehicle
currentLocation
status
```

Driver status can be:

```text
AVAILABLE
BUSY
OFFLINE
```

A driver can:

* Go online.
* Go offline.
* Accept a ride.
* Reject a ride.
* Start a ride.
* Complete a ride.

A driver can only have **one active ride** at a time.

---

# 3. Vehicle

Each driver has one vehicle.

A vehicle should contain:

```text
vehicleId
vehicleType
licenseNumber
```

Vehicle types:

```text
BIKE
AUTO
SEDAN
SUV
```

---

# 4. Location

Represent a location using:

```text
latitude
longitude
```

You should provide a way to calculate the distance between two locations.

For this problem, you may use a simplified Euclidean distance:

```text
distance = sqrt(
    (latitude1 - latitude2)^2 +
    (longitude1 - longitude2)^2
)
```

---

# 5. Ride Request

A rider can request a ride by providing:

```text
rider
pickupLocation
dropLocation
vehicleType
```

The system should create a ride request with a unique ID.

A ride should have the following states:

```text
REQUESTED
MATCHING
DRIVER_ASSIGNED
DRIVER_ARRIVING
RIDE_STARTED
COMPLETED
CANCELLED
```

You must ensure that invalid state transitions are not allowed.

For example:

```text
COMPLETED -> RIDE_STARTED
CANCELLED -> DRIVER_ASSIGNED
RIDE_STARTED -> CANCELLED
```

should not be possible.

---

# 6. Driver Matching

When a rider requests a ride:

1. The system should find available drivers.
2. Only drivers matching the requested vehicle type should be considered.
3. The nearest available driver should be selected.
4. The selected driver should receive the ride request.
5. The driver can accept or reject the request.

If the driver rejects the request:

* The system should try the next nearest available driver.

If no driver is available:

```text
Ride status = REQUESTED
```

and the ride should remain pending.

---

# 7. Concurrency Requirement

This is the **most important part of the problem**.

The system must support multiple concurrent requests.

For example:

```text
Rider A -> requests ride
Rider B -> requests ride

Driver D1 -> is the nearest available driver
```

Both rider requests may attempt to assign `D1` simultaneously.

Your system must guarantee that:

```text
D1 is assigned to at most ONE ride.
```

The following must never happen:

```text
Ride R1 -> Driver D1
Ride R2 -> Driver D1
```

at the same time.

---

# 8. Race Condition Scenario

Consider the following sequence:

```text
Thread-1:
    find nearest available driver D1

Thread-2:
    find nearest available driver D1

Thread-1:
    assign D1 to Ride R1

Thread-2:
    assign D1 to Ride R2
```

Design your system so that this cannot result in D1 being assigned to both rides.

You must explicitly explain:

* What data needs synchronization?
* Where will you use locks?
* What operations must be atomic?
* What happens if two threads attempt to acquire the same driver simultaneously?

---

# 9. Driver Acceptance Race Condition

Suppose a ride request is sent to multiple drivers.

For example:

```text
Ride R1
    |
    +---- Driver D1
    |
    +---- Driver D2
    |
    +---- Driver D3
```

Now:

```text
Thread-1 -> D1 accepts
Thread-2 -> D2 accepts
```

at almost exactly the same time.

Only **one driver** should ultimately be assigned to the ride.

The other driver's acceptance should fail gracefully.

For example:

```text
D1 -> ACCEPTED
D2 -> RIDE_ALREADY_ASSIGNED
```

The ride must never end up with two drivers.

---

# 10. Ride Cancellation Race Condition

Consider:

```text
Thread-1 -> Driver accepts ride
Thread-2 -> Rider cancels ride
```

These operations may happen concurrently.

Your system must guarantee a valid final state.

For example, it should not be possible for the ride to end up in:

```text
CANCELLED
+
DRIVER_ASSIGNED
```

simultaneously.

Define appropriate synchronization and state-transition rules.

---

# 11. Driver Availability Race Condition

Consider:

```text
Thread-1 -> Driver goes OFFLINE
Thread-2 -> Matching service assigns driver to a ride
```

These operations happen concurrently.

Your system must ensure that an offline driver cannot be assigned a new ride.

Similarly:

```text
Thread-1 -> Driver completes ride
Thread-2 -> Matching service searches for available drivers
```

should be handled safely.

---

# 12. Driver Request Timeout

When a driver receives a ride request, the driver has **10 seconds** to accept or reject it.

If the driver does not respond within 10 seconds:

```text
request -> TIMEOUT
```

The matching service should try the next available driver.

You should design how this timeout mechanism works.

You may use Java concurrency utilities such as:

```java
ScheduledExecutorService
ExecutorService
Future
CompletableFuture
BlockingQueue
```

where appropriate.

---

# 13. Matching Strategy

The matching algorithm should be configurable.

Create an abstraction such as:

```java
MatchingStrategy
```

Implement at least:

```text
NearestDriverStrategy
```

which selects the nearest available driver.

You may optionally implement:

```text
NearestDriverStrategy
HighestRatedDriverStrategy
LeastBusyDriverStrategy
```

The `RideService` should not be tightly coupled to a specific matching algorithm.

---

# 14. Pricing

The ride fare should be calculated when the ride is completed.

Fare depends on:

```text
baseFare
distance
duration
vehicleType
```

For example:

```text
fare =
    baseFare
    + distance * perKmRate
    + duration * perMinuteRate
```

Different vehicle types can have different pricing.

Design this using an appropriate design pattern.

For example:

```java
PricingStrategy
```

---

# 15. Payment

After completing a ride, the rider should pay the fare.

Support:

```text
CASH
CARD
UPI
```

Payment should be abstracted so that new payment methods can be added without modifying the ride service.

For example:

```java
PaymentStrategy
```

---

# 16. Payment Concurrency

A rider should not be charged twice if two threads attempt to complete/payment-process the same ride.

Consider:

```text
Thread-1 -> completeRide(R1)
Thread-2 -> completeRide(R1)
```

Your system must guarantee:

```text
Ride completed exactly once
Payment processed exactly once
```

---

# 17. Driver Location Updates

Drivers can continuously update their location.

For example:

```text
Driver D1:
    (10, 10)
    (10.2, 10.4)
    (10.5, 10.8)
    ...
```

Location updates may happen concurrently with ride matching.

For example:

```text
Thread-1 -> updateDriverLocation(D1)
Thread-2 -> findNearestDriver(...)
```

The system should safely handle concurrent reads and writes.

---

# 18. Thread Safety

The following operations may be called concurrently:

```text
requestRide()
cancelRide()
acceptRide()
rejectRide()
startRide()
completeRide()

registerDriver()
goOnline()
goOffline()

updateDriverLocation()

processPayment()
```

Your design must be thread-safe.

Avoid using a single global lock for the entire system unless you can justify why.

Prefer fine-grained synchronization where appropriate.

---

# 19. Data Structures

Choose appropriate concurrent data structures for maintaining:

```text
Drivers
Riders
Active rides
Ride requests
Available drivers
```

You may consider Java classes such as:

```java
ConcurrentHashMap
ConcurrentLinkedQueue
BlockingQueue
CopyOnWriteArrayList
AtomicInteger
AtomicReference
ReentrantLock
ReadWriteLock
StampedLock
```

You must justify your choices.

---

# 20. Thread Pool

The system should process ride requests concurrently.

Assume the system receives:

```text
1000+ ride requests per second
```

Do not create a new thread manually for every request.

Use an appropriate executor:

```java
ExecutorService
```

or a related concurrency abstraction.

Explain:

* How many thread pools you would use.
* What each pool is responsible for.
* How tasks are submitted.
* What happens when the system is overloaded.

---

# 21. Notifications

When important events occur, notify the relevant user.

Examples:

```text
Driver assigned
Driver accepted
Driver rejected
Driver arrived
Ride started
Ride completed
Ride cancelled
Payment successful
```

Support notification channels:

```text
SMS
EMAIL
PUSH
```

Use an appropriate design pattern so that new notification mechanisms can be added easily.

---

# 22. Idempotency

The following operations should be idempotent where appropriate:

```text
cancelRide()
completeRide()
processPayment()
```

For example:

```text
completeRide(R1)
completeRide(R1)
```

should not charge the rider twice.

Similarly:

```text
cancelRide(R1)
cancelRide(R1)
```

should not cause inconsistent state.

---

# 23. Failure Handling

Consider the following failures:

### Scenario 1

Driver accepts the ride but immediately goes offline.

What should happen?

---

### Scenario 2

Driver accepts the ride but the rider cancels immediately.

What should happen?

---

### Scenario 3

Payment fails after the ride has completed.

What should the ride status be?

---

### Scenario 4

No driver accepts the request after trying all available drivers.

What should happen?

---

### Scenario 5

A driver crashes/disconnects while the ride is in progress.

How would you handle this?

---

# 24. APIs

Design the following APIs:

```java
registerRider(...)
registerDriver(...)

goOnline(driverId)
goOffline(driverId)

updateDriverLocation(driverId, location)

requestRide(
    riderId,
    pickupLocation,
    dropLocation,
    vehicleType
)

acceptRide(driverId, rideId)
rejectRide(driverId, rideId)

cancelRide(riderId, rideId)

startRide(driverId, rideId)

completeRide(driverId, rideId)

processPayment(rideId, paymentMethod)

getRideStatus(rideId)
```

You may modify the APIs if you believe a better design is required.

---

# 25. Expected Classes

You should identify and design appropriate classes/interfaces.

The following are hints, not mandatory classes:

```text
RideSharingSystem

Rider
Driver
Vehicle
Location

Ride
RideRequest

DriverManager
RiderManager
RideManager

MatchingStrategy
NearestDriverStrategy

PricingStrategy
PaymentStrategy

NotificationService
NotificationStrategy

RideState
```

You are free to introduce additional classes.

---

# 26. Design Patterns

Use design patterns only where they provide value.

Potential patterns include:

```text
Strategy Pattern
Factory Pattern
Observer Pattern
State Pattern
Builder Pattern
```

You must explain why each pattern is being used.

Do not use patterns simply for the sake of using them.

---

# 27. Concurrency Constraints

Your implementation must satisfy the following invariants.

### Invariant 1 — Driver

A driver can have at most one active ride.

```text
Driver -> max 1 active Ride
```

### Invariant 2 — Rider

A rider can have at most one active ride.

```text
Rider -> max 1 active Ride
```

### Invariant 3 — Ride

A ride can have at most one driver.

```text
Ride -> max 1 Driver
```

### Invariant 4 — Payment

A ride can be paid for at most once.

```text
Ride -> max 1 successful Payment
```

### Invariant 5 — State

Ride state transitions must always be valid.

### Invariant 6 — Availability

A driver marked `BUSY` or `OFFLINE` must never be assigned a new ride.

---

# 28. Important Concurrent Scenario

Your interviewer will execute the following test:

There are:

```text
Drivers:
D1 -> 1 km away
D2 -> 2 km away
D3 -> 3 km away
```

and:

```text
Riders:
R1
R2
```

At exactly the same time:

```text
R1 requests a ride
R2 requests a ride
```

Both requests identify D1 as the nearest driver.

Your system must produce:

```text
R1 -> D1
R2 -> D2
```

or:

```text
R2 -> D1
R1 -> D2
```

but **never**:

```text
R1 -> D1
R2 -> D1
```

---

# 29. Advanced Concurrency Scenario

Consider:

```text
Ride R1 -> offered to D1
Ride R2 -> offered to D1
```

At the same time:

```text
D1 accepts R1
D1 accepts R2
```

Your system must guarantee that exactly one ride succeeds.

The other request must receive an appropriate response.

---

# 30. Deadlock Requirement

Your implementation must not introduce deadlocks.

Consider a ride transfer scenario where two resources need to be locked:

```text
Driver D1
Driver D2
```

If your design ever requires multiple locks, explain how you prevent:

```text
Thread A:
    lock(D1)
    lock(D2)

Thread B:
    lock(D2)
    lock(D1)
```

from causing a deadlock.

---

# 31. Deliverables

Implement the system in **Java**.

Your submission should contain:

### Part 1 — Class Diagram

Provide a UML-style class diagram showing:

* Classes
* Interfaces
* Relationships
* Important fields
* Important methods

---

### Part 2 — Core Classes

Implement the major domain classes:

```text
Rider
Driver
Vehicle
Location
Ride
RideRequest
```

---

### Part 3 — Services

Implement:

```text
RideService
DriverService
MatchingService
PaymentService
NotificationService
```

or an equivalent design.

---

### Part 4 — Concurrency

Demonstrate thread-safe handling of:

```text
Concurrent ride requests
Concurrent driver acceptance
Concurrent ride cancellation
Concurrent ride completion
Concurrent driver availability updates
```

---

### Part 5 — Design Patterns

Implement and demonstrate at least:

```text
Strategy
State
Observer
```

or justify alternative choices.

---

### Part 6 — Testing

Write concurrent tests for at least:

1. Two riders competing for one driver.
2. Two drivers accepting the same ride.
3. Rider cancellation racing with driver acceptance.
4. Two threads completing the same ride.
5. Two threads processing the same payment.
6. Driver going offline while matching is happening.
7. Driver location update while matching is happening.

Use Java concurrency utilities such as:

```java
CountDownLatch
CyclicBarrier
ExecutorService
Future
AtomicInteger
```

to deliberately reproduce race conditions.

---

# 32. Evaluation Criteria

Your solution will be evaluated on:

| Area                        | Weight |
| --------------------------- | -----: |
| Object-oriented design      |    15% |
| SOLID principles            |    10% |
| Design patterns             |    10% |
| Concurrency correctness     |    25% |
| Race-condition handling     |    15% |
| Thread-safe data structures |    10% |
| Error/failure handling      |     5% |
| Testability                 |     5% |
| Code quality                |     5% |

---

# 33. Bonus

For an advanced solution, support:

### Surge Pricing

Increase pricing when demand is significantly higher than available drivers.

```text
demand / availableDrivers
```

should influence the multiplier.

---

### Ride Sharing

Allow multiple riders to share a ride if their:

```text
pickup locations
drop locations
vehicle type
```

are compatible.

---

### Driver Rating

After completing a ride:

```text
Rider -> rates Driver
```

Maintain the driver's average rating.

---

### Cancellation Fees

Apply different cancellation fees depending on:

```text
Ride state
Time since request
Driver distance from pickup
```

---

### Driver Matching Radius

Instead of searching all drivers, only consider drivers within:

```text
5 km
```

of the pickup location.

---

# 34. Interviewer Follow-Up Questions

Be prepared to answer:

1. Why did you choose `ReentrantLock` over `synchronized`?
2. Where exactly is the critical section in your matching algorithm?
3. How do you atomically check and reserve a driver?
4. What happens if two riders select the same driver?
5. Can your solution deadlock?
6. How would you prevent deadlocks?
7. Why use `ConcurrentHashMap` instead of `HashMap`?
8. Why use `AtomicReference` for ride state?
9. Can a `ReadWriteLock` improve driver-location reads?
10. How would you handle 100,000 concurrent ride requests?
11. How would you partition/lock drivers to reduce contention?
12. How would you implement request timeouts?
13. How would you cancel a scheduled timeout?
14. What happens if a timeout task executes at the exact same time as driver acceptance?
15. How do you guarantee payment idempotency?
16. What happens if the application crashes after charging the rider but before updating the ride?
17. How would you make the system distributed across multiple servers?
18. Would in-memory locks still work in a distributed system?
19. How would Redis/distributed locks change your design?
20. How would you guarantee exactly-once driver assignment in a distributed environment?

---

# Final Task

> **Design and implement the complete Concurrent Ride-Sharing System in Java.**
>
> Your primary objective is **not just to make the system functional**, but to ensure that the system remains **correct under concurrent execution**.
>
> You should be able to demonstrate that no race condition can cause:
>
> * One driver to be assigned to multiple rides.
> * One ride to have multiple drivers.
> * One rider to have multiple active rides.
> * A cancelled ride to start.
> * A completed ride to be completed again.
> * A rider to be charged twice.
>
> **Assume that all APIs can be called concurrently by multiple threads.**
>
> Make reasonable assumptions where the requirements are unspecified and clearly document them.
