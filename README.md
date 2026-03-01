# 🏪 Transaction-Logger-Sync (v1.2)

A high-performance, Java-based Command Line Interface (CLI) Point of Sale (POS) system. This tool manages local financial records with automated **GitHub Cloud Synchronization** upon exit.

---

## 🚀 Overview
The **Transaction Logger** is designed for environments where local data persistence is critical. It records sales and refunds locally to a CSV buffer to ensure zero latency during operation, and performs a bulk **Server Sync** to GitHub when the session is closed.



## ✨ Key Features
* **Dual-Action Ledger:** Process `SALE (+)` and `REFUND (-)` actions with instant validation.
* **Real-time Analytics:** Integrated "Check Balance" feature to calculate Net Cash Flow.
* **Local-First Persistence:** Data is stored in `test.csv` to protect against network instability.
* **Automated Git Sync:** Custom integration that handles `add`, `commit`, and `push` automatically on exit.
* **Clean CLI UI:** Professional ASCII-art dashboard for an intuitive user experience.

---

## 📸 System Preview

### Main Dashboard
> The central command hub for the POS system.
![Main Menu](https://via.placeholder.com/600x200?text=POS+System+Main+Dashboard+ASCII)

### Financial Summary
> Automated calculation of sales vs. refunds.
![Summary Report](https://via.placeholder.com/600x300?text=Financial+Summary+Report+Example)

---

## 🛠 Technical Stack
* **Language:** Java (JDK 17+)
* **Architecture:** Object-Oriented Programming (OOPS)
* **Data Format:** CSV (Comma Separated Values)
* **Version Control:** Git (Remote: GitHub)

---

## ⚙️ How It Works

### 1. Data Entry
Transactions are captured and appended to a local `test.csv` file using standard Java I/O.
```csv
2026-03-01 23:18:00,TXN7441,SALE,10.00,Cash
