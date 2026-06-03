import { createContext, useContext, useMemo, useState } from "react";
import { AuthUser, Role } from "../types/review";

type AuthContextType = {
  user: AuthUser | null;
  login: (token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function decodeJwt(token: string): { sub: string; role: Role } | null {
  try {
    const payload = token.split(".")[1];
    const json = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
    return { sub: json.sub, role: json.role };
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const token = localStorage.getItem("aicr_token");
    if (!token) {
      return null;
    }
    const decoded = decodeJwt(token);
    return decoded ? { email: decoded.sub, role: decoded.role, token } : null;
  });

  const value = useMemo<AuthContextType>(
    () => ({
      user,
      login: (token) => {
        localStorage.setItem("aicr_token", token);
        const decoded = decodeJwt(token);
        if (decoded) {
          setUser({ email: decoded.sub, role: decoded.role, token });
        }
      },
      logout: () => {
        localStorage.removeItem("aicr_token");
        setUser(null);
      }
    }),
    [user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return ctx;
}
