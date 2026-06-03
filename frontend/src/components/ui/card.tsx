import { PropsWithChildren } from "react";
import { clsx } from "clsx";

export function Card({ children, className }: PropsWithChildren<{ className?: string }>) {
  return <div className={clsx("card-glass p-5", className)}>{children}</div>;
}
