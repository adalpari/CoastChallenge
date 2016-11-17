package com.github.adalpari;

import java.util.*;

public class Main {

    private static int n;
    private static int m;
    private static String[] lines;
    private static Section[][] map;
    private static Queue<Section> path = new LinkedList<>();

    public static void main(String[] args) {
        long  coastLength;
        try {
            readInput();
            insertEdges();
            iterateSections();

            coastLength = calculateLength();
            System.out.println(coastLength);
        } catch (GeneralException e) {
            System.out.println("An error was detected.");
        }
    }

    private static void readInput() throws GeneralException {
        Scanner sc = new Scanner(System.in);

        parseHeader(sc);
        initializeDataContents();
        parseBody(sc);

        sc.close();
    }

    private static void setSectionOnMap(char readChar, int i, int j) throws GeneralException {
        Section section = new Section(i, j);
        if (readChar == '0') {
            section.setWater();
        } else if (readChar != '1') {
            throw new GeneralException();
        }
        map[i][j] = section;
    }

    private static void parseHeader(Scanner sc) throws GeneralException {
        if (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] headerTokens = line.split(" ");

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
        }  else {
            throw new GeneralException();
        }
    }

    private static void parseBody(Scanner sc) throws GeneralException {
        for (int i = 0; i < n; i++){
            if (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.length() == m) {
                    for (int j = 0; j < m; j++) {
                        setSectionOnMap(line.charAt(j), i, j);
                    }
                } else {
                    throw new GeneralException();
                }
            } else {
                throw new GeneralException();
            }
        }
    }

    private static void initializeDataContents() {
        lines = new String[n];
        map = new Section[n][m];
    }

    private static void insertEdges() {
        for (int i = 0; i < n; i++) {
            if (map[i][0].isWater()) {
                map[i][0].setVisited();
                path.add(map[i][0]);
            }

            if (map[i][m-1].isWater()) {
                map[i][m-1].setVisited();
                path.add(map[i][m-1]);
            }
        }

        for (int j = 0; j < m; j++) {
            if (map[0][j].isWater()) {
                map[0][j].setVisited();
                path.add(map[0][j]);
            }

            if (map[n-1][j].isWater()) {
                map[n-1][j].setVisited();
                path.add(map[n-1][j]);
            }
        }
    }

    private static void iterateSections() {
        while (!path.isEmpty()) {
            Section section = path.poll();
            visitSection(section);
        }
    }

    private static void visitSection(Section section) {
        if (section.isWater()) {
            section.setConnected();
            section.setVisited();

            addUpToQueue(section);
            addDownToQueue(section);
            addLeftToQueue(section);
            addRightToQueue(section);
        }
    }

    private static void addUpToQueue(Section currentSection) {
        int i = currentSection.getY();
        int j = currentSection.getX();
        if (i > 0) {
            addSectionToQueue(map[i - 1][j]);
        }
    }

    private static void addDownToQueue(Section currentSection) {
        int i = currentSection.getY();
        int j = currentSection.getX();
        if (i < n - 1) {
            addSectionToQueue(map[i + 1][j]);
        }
    }

    private static void addLeftToQueue(Section currentSection) {
        int i = currentSection.getY();
        int j = currentSection.getX();
        if (j > 0) {
            addSectionToQueue(map[i][j - 1]);
        }
    }

    private static void addRightToQueue(Section currentSection) {
        int i = currentSection.getY();
        int j = currentSection.getX();
        if (j < m - 1) {
            addSectionToQueue(map[i][j + 1]);
        }
    }

    private static void addSectionToQueue(Section section) {
        if (!section.isVisited()) {
            path.add(section);
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
        private int y;
        private int x;
        private boolean water;
        private boolean connected;
        private boolean visited;

        public Section(int y, int x) {
            this.y = y;
            this.x = x;
            this.water = false;
            this.connected = false;
            this.visited = false;
        }

        public boolean isWater() {
            return water;
        }

        public void setWater() {
            this.water = true;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected() {
            this.connected = true;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited() {
            this.visited = true;
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }
    }

    private static class GeneralException extends Exception {
    }
}
