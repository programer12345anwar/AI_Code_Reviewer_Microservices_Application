import { FormEvent, useState } from "react";
import { Send } from "lucide-react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { api } from "../../lib/api";

type ChatMessage = {
  from: "user" | "assistant";
  text: string;
};

export function AiChatAssistant({ reviewId }: { reviewId: number }) {
  const [question, setQuestion] = useState("");
  const [loading, setLoading] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([
    { from: "assistant", text: "Ask me about this review, architecture choices, or risk prioritization." }
  ]);

  const ask = async (event: FormEvent) => {
    event.preventDefault();
    if (!question.trim()) return;

    const text = question.trim();
    setMessages((prev) => [...prev, { from: "user", text }]);
    setQuestion("");
    setLoading(true);

    try {
      const response = await api.post(`/api/v1/reviews/${reviewId}/chat`, { question: text });
      setMessages((prev) => [...prev, { from: "assistant", text: response.data.answer }]);
    } catch {
      setMessages((prev) => [...prev, { from: "assistant", text: "I couldn't answer right now. Please retry." }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card-glass flex h-[420px] flex-col p-4">
      <h3 className="mb-3 text-sm font-semibold">AI Chat Assistant</h3>
      <div className="mb-3 flex-1 space-y-2 overflow-auto">
        {messages.map((message, idx) => (
          <div
            key={`${message.from}-${idx}`}
            className={`rounded-xl px-3 py-2 text-sm ${
              message.from === "assistant"
                ? "bg-ink-100 text-ink-800 dark:bg-ink-800 dark:text-ink-100"
                : "ml-auto max-w-[85%] bg-mint-100 text-ink-900"
            }`}
          >
            {message.text}
          </div>
        ))}
      </div>

      <form className="flex items-center gap-2" onSubmit={ask}>
        <Input
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="Ask about critical issues, risk, or fixes..."
        />
        <Button type="submit" disabled={loading}>
          <Send size={14} />
        </Button>
      </form>
    </div>
  );
}
