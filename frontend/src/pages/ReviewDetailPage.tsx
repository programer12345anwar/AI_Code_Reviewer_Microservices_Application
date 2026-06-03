import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { toast } from "sonner";
import { AiChatAssistant } from "../components/chat/AiChatAssistant";
import { AppShell } from "../components/layout/AppShell";
import { DiffViewer } from "../components/reviews/DiffViewer";
import { IssueTable } from "../components/reviews/IssueTable";
import { Card } from "../components/ui/card";
import { Spinner } from "../components/ui/spinner";
import { api } from "../lib/api";
import { LlmReviewResult, ReviewDetail } from "../types/review";

export function ReviewDetailPage() {
  const { id } = useParams();
  const [review, setReview] = useState<ReviewDetail | null>(null);
  const [result, setResult] = useState<LlmReviewResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    api
      .get(`/api/v1/reviews/${id}`)
      .then((response) => {
        setReview(response.data);
        setResult(JSON.parse(response.data.resultJson));
      })
      .catch(() => toast.error("Unable to load review detail"))
      .finally(() => setLoading(false));
  }, [id]);

  const totalIssues = useMemo(() => {
    if (!result) return 0;
    return result.bugs.length + result.performanceIssues.length + result.securityIssues.length;
  }, [result]);

  if (loading) {
    return (
      <AppShell>
        <div className="flex justify-center py-12"><Spinner /></div>
      </AppShell>
    );
  }

  if (!review || !result) {
    return (
      <AppShell>
        <Card>Review not found.</Card>
      </AppShell>
    );
  }

  return (
    <AppShell>
      <div className="grid gap-4 xl:grid-cols-3">
        <div className="space-y-4 xl:col-span-2">
          <Card>
            <h2 className="text-xl font-bold">{review.repository} · PR #{review.prNumber}</h2>
            <p className="mt-2 text-sm text-ink-500">Quality Score: {review.qualityScore}/100 · Model: {review.modelUsed}</p>
            <p className="mt-3 text-sm">{review.summary}</p>
            <p className="mt-2 text-sm text-ink-500">Total actionable issues: {totalIssues}</p>
          </Card>

          <DiffViewer normalizedDiff={review.normalizedDiff} />

          <div className="grid gap-3 md:grid-cols-2">
            <IssueTable title="Bugs" issues={result.bugs} />
            <IssueTable title="Performance Issues" issues={result.performanceIssues} />
            <IssueTable title="Security Issues" issues={result.securityIssues} />
            <IssueTable title="Suggestions" issues={result.suggestions} />
          </div>
        </div>

        <AiChatAssistant reviewId={review.id} />
      </div>
    </AppShell>
  );
}
