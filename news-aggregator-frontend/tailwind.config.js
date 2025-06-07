/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}", // This tells Tailwind to look for classes in all JS, TS, JSX, TSX files in the src directory
  ],
  theme: {
    extend: {
      // Define a custom color palette for your theme (optional but recommended for "eye-charming")
      colors: {
        primary: '#4F46E5', // A vibrant indigo for primary actions/branding
        secondary: '#6B7280', // A subtle gray for secondary text/borders
        accent: '#EF4444', // A red for highlights or warnings
        background: '#F9FAFB', // Light gray background
        card: '#FFFFFF', // White for cards
        textPrimary: '#1F2937', // Dark text
        textSecondary: '#4B5563', // Lighter text
      },
      // You can also extend typography, spacing, etc.
      fontFamily: {
        sans: ['Inter', 'sans-serif'], // Or any other preferred font
      },
    },
  },
  plugins: [],
}