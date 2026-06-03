import { LogOut, Moon, Sun } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { useTheme } from "../../context/ThemeContext";
import { Button } from "../ui/button";

export function TopBar() {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <header className="card-glass mb-4 flex items-center justify-between px-4 py-3">
      <div>
        <p className="text-sm text-ink-500">Signed in as</p>
        <p className="font-semibold">{user?.email}</p>
      </div>

      <div className="flex items-center gap-2">
        <Button variant="ghost" onClick={toggleTheme} aria-label="Toggle theme">
          {theme === "dark" ? <Sun size={16} /> : <Moon size={16} />}
        </Button>
        <Button variant="ghost" onClick={logout}>
          <LogOut size={16} className="mr-1" /> Logout
        </Button>
      </div>
    </header>
  );
}
