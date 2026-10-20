# Draws the Task C walkthrough figures used in the slide deck. Run: python3 docs/figures/task-c/make_figures.py <output-dir>
"""Draws the Task C walkthrough figures (median-of-medians selection) as SVG files."""
import os
import sys

OUT = sys.argv[1]
os.makedirs(OUT, exist_ok=True)

W, H = 960, 560
INK, MUTED, FAINT = "#1f2937", "#6b7280", "#d1d5db"
BLUE, ORANGE, GREEN, PURPLE, RED = "#2563eb", "#ea580c", "#16a34a", "#7c3aed", "#dc2626"
FILL = {"plain": ("#ffffff", INK), "smaller": ("#dbeafe", BLUE), "same": ("#ede9fe", PURPLE),
        "larger": ("#ffedd5", ORANGE), "median": ("#fef3c7", "#d97706"), "answer": ("#dcfce7", GREEN),
        "done": ("#f3f4f6", FAINT)}
FONT = 'font-family="Helvetica, Arial, sans-serif"'


def svg(body):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}" {FONT}>'
            f'<rect width="{W}" height="{H}" fill="#ffffff"/>{body}</svg>')


def text(x, y, s, size=22, color=INK, anchor="middle", weight="normal"):
    return (f'<text x="{x}" y="{y}" font-size="{size}" fill="{color}" text-anchor="{anchor}" '
            f'font-weight="{weight}">{s}</text>')


def box(x, y, label, style="plain", w=70, h=54):
    fill, stroke = FILL[style]
    color = "#9ca3af" if style == "done" else INK
    return (f'<rect x="{x - w / 2}" y="{y - h / 2}" width="{w}" height="{h}" rx="10" fill="{fill}" '
            f'stroke="{stroke}" stroke-width="2.5"/>' + text(x, y + 8, str(label), 24, color, weight="bold"))


def row(x0, y, values, styles=None, gap=82, w=70):
    styles = styles or ["plain"] * len(values)
    return "".join(box(x0 + i * gap, y, v, s, w) for i, v in enumerate(values) for s in [styles[i]])


def bracket(x1, x2, y, label, color=MUTED):
    return (f'<path d="M {x1} {y} L {x1} {y + 10} L {x2} {y + 10} L {x2} {y}" fill="none" stroke="{color}" '
            f'stroke-width="2.5"/>' + text((x1 + x2) / 2, y + 36, label, 20, color))


def write(name, body):
    with open(os.path.join(OUT, name), "w") as f:
        f.write(svg(body))


T = [18, 4, 29, 11, 4, 16, 35, 9, 22]
X0 = 150

# 1. Groups of five
body = text(W / 2, 44, "Step 1: groups of 5, sort each tiny group, take its middle", 24, INK, weight="bold")
body += text(40, 128, "T", 22, MUTED, "start") + row(X0, 120, T)
body += bracket(X0 - 35, X0 + 4 * 82 + 35, 152, "group 1")
body += bracket(X0 + 5 * 82 - 35, X0 + 8 * 82 + 35, 152, "group 2")
body += text(40, 288, "sorted", 22, MUTED, "start")
body += row(X0, 280, [4, 4, 11, 18, 29], ["plain", "plain", "median", "plain", "plain"])
body += row(X0 + 5 * 82, 280, [9, 16, 22, 35], ["plain", "median", "plain", "plain"])
body += text(X0 + 2 * 82, 350, "middle: 11", 22, "#b45309", weight="bold")
body += text(X0 + 6 * 82, 350, "middle (lower of two): 16", 22, "#b45309", weight="bold")
body += text(W / 2, 470, "Only groups of 5 or fewer are sorted. T itself is never sorted.", 24, GREEN, weight="bold")
write("c-1-groups.svg", body)

