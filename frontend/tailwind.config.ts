import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: ["class"],
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        ink: {
          50: "#f6f8ff",
          100: "#eceffd",
          200: "#d8ddfb",
          300: "#b5c0f6",
          400: "#8998ef",
          500: "#5f72e6",
          600: "#4556d1",
          700: "#3844a9",
          800: "#313c86",
          900: "#1f2552"
        },
        mint: {
          100: "#d9fff2",
          300: "#79f5ca",
          500: "#11bf8a"
        }
      },
      boxShadow: {
        panel: "0 10px 35px rgba(26, 35, 95, 0.12)"
      },
      fontFamily: {
        sans: ["'Space Grotesk'", "'Segoe UI'", "sans-serif"],
        mono: ["'IBM Plex Mono'", "monospace"]
      },
      backgroundImage: {
        halo: "radial-gradient(circle at 20% 20%, rgba(17,191,138,0.2) 0%, transparent 45%), radial-gradient(circle at 90% 5%, rgba(95,114,230,0.22) 0%, transparent 38%)"
      }
    }
  },
  plugins: []
};

export default config;
