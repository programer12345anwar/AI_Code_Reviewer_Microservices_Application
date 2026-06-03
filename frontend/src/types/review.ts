export type Role = "ADMIN" | "USER";

export type AuthUser = {
  email: string;
  role: Role;
  token: string;
};

export type CodeIssue = {
  line: number | null;
  severity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  message: string;
  recommendation: string;
};

export type LlmReviewResult = {
  bugs: CodeIssue[];
  performanceIssues: CodeIssue[];
  securityIssues: CodeIssue[];
  suggestions: CodeIssue[];
  modelUsed: string;
  summary: string;
};

export type ReviewSummary = {
  id: number;
  requestId: string;
  repository: string;
  prNumber: number;
  qualityScore: number;
  modelUsed: string;
  createdAt: string;
};

export type ReviewDetail = {
  id: number;
  requestId: string;
  repository: string;
  prNumber: number;
  qualityScore: number;
  modelUsed: string;
  summary: string;
  normalizedDiff: string;
  resultJson: string;
  createdAt: string;
};

export type ReviewAnalytics = {
  totalReviews: number;
  avgScore: number;
  highRiskReviews: number;
  lowRiskReviews: number;
};