# 2. Pivot
body = text(W / 2, 44, "Step 2: the middle of the medians is the pivot", 24, INK, weight="bold")
body += text(W / 2, 140, "medians", 22, MUTED) + row(W / 2 - 41, 190, [11, 16], ["median", "median"])
body += (f'<path d="M {W / 2} 230 L {W / 2} 300" stroke="{INK}" stroke-width="3"/>'
         f'<path d="M {W / 2 - 10} 292 L {W / 2 + 10} 292 L {W / 2} 310 z" fill="{INK}"/>')
body += box(W / 2, 360, "11", "same", w=110, h=70)
body += text(W / 2, 430, "pivot = 11", 28, PURPLE, weight="bold")
body += text(W / 2, 500, "With many groups, finding this middle runs the same algorithm on the medians.", 22, MUTED)
write("c-2-pivot.svg", body)

# 3. Split around the pivot
dest = ["larger" if v > 11 else "smaller" if v < 11 else "same" for v in T]
body = text(W / 2, 44, "Step 3: one pass, compare each value with 11", 24, INK, weight="bold")
body += text(40, 128, "T", 22, MUTED, "start") + row(X0, 120, T, dest)
body += text(40, 248, "smaller", 22, BLUE, "start") + row(X0 + 82, 240, [4, 4, 9], ["smaller"] * 3)
body += text(40, 338, "same", 22, PURPLE, "start") + row(X0 + 82, 330, [11], ["same"])
body += text(40, 428, "larger", 22, ORANGE, "start") + row(X0 + 82, 420, [18, 29, 16, 35, 22], ["larger"] * 5)
body += text(W - 40, 248, "3 values", 22, BLUE, "end", "bold")
body += text(W - 40, 338, "1 value", 22, PURPLE, "end", "bold")
body += text(W - 40, 428, "5 values", 22, ORANGE, "end", "bold")
body += text(W / 2, 515, "No sorting: each group keeps the original order.", 24, GREEN, weight="bold")
write("c-3-split.svg", body)


# 4. Rank bands
def bands(name, title, sizes, labels, ks, footer):
    body = text(W / 2, 44, title, 24, INK, weight="bold")
    n = sum(sizes)
    cell = min(80, 760 / n)
    x0 = (W - cell * n) / 2
    styles = ["smaller", "same", "larger"]
    r = 0
    for sz, st, lab in zip(sizes, styles, labels):
        if sz == 0:
            continue
        fill, stroke = FILL[st]
        body += (f'<rect x="{x0 + r * cell}" y="140" width="{sz * cell}" height="70" rx="8" fill="{fill}" '
                 f'stroke="{stroke}" stroke-width="2.5"/>')
        body += text(x0 + (r + sz / 2) * cell, 120, lab, 21, stroke, weight="bold")
        r += sz
    for i in range(n):
        body += text(x0 + (i + 0.5) * cell, 184, str(i + 1), 22, INK)
    body += text(x0 - 16, 184, "rank", 20, MUTED, "end")
    # Markers under the bar, outcomes listed below at a fixed left edge
    for k, _ in ks:
        x = x0 + (k - 0.5) * cell
        body += f'<path d="M {x} 222 L {x - 9} 238 L {x + 9} 238 z" fill="{RED}"/>'
        body += text(x, 262, f"k={k}", 19, RED, weight="bold")
    for j, (k, outcome) in enumerate(ks):
        body += text(x0, 318 + j * 44, f"k = {k}: {outcome}", 22, INK, "start")
    body += text(W / 2, 525, footer, 22, MUTED)
    write(name, body)


bands("c-4-bands.svg", "Step 4: which rank band does k land in?", [3, 1, 5],
      ["smaller: ranks 1–3", "same: 4", "larger: ranks 5–9"],
      [(2, "keep smaller, k stays 2"), (4, "in same, answer = pivot 11"),
       (5, "keep larger, new k = 5 − 3 − 1 = 1"), (9, "keep larger, new k = 9 − 3 − 1 = 5")],
      "Bands come from the group sizes only. No group is sorted to find them.")

