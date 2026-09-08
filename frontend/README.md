# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type-aware lint rules:

```js
export default defineConfig([
  # Frontend

  This module is the React 19, TypeScript, and Vite client for the AI Agentic Enterprise Platform. It includes routed authentication, project, team, department, document, analytics, and Nova AI views, with REST service modules for the Spring Boot backend.

  ## Commands

  ```powershell
  npm install
  npm run dev
  npm run build
  npm run lint
  ```

  The client currently uses the local backend at `http://localhost:8080/api/v1`. Backend and Ollama setup instructions are in the repository [README](../README.md). The frontend is an active product slice and is still being expanded with stronger session handling, authorization-aware navigation, richer document workflows, and durable AI conversations.
    ],
