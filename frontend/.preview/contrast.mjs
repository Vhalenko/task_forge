const colors = {
  "ink-950": "#07090d",
  "ink-900": "#0e1117",
  "ink-850": "#12161e",
  "ink-800": "#171c26",
  "ink-400": "#9aa5b7",
  "ink-300": "#c4ccd9",
  "ink-200": "#e2e7ef",
  "ink-100": "#f2f5f9",
  accent: "#9eea63",
  "rose-300": "#fda4af",
  "rose-200": "#fecdd3",
  "sky-200": "#bae6fd",
  "emerald-200": "#a7f3d0",
  "amber-200": "#fde68a",
  "sky-400": "#38bdf8",
  "emerald-400": "#34d399",
  "rose-400": "#fb7185",
  "amber-400": "#fbbf24",
};

const hexToRgb = (hex) => {
  const value = hex.replace("#", "");
  return [0, 2, 4].map((i) => parseInt(value.slice(i, i + 2), 16));
};

const luminance = (hex) => {
  const [r, g, b] = hexToRgb(hex).map((v) => {
    const channel = v / 255;
    return channel <= 0.03928 ? channel / 12.92 : ((channel + 0.055) / 1.055) ** 2.4;
  });
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
};

const ratio = (fg, bg) => {
  const a = luminance(fg);
  const b = luminance(bg);
  const [light, dark] = a > b ? [a, b] : [b, a];
  return (light + 0.05) / (dark + 0.05);
};

const blend = (fg, alpha, bg) => {
  const f = hexToRgb(fg);
  const b = hexToRgb(bg);
  const out = f.map((c, i) => Math.round(c * alpha + b[i] * (1 - alpha)));
  return "#" + out.map((c) => c.toString(16).padStart(2, "0")).join("");
};

const checks = [
  ["Heading white on page", colors["ink-100"], colors["ink-950"]],
  ["Body text on page", colors["ink-200"], colors["ink-950"]],
  ["Body text on card", colors["ink-200"], colors["ink-900"]],
  ["Secondary text on card", colors["ink-300"], colors["ink-900"]],
  ["Muted text on page", colors["ink-400"], colors["ink-950"]],
  ["Muted text on card", colors["ink-400"], colors["ink-900"]],
  ["Muted text on raised card", colors["ink-400"], colors["ink-850"]],
  ["Muted text on field", colors["ink-400"], colors["ink-800"]],
  ["Accent text on page", colors.accent, colors["ink-950"]],
  ["Accent text on card", colors.accent, colors["ink-900"]],
  ["Dark text on accent button", colors["ink-950"], colors.accent],
  ["Error text on card", colors["rose-300"], colors["ink-900"]],
  ["Error text on raised card", colors["rose-300"], colors["ink-800"]],
  ["Status To do text", colors["ink-200"], blend("#ffffff", 0.06, colors["ink-900"])],
  ["Status In progress text", colors["sky-200"], blend(colors["sky-400"], 0.1, colors["ink-900"])],
  ["Status Done text", colors["emerald-200"], blend(colors["emerald-400"], 0.1, colors["ink-900"])],
  ["Priority High text", colors["rose-200"], blend(colors["rose-400"], 0.1, colors["ink-800"])],
  ["Priority Medium text", colors["amber-200"], blend(colors["amber-400"], 0.1, colors["ink-800"])],
  ["Board Low chip text", colors["ink-200"], blend("#ffffff", 0.06, colors["ink-800"])],
];

let failures = 0;
for (const [label, fg, bg] of checks) {
  const value = ratio(fg, bg);
  const pass = value >= 4.5;
  if (!pass) failures += 1;
  console.log(`${pass ? "PASS" : "FAIL"}  ${value.toFixed(2).padStart(5)}:1  ${label}  (${fg} on ${bg})`);
}
console.log(`\n${checks.length - failures}/${checks.length} pass AA for normal text (4.5:1).`);