# 5. Base case
body = text(W / 2, 44, "Step 5: the kept group has 5 or fewer values", 24, INK, weight="bold")
body += text(40, 158, "larger", 22, ORANGE, "start") + row(X0 + 82, 150, [18, 29, 16, 35, 22], ["larger"] * 5)
body += text(W - 40, 158, "k = 1", 26, RED, "end", "bold")
body += text(40, 318, "sorted", 22, MUTED, "start")
body += row(X0 + 82, 310, [16, 18, 22, 29, 35], ["answer", "plain", "plain", "plain", "plain"])
body += text(X0 + 82, 380, "1st", 22, GREEN, weight="bold")
body += text(W / 2, 470, "Answer: 16, the 5th smallest of the original 9", 28, GREEN, weight="bold")
write("c-5-base.svg", body)

# 6. A run on 100 values
ROUNDS = [(100, 37, (51, 1, 48), 0, "keep smaller, k stays 37"),
          (51, 37, (26, 1, 24), 2, "keep larger, k = 37 − 27 = 10"),
          (24, 10, (13, 1, 10), 0, "keep smaller, k stays 10"),
          (13, 10, (6, 1, 6), 2, "keep larger, k = 10 − 7 = 3"),
          (6, 3, (2, 1, 3), 1, "in same: answer = pivot 37")]
body = text(W / 2, 40, "Values 1–100 scrambled, k = 37: each round keeps one band", 24, INK, weight="bold")
BX, BW = 250, 380
for i, (n, k, sizes, keep, note) in enumerate(ROUNDS):
    y = 80 + i * 92
    body += text(40, y + 26, f"{n} values", 22, INK, "start", "bold")
    body += text(40, y + 52, f"k = {k}", 20, RED, "start", "bold")
    total = sum(sizes)
    widths = [max(BW * s / total, 26) for s in sizes]
    scale = BW / sum(widths)
    x = BX
    for j, (s, st) in enumerate(zip(sizes, ["smaller", "same", "larger"])):
        w = widths[j] * scale
        fill, stroke = FILL[st] if j == keep else ("#f9fafb", FAINT)
        sw = 3.5 if j == keep else 2
        body += (f'<rect x="{x}" y="{y}" width="{w}" height="58" rx="6" fill="{fill}" stroke="{stroke}" '
                 f'stroke-width="{sw}"/>')
        body += text(x + w / 2, y + 37, str(s), 20, INK if j == keep else "#9ca3af", weight="bold")
        x += w
    body += text(BX + BW + 20, y + 37, note, 20, INK, "start")
body += text(W / 2, 545, "Values left: 100 → 51 → 24 → 13 → 6. Bars show smaller / same / larger.", 20, MUTED)
write("c-6-rounds.svg", body)

# 7. Duplicates (C4)
body = text(W / 2, 44, "Repeated values: test C4, k = 4", 24, INK, weight="bold")
body += text(40, 128, "T", 22, MUTED, "start") + row(X0 + 82, 120, [7] * 6, ["same"] * 6)
body += text(W / 2, 200, "pivot = 7 → smaller: none · same: all six · larger: none", 22, PURPLE, weight="bold")
fill, stroke = FILL["same"]
cell, x0 = 80, (W - 6 * 80) / 2
body += (f'<rect x="{x0}" y="260" width="{6 * cell}" height="70" rx="8" fill="{fill}" stroke="{stroke}" '
         f'stroke-width="2.5"/>')
body += text(W / 2, 248, "same: ranks 1–6", 21, stroke, weight="bold")
for i in range(6):
    body += text(x0 + (i + 0.5) * cell, 304, str(i + 1), 22, INK)
kx = x0 + 3.5 * cell
body += f'<path d="M {kx} 350 L {kx - 9} 366 L {kx + 9} 366 z" fill="{RED}"/>'
body += text(kx + 18, 366, "k = 4: in same, answer = pivot 7", 21, INK, "start")
body += text(W / 2, 470, "All copies of the pivot go into “same”, so repeats finish in one step.", 22, MUTED)
write("c-7-duplicates.svg", body)
