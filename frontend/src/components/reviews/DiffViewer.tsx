import Editor from "@monaco-editor/react";
import { Card } from "../ui/card";

export function DiffViewer({ normalizedDiff }: { normalizedDiff: string }) {
  const lines = normalizedDiff.split("\n");

  return (
    <Card className="space-y-4">
      <h3 className="text-sm font-semibold">GitHub-Style Diff</h3>

      <div className="overflow-hidden rounded-xl border border-ink-200 dark:border-ink-700">
        <div className="max-h-64 overflow-auto bg-white dark:bg-ink-950">
          {lines.map((line, index) => (
            <div
              key={`${line}-${index}`}
              className={`px-3 py-1 font-mono text-xs ${
                line.startsWith("+")
                  ? "bg-emerald-50 text-emerald-900 dark:bg-emerald-900/30 dark:text-emerald-200"
                  : line.startsWith("-")
                    ? "bg-red-50 text-red-900 dark:bg-red-900/20 dark:text-red-200"
                    : "text-ink-700 dark:text-ink-300"
              }`}
            >
              {line}
            </div>
          ))}
        </div>
      </div>

      <Editor
        height="260px"
        language="diff"
        value={normalizedDiff}
        theme="vs-dark"
        options={{ readOnly: true, minimap: { enabled: false }, fontSize: 12 }}
      />
    </Card>
  );
}
