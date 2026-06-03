package com.aicodereviewer.github.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class DiffExtractionService {

    private static final Pattern HUNK_PATTERN = Pattern.compile("@@ -(\\d+)(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@");

    public String normalize(List<String> patches) {
        StringBuilder normalized = new StringBuilder();
        for (String patch : patches) {
            normalized.append(normalizePatch(patch)).append("\n");
        }
        return normalized.toString().trim();
    }

    private String normalizePatch(String patch) {
        if (patch == null || patch.isBlank()) {
            return "";
        }

        List<String> output = new ArrayList<>();
        int oldLine = 0;
        int newLine = 0;

        for (String line : patch.split("\\n")) {
            if (line.startsWith("@@")) {
                Matcher matcher = HUNK_PATTERN.matcher(line);
                if (matcher.find()) {
                    oldLine = Integer.parseInt(matcher.group(1));
                    newLine = Integer.parseInt(matcher.group(2));
                }
                continue;
            }

            if (line.startsWith("+") && !line.startsWith("+++")) {
                output.add("+L" + newLine + ": " + line.substring(1));
                newLine++;
                continue;
            }
            if (line.startsWith("-") && !line.startsWith("---")) {
                output.add("-L" + oldLine + ": " + line.substring(1));
                oldLine++;
                continue;
            }

            if (!line.startsWith("\\")) {
                oldLine++;
                newLine++;
            }
        }

        return String.join("\n", output);
    }
}
