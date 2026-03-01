package com.mrinmoy;

import com.mrinmoy.model.Refund;
import com.mrinmoy.model.Sale;
import com.mrinmoy.model.Transaction;
import com.mrinmoy.services.DataSync;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.mrinmoy.services.DataSync.FILE_NAME;
import static com.mrinmoy.services.DataSync.saveToFile;

public class Main {


    // ANSI Colors for a professional feel
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";

    private static void printFullRow(int width, String left, String right) {
        int padding = width - left.length() - right.length() - 2;
        System.out.printf("|%s%s%s|\n", left, " ".repeat(Math.max(0, padding)), right);
    }

    private static void printSplitRow(int width, String col1, String col2) {
        int half = (width / 2) - 1;
        String format = "| %-" + (half - 2) + "s | %-" + (half - 2) + "s |\n";
        System.out.printf(format, col1, col2);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            // Get current date for the header

            int WIDTH = 60; // Change this to 80 or 100 to stretch the box
            String border = "+" + "-".repeat(WIDTH - 2) + "+";

            // --- HEADER SECTION ---
            System.out.println(BLUE + border + RESET);
            printFullRow(WIDTH, " 🏪 TRANSACTION LEDGER - POS SYSTEM", "V1.2");
            printFullRow(WIDTH, " DATE: " + java.time.LocalDate.now(), "STATUS: ONLINE");
            System.out.println(BLUE + border + RESET);

            // --- MENU SECTION (2 Columns) ---
            printSplitRow(WIDTH, GREEN + "[1] NEW SALE (+)" + RESET, YELLOW + "[3] VIEW HISTORY" + RESET);
            printSplitRow(WIDTH, RED + "[2] REFUND   (-)" + RESET, YELLOW + "[4] CHECK BALANCE" + RESET);

            // --- FOOTER SECTION ---
            System.out.println(BLUE + border + RESET);
            printFullRow(WIDTH, " [5] SERVER SYNC & EXIT", "");
            System.out.println(BLUE + border + RESET);
            System.out.print(BOLD + " ACTION > " + RESET);

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    handleEntry(sc, "SALE");
                    break;
                case 2:
                    handleEntry(sc, "REFUND");
                    break;
                case 3:
                    showRecent();
                    break;
                case 4:
                    showBalance();
                    break;
                case 5:
                    System.out.println("SERVER SYNC & EXIT");
                    dataSync();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void dataSync() {
        File csvFile = new File(FILE_NAME);
        if (!csvFile.exists()) {
            System.out.println("File not found.");
            return;
        }

        try {
            // 1. Read all lines from the CSV
            List<String> lines = Files.readAllLines(csvFile.toPath());

            // 2. Perform your Upload/Append logic here
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    // Example: uploadToDatabase(line);
                    DataSync dataSync = new DataSync();
                    dataSync.uploadTransaction(line);
                    System.out.println("Syncing record: " + line);
                }
            }

            // 3. Clear the file content
            // Opening a FileWriter with append=false (default) wipes the file
            new FileWriter(csvFile, false).close();

            System.out.println("Data synced and test.csv cleared successfully.");

        } catch (IOException e) {
            System.err.println("An error occurred during data sync: " + e.getMessage());
        }
    }

    private static void showBalance() {
        int width = 60; // Matches your Main menu width
        String border = "+" + "=".repeat(width - 2) + "+";

        double totalSales = 0;
        double totalRefunds = 0;
        int saleCount = 0;
        int refundCount = 0;

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println(RED + "\n  ⚠ No ledger found. Balance is ₹0.00" + RESET);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                double amount = Double.parseDouble(data[3]);

                if (data[2].equalsIgnoreCase("SALE")) {
                    totalSales += amount;
                    saleCount++;
                } else {
                    totalRefunds += amount;
                    refundCount++;
                }
            }

            double netBalance = totalSales - totalRefunds;
            // --- DISPLAY THE BALANCE BOX ---
            System.out.println("\n" + BLUE + border + RESET);
            System.out.printf(BLUE + "| %-57s |\n" + RESET, BOLD + " 💰 FINANCIAL SUMMARY REPORT" + RESET);
            System.out.println(BLUE + border + RESET);
            // Row 1: Total Sales
            System.out.printf("| Total Sales   (%d entries) %" + (width - 32) + "s " + GREEN + "₹%10.2f" + RESET + " |\n",
                    saleCount, "", totalSales);
            // Row 2: Total Refunds
            System.out.printf("| Total Refunds (%d entries) %" + (width - 32) + "s " + RED + "₹%10.2f" + RESET + " |\n",
                    refundCount, "", totalRefunds);
            System.out.println("|" + "-".repeat(width - 2) + "|");
            // Row 3: Net Balance (The most important part)
            String balanceColor = (netBalance >= 0) ? GREEN : RED;
            System.out.printf("| " + BOLD + "NET CASH BALANCE %" + (width - 38) + "s " + balanceColor + "₹%12.2f" + RESET + "  |\n",
                    "", netBalance);
            System.out.println(BLUE + border + RESET);

        } catch (Exception e) {
            System.out.println(RED + "Error calculating balance: " + e.getMessage() + RESET);
        }
    }

    // Helper to collect user input and save it
    private static void handleEntry(Scanner sc, String type) {
        System.out.print("Enter Amount: ");
        double amt = sc.nextDouble();
        System.out.print("Payment Method (Cash/Online): ");
        String method = sc.next();

        // Generate a random ID for the transaction
        String id = "TXN" + (int) (Math.random() * 9000 + 1000);

        Transaction txn = type.equals("SALE") ? new Sale(id, amt, method) : new Refund(id, amt, method);
        saveToFile(txn.toCsv());
    }


    private static void showRecent() {
        int width = 60; // Must match your Main menu width
        String border = "+" + "-".repeat(width - 2) + "+";
        List<String> logs = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println(RED + "  ⚠ No records found in ledger.csv" + RESET);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }

            // --- TABLE HEADER ---
            System.out.println("\n" + BLUE + border + RESET);
            System.out.printf(BLUE + "| %-57s |\n" + RESET, BOLD + " 📜 RECENT TRANSACTION HISTORY (LAST 5)" + RESET);
            System.out.println(BLUE + border + RESET);
            System.out.printf("| %-19s | %-8s | %-10s | %-10s |\n", "TIMESTAMP", "TYPE", "AMOUNT", "METHOD");
            System.out.println("|" + "-".repeat(width - 2) + "|");

            // --- TABLE DATA ---
            int start = Math.max(0, logs.size() - 5);
            for (int i = start; i < logs.size(); i++) {
                String[] data = logs.get(i).split(",");

                // Apply colors based on transaction type
                String typeColor = data[2].equalsIgnoreCase("SALE") ? GREEN : RED;

                System.out.printf("| %-19s | " + typeColor + "%-8s" + RESET + " | ₹%-9.2f | %-10s |\n",
                        data[0],          // Timestamp
                        data[2],          // Type (SALE/REFUND)
                        Double.parseDouble(data[3]), // Amount
                        data[4]           // Method
                );
            }
            System.out.println(BLUE + border + RESET);

        } catch (IOException e) {
            System.out.println(RED + "Error reading the ledger file." + RESET);
        }
    }
}