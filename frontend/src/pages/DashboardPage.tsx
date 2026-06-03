import { useEffect, useMemo, useState } from "react";
import { BarChart, Bar, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { toast } from "sonner";
import { Card } from "../components/ui/card";
import { AppShell } from "../components/layout/AppShell";
import { api } from "../lib/api";
import { ReviewAnalytics, ReviewSummary } from "../types/review";
import { useAuth } from "../context/AuthContext";
import { useReviewWebSocket } from "../hooks/useReviewWebSocket";

export function DashboardPage() {
  const { user } = useAuth();
  const [analytics, setAnalytics] = useState<ReviewAnalytics | null>(null);
  const [recent, setRecent] = useState<ReviewSummary[]>([]);

  const load = async () => {
    const reviewsRes = await api.get("/api/v1/reviews?page=0&size=6");
    const recentReviews = reviewsRes.data.content || [];
    setRecent(recentReviews);

    if (user?.role === "ADMIN") {
        const analyticsRes = await api.get("/api/v1/reviews/analytics");
        setAnalytics(analyticsRes.data);
    } else {
      const avg = recentReviews.length
        ? recentReviews.reduce((sum: number, review: ReviewSummary) => sum + review.qualityScore, 0) / recentReviews.length
        : 0;
      setAnalytics({
        totalReviews: recentReviews.length,
        avgScore: avg,
        highRiskReviews: recentReviews.filter((review: ReviewSummary) => review.qualityScore < 60).length,
        lowRiskReviews: recentReviews.filter((review: ReviewSummary) => review.qualityScore >= 80).length
      });
    }
  };

  useEffect(() => {
    load().catch(() => toast.error("Failed to load dashboard"));
  }, []);

  useReviewWebSocket((id) => {
    toast.success(`New review completed #${id}`);
    load().catch(() => null);
  });

  const chartData = useMemo(
    () =>
      recent.map((item) => ({
        label: `PR-${item.prNumber}`,
        score: item.qualityScore
      })),
    [recent]
  );

  return (
    <AppShell>
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <Card>
          <p className="text-sm text-ink-500">Total Reviews</p>
          <p className="mt-2 text-3xl font-bold">{analytics?.totalReviews ?? "-"}</p>
        </Card>
        <Card>
          <p className="text-sm text-ink-500">Average Score</p>
          <p className="mt-2 text-3xl font-bold">{analytics?.avgScore?.toFixed(1) ?? "-"}</p>
        </Card>
        <Card>
          <p className="text-sm text-ink-500">High Risk Reviews</p>
          <p className="mt-2 text-3xl font-bold">{analytics?.highRiskReviews ?? "-"}</p>
        </Card>
        <Card>
          <p className="text-sm text-ink-500">Low Risk Reviews</p>
          <p className="mt-2 text-3xl font-bold">{analytics?.lowRiskReviews ?? "-"}</p>
        </Card>
      </div>

      <div className="mt-4 grid gap-4 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <h3 className="mb-4 text-sm font-semibold">Recent Quality Scores</h3>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="label" />
                <YAxis domain={[0, 100]} />
                <Tooltip />
                <Bar dataKey="score" fill="#4556d1" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Card>

        <Card>
          <h3 className="mb-2 text-sm font-semibold">Access Level</h3>
          <p className="text-sm text-ink-500">Current role: {user?.role}</p>
          <p className="mt-4 text-sm">
            {user?.role === "ADMIN"
              ? "You can access analytics and organization-wide review metrics."
              : "You can access review streams and your assigned repositories."}
          </p>
        </Card>
      </div>
    </AppShell>
  );
}
