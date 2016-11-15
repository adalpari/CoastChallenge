package com.github.adalpari;

import java.util.Scanner;

public class Main {

    private static int n;
    private static int m;
    private static String[] lines;
    private static Section[][] map;

    public static void main(String[] args) {
        try {
            readInput();
            fillBoard();

            long  coastLength = calculateLength();
            System.out.println(coastLength);
        } catch (GeneralException e) {
            System.out.println("An error was detected.");
        }
    }

    private static void readInput() throws GeneralException {
        Scanner sc = new Scanner(System.in);

        if (sc.hasNextLine()) {
            parseHeader(sc.nextLine());
        }

        initializeDataContents();

        for (int i = 0; i < n; i++){
            if (sc.hasNextLine()) {
                lines[i] = sc.nextLine();
                if (lines[i].length() != m) {
                    throw new GeneralException();
                }
            } else {
                throw new GeneralException();
            }
        }

        sc.close();
    }

    private static void parseHeader(String header) throws GeneralException {
        String[] headerTokens = header.split(" ");

        if (headerTokens.length == 2) {
            try {
                n = Integer.parseInt(headerTokens[0]);
                m = Integer.parseInt(headerTokens[1]);
            } catch (NumberFormatException e) {
                throw new GeneralException();
            }
        } else {
            throw new GeneralException();
        }
    }

    private static void initializeDataContents() {
        lines = new String[n];
        map = new Section[n][m];
    }

    private static void fillBoard() throws GeneralException {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                setSectionOnMap(lines[i].charAt(j), i, j);
                // first check of connected water
                setConnectedUp(i, j);
            }
        }

        //reverse lookup for setConnected
        for (int i = n - 1; i >= 0; i--) {
            for (int j = m -1; j >= 0; j--) {
                setConnectedDown(i, j);
            }
        }
    }

    private static void setSectionOnMap(char readChar, int i, int j) throws GeneralException {
        Section section = new Section();
        if (readChar == '0') {
            section.setWater(true);
        } else if (readChar != '1') {
            throw new GeneralException();
        }
        map[i][j] = section;
    }

    private static void setConnectedUp(int i, int j) {
        Section section = new Section();
        if (section.isWater()) {
            if (i == 0 || j == 0 || i == n - 1 || j == m - 1) {
                map[i][j].setConnected(true);
            } else if (map[i - 1][j].isConnected() || map[i][j - 1].isConnected()) {
                map[i][j].setConnected(true);
            }
        }
    }

    private static void setConnectedDown(int i, int j) {
        Section section = map[i][j];
        if (section.isWater() && !section.isConnected()) {
            if (i == 0 || j == 0 || i == n - 1 || j == m - 1) {
                section.setConnected(true);
            } else if (map[i + 1][j].isConnected() || map[i][j + 1].isConnected()) {
                section.setConnected(true);
            }
        }
    }

    private static long calculateLength() {
        long coastLength = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                coastLength += getCoastLength(i, j);
            }
        }

        return coastLength;
    }

    private static long getCoastLength(int i, int j) {
        long coastLength = 0;
        Section section = map[i][j];

        if (!section.isWater()) {
            coastLength = getCoastLengthDown(i, j)
                    + getCoastLengthLeft(i, j)
                    + getCoastLengthRight(i, j)
                    + getCoastLengthUp(i, j);
        }

        return coastLength;
    }

    private static long getCoastLengthUp(int i, int j) {
        long coastLength = 0;

        if (i == 0) {
            coastLength++;
        } else {
            Section up = map[i-1][j];
            if (up.isWater() && up.isConnected()) {
                coastLength++;
            }
        }

        return coastLength;
    }

    private static long getCoastLengthDown(int i, int j) {
        long coastLength = 0;

        if (i == n - 1) {
            coastLength++;
        } else {
            Section down = map[i+1][j];
            if (down.isWater() && down.isConnected()) {
                coastLength++;
            }
        }

        return coastLength;
    }

    private static long getCoastLengthLeft(int i, int j) {
        long coastLength = 0;

        if (j == 0) {
            coastLength++;
        } else {
            Section left = map[i][j-1];
            if (left.isWater() && left.isConnected()) {
                coastLength++;
            }
        }

        return coastLength;
    }

    private static long getCoastLengthRight(int i, int j) {
        long coastLength = 0;

        if (j == m - 1) {
            coastLength++;
        } else {
            Section right = map[i][j+1];
            if (right.isWater() && right.isConnected()) {
                coastLength++;
            }
        }

        return coastLength;
    }

    private static class Section {
        private boolean water;
        private boolean connected;

        public Section() {
            this.water = false;
            this.connected = false;
        }

        public boolean isWater() {
            return water;
        }

        public void setWater(boolean water) {
            this.water = water;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }
    }

    private static class GeneralException extends Exception {
    }
}
