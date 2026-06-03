import { clsx } from "clsx";
import { ButtonHTMLAttributes } from "react";

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "secondary" | "ghost" | "danger";
};

export function Button({ variant = "primary", className, ...props }: Props) {
  return (
    <button
      className={clsx(
        "inline-flex items-center justify-center rounded-xl px-4 py-2 text-sm font-semibold transition-all disabled:opacity-60",
        variant === "primary" && "bg-ink-600 text-white hover:bg-ink-700",
        variant === "secondary" && "bg-mint-500 text-ink-900 hover:bg-mint-300",
        variant === "ghost" && "bg-transparent text-ink-600 hover:bg-ink-100 dark:text-ink-200 dark:hover:bg-ink-700",
        variant === "danger" && "bg-red-600 text-white hover:bg-red-700",
        className
      )}
      {...props}
    />
  );
}
