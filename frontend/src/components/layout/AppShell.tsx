import { ReactNode } from "react";
import { Sidebar } from "./Sidebar";
import { TopBar } from "./TopBar";
import { useAuth } from "../../context/AuthContext";

export function AppShell({ children }: { children: ReactNode }) {
  const { user } = useAuth();

  return (
    <div className="min-h-screen lg:flex">
      {user && <Sidebar role={user.role} />}
      <main className="flex-1 p-4 lg:p-8">
        {user && <TopBar />}
        {children}
      </main>
    </div>
  );
}
