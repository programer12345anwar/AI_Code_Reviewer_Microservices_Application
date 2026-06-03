import { CodeIssue } from "../../types/review";
import { Badge } from "../ui/badge";

function toneFromSeverity(severity: string): "low" | "medium" | "high" | "critical" {
  const normalized = severity.toLowerCase();
  if (normalized === "critical") return "critical";
  if (normalized === "high") return "high";
  if (normalized === "medium") return "medium";
  return "low";
}

export function IssueTable({ title, issues }: { title: string; issues: CodeIssue[] }) {
  return (
    <div className="space-y-3">
      <h4 className="font-semibold">{title}</h4>
      {issues.length === 0 ? (
        <p className="text-sm text-ink-500">No items.</p>
      ) : (
        <div className="space-y-2">
          {issues.map((issue, idx) => (
            <div key={`${title}-${idx}`} className="card-glass p-3 text-sm">
              <div className="mb-2 flex items-center gap-2">
                <Badge label={issue.severity} tone={toneFromSeverity(issue.severity)} />
                <span className="font-mono text-xs text-ink-500">Line {issue.line ?? "N/A"}</span>
              </div>
              <p className="font-medium">{issue.message}</p>
              <p className="text-ink-600 dark:text-ink-300">{issue.recommendation}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
