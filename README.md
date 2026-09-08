# Concurrent LLD Design Problem

## Problem: Concurrent Ride Booking System

### Difficulty
⭐⭐⭐⭐☆

### Topics
- Low-Level Design
- Multithreading
- Concurrency
- Race Conditions
- Locks
- Atomic Operations
- Concurrent Collections

---

## Problem Statement

Design and implement a simplified **concurrent ride booking system** similar to Uber or Ola.

The system should allow multiple users to request rides simultaneously while ensuring that a driver is never assigned to more than one active ride.

Your system must be thread-safe and should support maximum possible concurrency.

---

## Functional Requirements

### 1. Register Driver

The system should allow registering drivers.

Each driver should have:

- `driverId`
- `name`
- `location`
- `status`

Driver statuses:

```text
AVAILABLE
BUSY
OFFLINE
