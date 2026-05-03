/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        /* Warm Intelligence design system */
        ai: {
          bg: '#faf8f5',
          surface: '#ffffff',
          'surface-2': '#f5f3f0',
          border: '#e8e4df',
          'border-2': '#d4cdc4',
          accent: '#f59e0b',
          'accent-soft': '#fffbeb',
          'accent-border': '#fde68a',
          text: '#1a1a2e',
          'text-2': '#6b7280',
          'text-3': '#9ca3af',
          success: '#059669',
          'success-soft': '#ecfdf5',
          danger: '#dc2626',
          'danger-soft': '#fef2f2',
        },
        /* Dark mode for agent page */
        agent: {
          bg: '#0a0e1a',
          card: 'rgba(255,255,255,0.04)',
          border: 'rgba(255,255,255,0.08)',
          accent: '#06b6d4',
          accentDim: '#0891b2',
          text: '#f1f5f9',
          muted: '#64748b',
        }
      },
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'system-ui', '-apple-system', 'sans-serif'],
        display: ['Syne', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
      borderRadius: {
        '2xl': '16px',
        '3xl': '20px',
      },
      boxShadow: {
        'warm': '0 1px 3px rgba(0,0,0,0.04), 0 1px 2px rgba(245,158,11,0.02)',
        'warm-md': '0 4px 12px rgba(0,0,0,0.05), 0 2px 4px rgba(245,158,11,0.03)',
        'warm-lg': '0 8px 30px rgba(0,0,0,0.06), 0 4px 8px rgba(245,158,11,0.04)',
        'warm-xl': '0 12px 40px rgba(0,0,0,0.08)',
      },
      animation: {
        'pulse-dot': 'pulse-dot 1.4s ease-in-out infinite',
        'fade-in': 'fade-in 0.3s ease-out',
        'slide-up': 'slide-up 0.35s ease-out',
        'scale-in': 'scale-in 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'spark': 'spark 2s ease-in-out infinite',
        'float': 'float 6s ease-in-out infinite',
        'card-in': 'card-in 0.4s ease-out backwards',
      },
      keyframes: {
        'pulse-dot': {
          '0%, 80%, 100%': { opacity: '0.3', transform: 'scale(0.8)' },
          '40%': { opacity: '1', transform: 'scale(1.1)' },
        },
        'fade-in': {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        'slide-up': {
          '0%': { opacity: '0', transform: 'translateY(16px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        'scale-in': {
          '0%': { opacity: '0', transform: 'scale(0.95)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
        'spark': {
          '0%, 100%': { opacity: '0.6', transform: 'scale(1)' },
          '50%': { opacity: '1', transform: 'scale(1.05)' },
        },
        'float': {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-8px)' },
        },
        'card-in': {
          '0%': { opacity: '0', transform: 'translateY(12px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
      },
    },
  },
  plugins: [],
}
