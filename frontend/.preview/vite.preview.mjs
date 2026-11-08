import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

const user = {
  id: 1,
  firstName: "Maya",
  lastName: "Lindqvist",
  username: "maya",
  email: "maya@northwind.io",
  role: "ADMIN",
  teamId: 1,
  teamName: "Northwind Product",
  blocked: false,
};

const tasks = [
  {
    id: 1,
    title: "Redesign the project overview page",
    description: "Refresh the overview with clearer hierarchy and accessible contrast.",
    priority: "HIGH",
    status: "IN_PROGRESS",
    dueDate: "2026-11-05T23:59:59",
    createdAt: "2026-10-18T09:12:00",
    completedAt: null,
    createdBy: 1,
    createdByName: "Maya Lindqvist",
    assignees: [{ id: 1, firstName: "Maya", lastName: "Lindqvist" }],
    projectId: 1,
    projectName: "Website redesign",
  },
  {
    id: 2,
    title: "Fix focus states on the task board",
    description: "Keyboard focus disappears on the draggable cards in Safari.",
    priority: "HIGH",
    status: "TODO",
    dueDate: "2026-10-29T23:59:59",
    createdAt: "2026-10-21T14:05:00",
    completedAt: null,
    createdBy: 1,
    createdByName: "Maya Lindqvist",
    assignees: [{ id: 1, firstName: "Maya", lastName: "Lindqvist" }],
    projectId: 1,
    projectName: "Website redesign",
  },
  {
    id: 3,
    title: "Write onboarding checklist for new teammates",
    description: "Cover accounts, local setup, and the first-week rituals.",
    priority: "MEDIUM",
    status: "TODO",
    dueDate: "2026-11-12T23:59:59",
    createdAt: "2026-10-23T11:40:00",
    completedAt: null,
    createdBy: 2,
    createdByName: "Jonas Weber",
    assignees: [{ id: 1, firstName: "Maya", lastName: "Lindqvist" }],
    projectId: 1,
    projectName: "Website redesign",
  },
  {
    id: 4,
    title: "Ship dark theme design tokens",
    description: "Surfaces, borders, text, and semantic status colors are now shared.",
    priority: "LOW",
    status: "DONE",
    dueDate: "2026-10-25T23:59:59",
    createdAt: "2026-10-10T08:30:00",
    completedAt: "2026-10-24T16:20:00",
    createdBy: 1,
    createdByName: "Maya Lindqvist",
    assignees: [{ id: 1, firstName: "Maya", lastName: "Lindqvist" }],
    projectId: 1,
    projectName: "Website redesign",
  },
  {
    id: 5,
    title: "Audit empty states and error copy",
    description: "Make recovery paths obvious when a request fails.",
    priority: "MEDIUM",
    status: "IN_PROGRESS",
    dueDate: "2026-11-08T23:59:59",
    createdAt: "2026-10-25T13:15:00",
    completedAt: null,
    createdBy: 3,
    createdByName: "Priya Raman",
    assignees: [{ id: 1, firstName: "Maya", lastName: "Lindqvist" }],
    projectId: 1,
    projectName: "Website redesign",
  },
  {
    id: 6,
    title: "Set up weekly release notes",
    description: null,
    priority: "LOW",
    status: "TODO",
    dueDate: null,
    createdAt: "2026-10-28T10:00:00",
    completedAt: null,
    createdBy: 1,
    createdByName: "Maya Lindqvist",
    assignees: [{ id: 1, firstName: "Maya", lastName: "Lindqvist" }],
    projectId: 1,
    projectName: "Website redesign",
  },
];

const projects = [
  { id: 1, name: "Website redesign" },
  { id: 2, name: "Mobile app" },
  { id: 3, name: "Design system" },
];

const comments = [
  {
    id: 1,
    text: "The new sidebar reads much better. The muted dates are still a little dim on task rows.",
    createdAt: "2026-11-01T15:42:00",
    createdById: 2,
    createdByName: "Jonas Weber",
  },
];

const members = [
  user,
  {
    id: 2,
    firstName: "Jonas",
    lastName: "Weber",
    username: "jonas",
    email: "jonas@northwind.io",
    role: "USER",
    teamId: 1,
    teamName: "Northwind Product",
    blocked: false,
  },
];

function send(res, body) {
  res.statusCode = 200;
  res.setHeader("Content-Type", "application/json");
  res.end(JSON.stringify(body));
}

function mockApi() {
  return {
    name: "preview-mock-api",
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        const raw = req.url ?? "";
        if (!raw.startsWith("/api/v1/")) return next();
        const path = raw.split("?")[0].slice("/api/v1".length);
        if ((req.method ?? "GET") !== "GET") {
          res.statusCode = 204;
          res.end();
          return;
        }
        if (path === "/users/me") return send(res, user);
        if (path === "/users/me/tasks") return send(res, tasks);
        if (/^\/tasks\/\d+\/comments$/.test(path)) return send(res, comments);
        if (/^\/tasks\/\d+$/.test(path)) return send(res, tasks[0]);
        if (/^\/projects\/\d+\/tasks$/.test(path)) return send(res, tasks);
        if (/^\/projects\/\d+$/.test(path)) return send(res, projects[0]);
        if (/^\/teams\/\d+\/users$/.test(path)) return send(res, members);
        if (/^\/teams\/\d+\/projects$/.test(path)) return send(res, projects);
        if (/^\/teams\/\d+$/.test(path)) return send(res, { id: 1, name: "Northwind Product" });
        if (path === "/teams") {
          return send(res, [
            { id: 1, name: "Northwind Product" },
            { id: 2, name: "Platform squad" },
          ]);
        }
        return send(res, {});
      });
    },
    transformIndexHtml(html) {
      const bootstrap = `<script>
try {
  var auth = new URLSearchParams(location.search).get("preview") === "auth";
  if (auth) {
    localStorage.setItem("token", "preview-token");
    localStorage.setItem("user", ${JSON.stringify(JSON.stringify(user))});
  } else {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  }
} catch (e) {}
</script>`;
      return html.replace("</head>", `${bootstrap}</head>`);
    },
  };
}

export default defineConfig({
  plugins: [react(), tailwindcss(), mockApi()],
  server: {
    host: "127.0.0.1",
    port: 5188,
    strictPort: true,
  },
});
