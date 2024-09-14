import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Lab4 {
    static class HashEntry {
        String key;
        int initialHashAddress;
        int numberOfProbes;

        HashEntry(String key, int initialHashAddress) {
            this.key = key;
            this.initialHashAddress = initialHashAddress;
            this.numberOfProbes = 1; // Initialize with 1 for the initial probe
        }
    }

    // Function to calculate custom hash HA
    static int customHash(String str) {
        int ha = (str.charAt(0) * 256 + str.charAt(2)) / 1024 +
                str.charAt(4) / 313 + str.charAt(5) / 3 + str.charAt(9);
        return ha % 128 + 1;
    }

    // Function to insert an entry into the hash table using linear probing
    static void insertEntry(HashEntry[] table, HashEntry entry) {
        int currentAddress = entry.initialHashAddress - 1;

        while (table[currentAddress] != null) {
            entry.numberOfProbes++;
            currentAddress = (currentAddress + 1) % 128;
        }

        table[currentAddress] = entry;
    }

    // Function to lookup an entry in the hash table and return the number of probes
    static int lookupEntry(HashEntry[] table, String key) {
        int initialHashAddress = customHash(key);
        int currentAddress = initialHashAddress - 1;
        int probes = 1;

        while (table[currentAddress] != null) {
            if (table[currentAddress].key.equals(key)) {
                return probes;
            }
            probes++;
            currentAddress = (currentAddress + 1) % 128;
        }

        return -1; // Key not found
    }

    public static void main(String[] args) {
        HashEntry[] hashTable = new HashEntry[128];

        // Read keys from the file and insert into the hash table
        try (BufferedReader br = new BufferedReader(new FileReader("path/to/your/file.txt"))) {
            String line;
            int keysInserted = 0;
            while ((line = br.readLine()) != null && keysInserted < 110) {
                // Pad the key with spaces to make it 16 characters long and left-justify
                String paddedKey = String.format("%-16s", line);
                int initialHashAddress = customHash(paddedKey);
                HashEntry entry = new HashEntry(paddedKey, initialHashAddress);
                insertEntry(hashTable, entry);
                keysInserted++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Lookup the first 25 keys
        int[] probesFirst25 = new int[25];
        for (int i = 0; i < 25; i++) {
            probesFirst25[i] = lookupEntry(hashTable, hashTable[i].key);
        }

        // Lookup the last 25 keys
        int[] probesLast25 = new int[25];
        for (int i = hashTable.length - 25, j = 0; i < hashTable.length; i++, j++) {
            probesLast25[j] = lookupEntry(hashTable, hashTable[i].key);
        }

        // Print results and hash table contents
        System.out.println("Probes for first 25 keys:");
        printProbesStats(probesFirst25);

        System.out.println("\nProbes for last 25 keys:");
        printProbesStats(probesLast25);

        System.out.println("\nHash Table Contents:");
        printHashTable(hashTable);

        // Calculate and print the theoretical expected number of probes
        double theoreticalProbes = calculateTheoreticalProbes(hashTable);
        System.out.println("\nTheoretical Expected Number of Probes: " + theoreticalProbes);
    }

    // Function to print statistics for probes
    static void printProbesStats(int[] probes) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int total = 0;

        for (int probe : probes) {
            if (probe != -1) {
                min = Math.min(min, probe);
                max = Math.max(max, probe);
                total += probe;
            }
        }

        double average = (double) total / probes.length;

        System.out.println("Minimum probes: " + min);
        System.out.println("Maximum probes: " + max);
        System.out.println("Average probes: " + average);
    }

    // Function to print the hash table contents
    static void printHashTable(HashEntry[] table) {
        for (int i = 0; i < table.length; i++) {
            HashEntry entry = table[i];
            System.out.printf("%3d: ", i + 1);
            if (entry != null) {
                System.out.printf("Key: %-16s | Initial Hash Address: %3d | Probes: %2d\n", entry.key, entry.initialHashAddress, entry.numberOfProbes);
            } else {
                System.out.println("Empty");
            }
        }
    }

    // Function to calculate the theoretical expected number of probes
    static double calculateTheoreticalProbes(HashEntry[] table) {
        int occupiedSlots = 0;
        int totalProbes = 0;

        for (HashEntry entry : table) {
            if (entry != null) {
                occupiedSlots++;
                totalProbes += entry.numberOfProbes;
            }
        }

        double loadFactor = (double) occupiedSlots / table.length;
        double theoreticalProbes = loadFactor * (1 + 1 / (1 - loadFactor));

        return theoreticalProbes;
    }
}