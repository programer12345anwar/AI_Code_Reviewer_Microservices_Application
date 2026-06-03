import { clsx } from "clsx";

type Props = {
  label: string;
  tone?: "low" | "medium" | "high" | "critical";
};

export function Badge({ label, tone = "medium" }: Props) {
  return (
    <span
      className={clsx(
        "inline-flex rounded-full px-2.5 py-1 text-xs font-semibold",
        tone === "low" && "bg-emerald-100 text-emerald-800",
        tone === "medium" && "bg-amber-100 text-amber-900",
        tone === "high" && "bg-orange-100 text-orange-900",
        tone === "critical" && "bg-red-100 text-red-800"
      )}
    >
      {label}
    </span>
  );
}
