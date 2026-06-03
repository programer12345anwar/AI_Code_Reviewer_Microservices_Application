import { Link, useLocation } from "react-router-dom";
import { clsx } from "clsx";
import { BarChart3, ListChecks } from "lucide-react";

const links = [
  { to: "/dashboard", label: "Dashboard", icon: BarChart3 },
  { to: "/reviews", label: "Reviews", icon: ListChecks }
];

export function Sidebar({ role }: { role: "ADMIN" | "USER" }) {
  const location = useLocation();

  return (
    <aside className="hidden w-64 flex-col gap-6 border-r border-ink-200/70 bg-white/80 p-6 dark:border-ink-800 dark:bg-ink-900/70 lg:flex">
      <div>
        <h1 className="text-lg font-bold">AI Code Reviewer</h1>
        <p className="text-xs text-ink-500">Realtime PR quality intelligence</p>
      </div>

      <nav className="flex flex-col gap-2">
        {links
          .map((link) => {
            const Icon = link.icon;
            return (
              <Link
                key={link.to}
                to={link.to}
                className={clsx(
                  "flex items-center gap-2 rounded-xl px-3 py-2 text-sm transition-colors",
                  location.pathname.startsWith(link.to)
                    ? "bg-ink-600 text-white"
                    : "text-ink-600 hover:bg-ink-100 dark:text-ink-300 dark:hover:bg-ink-800"
                )}
              >
                <Icon size={16} />
                {link.label}
              </Link>
            );
          })}
      </nav>
    </aside>
  );
}
