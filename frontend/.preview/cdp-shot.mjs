import fs from "node:fs";

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const targets = await fetch("http://127.0.0.1:9223/json/list").then((r) => r.json());
const page = targets.find((t) => t.type === "page");
if (!page) throw new Error("no page target");

const ws = new WebSocket(page.webSocketDebuggerUrl);
await new Promise((resolve, reject) => {
  ws.onopen = resolve;
  ws.onerror = reject;
});

let nextId = 0;
const pending = new Map();
ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  if (message.id && pending.has(message.id)) {
    const { resolve, reject } = pending.get(message.id);
    pending.delete(message.id);
    if (message.error) reject(new Error(JSON.stringify(message.error)));
    else resolve(message.result);
  }
};

function send(method, params = {}) {
  return new Promise((resolve, reject) => {
    const id = ++nextId;
    pending.set(id, { resolve, reject });
    ws.send(JSON.stringify({ id, method, params }));
  });
}

async function evaluate(expression) {
  const result = await send("Runtime.evaluate", { expression, returnByValue: true, awaitPromise: true });
  return result.result.value;
}

await send("Page.enable");
await send("Runtime.enable");

const routes = [
  { key: "landing-desktop", path: "/", w: 1440, h: 1000 },
  { key: "landing-features", path: "/", w: 1440, h: 1000, selector: "#features" },
  { key: "landing-workflow", path: "/", w: 1440, h: 1000, selector: "#workflow" },
  { key: "landing-auth", path: "/?preview=auth", w: 1440, h: 1000 },
  { key: "tasks-auth", path: "/tasks?preview=auth", w: 1440, h: 1000 },
  { key: "mobile-landing", path: "/", w: 390, h: 844, mobile: true },
  { key: "mobile-landing-features", path: "/", w: 390, h: 844, mobile: true, selector: "#features" },
  { key: "mobile-landing-workflow", path: "/", w: 390, h: 844, mobile: true, selector: "#workflow" },
  { key: "landing-footer", path: "/", w: 1440, h: 1000, selector: "footer" },
  { key: "mobile-landing-footer", path: "/", w: 390, h: 844, mobile: true, selector: "footer" },
  { key: "tablet-landing", path: "/", w: 768, h: 1024 },
  { key: "mobile-sm-landing", path: "/", w: 320, h: 700, mobile: true },
];

const outDir = "C:/dev/task_forge/.preview/shots";

for (const route of routes) {
  await send("Emulation.setDeviceMetricsOverride", {
    width: route.w,
    height: route.h,
    deviceScaleFactor: 1,
    mobile: route.mobile ?? false,
  });
  await send("Page.navigate", { url: `http://127.0.0.1:5188${route.path}` });
  await sleep(1600);

  if (route.selector) {
    await evaluate(`(() => {
      const el = document.querySelector(${JSON.stringify(route.selector)});
      if (!el) return "missing";
      const y = el.getBoundingClientRect().top + window.scrollY - 80;
      window.scrollTo(0, y);
      return "scrolled";
    })()`);
    await sleep(400);
  }

  const state = await evaluate(
    "JSON.stringify({ path: location.pathname, h1: document.querySelector('h1')?.textContent?.trim() ?? null, width: innerWidth, scrollWidth: document.documentElement.scrollWidth })"
  );
  if (route.key === "mobile-sm-landing") {
    const overflow = await evaluate(`JSON.stringify([...document.querySelectorAll("body *")]
      .filter((el) => el.getBoundingClientRect().right > innerWidth + 0.5)
      .slice(0, 12)
      .map((el) => ({ tag: el.tagName, cls: String(el.className).slice(0, 90), right: Math.round(el.getBoundingClientRect().right), width: Math.round(el.getBoundingClientRect().width) })))`);
    console.log("OVERFLOW", overflow);
  }
  const shot = await send("Page.captureScreenshot", {
    format: "png",
    fromSurface: true,
    captureBeyondViewport: false,
  });
  fs.writeFileSync(`${outDir}/${route.key}.png`, Buffer.from(shot.data, "base64"));
  console.log("OK", route.key, state);
}

// Redirect and route assertions.
for (const test of [
  { name: "guest /tasks redirects to login", path: "/tasks", check: "location.pathname" },
  { name: "landing is default", path: "/", check: "document.querySelector('h1')?.textContent?.trim()" },
  { name: "auth landing shows workspace CTA", path: "/?preview=auth", check: "document.body.innerText.includes('Open workspace')" },
]) {
  await send("Emulation.setDeviceMetricsOverride", {
    width: 1440,
    height: 1000,
    deviceScaleFactor: 1,
    mobile: false,
  });
  await send("Page.navigate", { url: `http://127.0.0.1:5188${test.path}` });
  await sleep(1400);
  const value = await evaluate(`JSON.stringify(${test.check})`);
  console.log("TEST", test.name, "=>", value);
}

ws.close();
