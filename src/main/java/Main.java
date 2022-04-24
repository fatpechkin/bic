import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String inputFile = "lng.csv";
        String outputFile = "output.txt";
        List<String> lines = readInput(inputFile).stream().toList();
        if (lines == null) {
            return;
        }
        List<List<String>> result = processLines(lines);
        result = result.stream()
                .filter(l -> l.size() > 1)
                .sorted((o1, o2) -> o2.size() - o1.size())
                .collect(Collectors.toList());
        writeOutput(outputFile, resultToString(result));
    }

    private static class Dot {
        private final String val;
        private final int index;

        public Dot(String val, int index) {
            this.val = val;
            this.index = index;
        }
    }

    private static HashSet<String> readInput(String inputFile) {
        HashSet<String> localLines = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line = br.readLine();
            while (line != null) {
                localLines.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localLines;
    }

    private static List<List<String>> processLines(List<String> lines) {

        List<List<String>> groups = new ArrayList<>();
        if (lines.size() < 2) {
            groups.add(lines);
            return groups;
        }

        List<Map<String, Integer>> dotMap = new ArrayList<>();
        Map<Integer, Integer> crossMap = new HashMap<>();
        for (String line : lines) {
            String[] lineDots = line.split(";");
            TreeSet<Integer> matchingGroups = new TreeSet<>();
            List<Dot> dots = new ArrayList<>();

            for (int i = 0; i < lineDots.length; i++) {
                String dot = lineDots[i];
                if (dotMap.size() == i)
                    dotMap.add(new HashMap<>());
                if ("".equals(dot.replaceAll("\"","").trim()))
                    continue;

                Map<String, Integer> currentIndex = dotMap.get(i);
                Integer dotGroupNumber = currentIndex.get(dot);
                if (dotGroupNumber != null) {
                    while (crossMap.containsKey(dotGroupNumber))
                        dotGroupNumber = crossMap.get(dotGroupNumber);
                    matchingGroups.add(dotGroupNumber);
                } else {
                    dots.add(new Dot(dot, i));
                }
            }
            int groupNumber;
            if (matchingGroups.isEmpty()) {
                groups.add(new ArrayList<>());
                groupNumber = groups.size() - 1;
            } else {
                groupNumber = matchingGroups.first();
            }
            for (Dot dot : dots) {
                dotMap.get(dot.index).put(dot.val, groupNumber);
            }
            for (int matchedGrNum : matchingGroups) {
                if (matchedGrNum != groupNumber) {
                    crossMap.put(matchedGrNum, groupNumber);
                    groups.get(groupNumber).addAll(groups.get(matchedGrNum));
                    groups.set(matchedGrNum, null);
                }
            }
            groups.get(groupNumber).add(line);
        }
        groups.removeAll(Collections.singleton(null));
        return groups;
    }

    private static String resultToString(List<List<String>> result) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (List<String> list :
                result) {
            if (list.size() > 1) {
                sb.append("Group " + i + "\n");
                for (String s :
                        list) {
                    sb.append(String.join(";", s) + "\n");
                }
                i++;
            }
        }
        return sb.toString();
    }

    private static void writeOutput(String outputFile, String content) {
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            writer.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
