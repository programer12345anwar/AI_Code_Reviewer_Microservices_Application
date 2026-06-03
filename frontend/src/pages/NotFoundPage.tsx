import { Link } from "react-router-dom";
import { AppShell } from "../components/layout/AppShell";
import { Button } from "../components/ui/button";

export function NotFoundPage() {
  return (
    <AppShell>
      <div className="card-glass mx-auto max-w-xl p-10 text-center">
        <h1 className="text-3xl font-bold">404</h1>
        <p className="mt-2 text-ink-500">The page you're looking for does not exist.</p>
        <Link to="/dashboard">
          <Button className="mt-4">Go to Dashboard</Button>
        </Link>
      </div>
    </AppShell>
  );
}
