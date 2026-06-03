import { Link } from "react-router-dom";
import { ReviewSummary } from "../../types/review";
import { Badge } from "../ui/badge";
import { Card } from "../ui/card";

function scoreTone(score: number): "low" | "medium" | "high" | "critical" {
  if (score < 50) return "critical";
  if (score < 70) return "high";
  if (score < 85) return "medium";
  return "low";
}

export function ReviewCard({ review }: { review: ReviewSummary }) {
  return (
    <Card className="space-y-3">
      <div className="flex items-start justify-between">
        <div>
          <p className="font-semibold">{review.repository}</p>
          <p className="text-xs text-ink-500">PR #{review.prNumber} · {review.requestId.slice(0, 8)}</p>
        </div>
        <Badge tone={scoreTone(review.qualityScore)} label={`Score ${review.qualityScore}`} />
      </div>

      <div className="flex items-center justify-between text-sm">
        <span className="text-ink-500">Model: {review.modelUsed}</span>
        <Link className="font-semibold text-ink-600 hover:underline" to={`/reviews/${review.id}`}>
          Open details
        </Link>
      </div>
    </Card>
  );
}
