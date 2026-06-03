import { clsx } from "clsx";
import { InputHTMLAttributes } from "react";

export function Input({ className, ...props }: InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      className={clsx(
        "w-full rounded-xl border border-ink-200 bg-white px-3 py-2 text-sm outline-none ring-0 placeholder:text-ink-400 focus:border-ink-500 dark:border-ink-700 dark:bg-ink-800",
        className
      )}
      {...props}
    />
  );
}
