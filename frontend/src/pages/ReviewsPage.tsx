import { useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { AppShell } from "../components/layout/AppShell";
import { ReviewCard } from "../components/reviews/ReviewCard";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Spinner } from "../components/ui/spinner";
import { useReviewWebSocket } from "../hooks/useReviewWebSocket";
import { api } from "../lib/api";
import { ReviewSummary } from "../types/review";

export function ReviewsPage() {
  const [reviews, setReviews] = useState<ReviewSummary[]>([]);
  const [search, setSearch] = useState("");
  const [severity, setSeverity] = useState<"ALL" | "LOW" | "MEDIUM" | "HIGH">("ALL");
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/api/v1/reviews?page=0&size=50&search=${encodeURIComponent(search)}`);
      setReviews(response.data.content || []);
    } catch {
      toast.error("Unable to load reviews");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load().catch(() => null);
  }, []);

  useReviewWebSocket(() => {
    load().catch(() => null);
    toast.success("Reviews updated in real-time");
  });

  const filtered = useMemo(() => {
    return reviews.filter((review) => {
      if (severity === "ALL") return true;
      if (severity === "HIGH") return review.qualityScore < 60;
      if (severity === "MEDIUM") return review.qualityScore >= 60 && review.qualityScore < 80;
      return review.qualityScore >= 80;
    });
  }, [reviews, severity]);

  return (
    <AppShell>
      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search repository..." className="max-w-sm" />
        <select
          className="rounded-xl border border-ink-200 bg-white px-3 py-2 text-sm dark:border-ink-700 dark:bg-ink-800"
          value={severity}
          onChange={(e) => setSeverity(e.target.value as "ALL" | "LOW" | "MEDIUM" | "HIGH")}
        >
          <option value="ALL">All Severity</option>
          <option value="HIGH">High Risk</option>
          <option value="MEDIUM">Medium Risk</option>
          <option value="LOW">Low Risk</option>
        </select>
        <Button onClick={() => load().catch(() => null)}>Search</Button>
      </div>

      {loading ? (
        <div className="flex justify-center py-10">
          <Spinner />
        </div>
      ) : (
        <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          {filtered.map((review) => (
            <ReviewCard key={review.id} review={review} />
          ))}
        </div>
      )}
    </AppShell>
  );
}
