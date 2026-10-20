# Regenerate: python3 docs/figures/task-a/make_figures.py docs/figures/task-a
"""Generates the Task A explainer figures as SVG files."""
import math
import os
import sys

OUT = sys.argv[1]
os.makedirs(OUT, exist_ok=True)

PTS = {"A": (0, 0), "B": (7, 2), "C": (4, 5), "D": (5, 5), "E": (12, 2), "F": (2, 9)}
LEFT = {"A", "F", "C"}
LINE_X = 4
D = math.sqrt(13)

BLUE, ORANGE, GREY, GREEN, INK, FAINT = "#2563eb", "#ea580c", "#a3a3a3", "#16a34a", "#1f2937", "#e5e7eb"
XMIN, XMAX, YMIN, YMAX, S = -1, 13.5, -1, 10.5, 38
ML, MT = 34, 16
W = int((XMAX - XMIN) * S + ML + 16)
H = int((YMAX - YMIN) * S + MT + 34)


def px(x, y):
    return ML + (x - XMIN) * S, MT + (YMAX - y) * S


def svg(body, w=W, h=H):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}" '
            f'font-family="Helvetica, Arial, sans-serif" font-size="13">'
            f'<rect width="{w}" height="{h}" fill="#ffffff"/>{body}</svg>')


def grid():
    out = []
    for x in range(0, 14):
        X, _ = px(x, 0)
        out.append(f'<line x1="{X}" y1="{px(0, YMAX)[1]}" x2="{X}" y2="{px(0, YMIN)[1]}" stroke="{FAINT}"/>')
        if x % 2 == 0:
            out.append(f'<text x="{X}" y="{px(0, YMIN)[1] + 16}" text-anchor="middle" fill="#6b7280">{x}</text>')
    for y in range(0, 11):
        _, Y = px(0, y)
        out.append(f'<line x1="{px(XMIN, 0)[0]}" y1="{Y}" x2="{px(XMAX, 0)[0]}" y2="{Y}" stroke="{FAINT}"/>')
        if y % 2 == 0:
            out.append(f'<text x="{px(XMIN, 0)[0] - 6}" y="{Y + 4}" text-anchor="end" fill="#6b7280">{y}</text>')
    return "".join(out)


def point(name, color, label=True):
    X, Y = px(*PTS[name])
    s = f'<circle cx="{X}" cy="{Y}" r="6" fill="{color}"/>'
    if label:
        x, y = PTS[name]
        left = name == "C"
        tx = X - 9 if left else X + 9
        anchor = "end" if left else "start"
        s += f'<text x="{tx}" y="{Y - 8}" fill="{INK}" font-weight="bold" text-anchor="{anchor}">{name}</text>'
        s += f'<text x="{tx}" y="{Y + 16}" fill="#6b7280" font-size="11" text-anchor="{anchor}">({x}, {y})</text>'
    return s


def divider(label=True):
    X, _ = px(LINE_X, 0)
    s = (f'<line x1="{X}" y1="{px(0, YMAX)[1]}" x2="{X}" y2="{px(0, YMIN)[1]}" '
         f'stroke="{INK}" stroke-width="2" stroke-dasharray="7 5"/>')
    if label:
        s += f'<text x="{X + 6}" y="{px(0, YMAX)[1] + 14}" fill="{INK}">dividing line x = 4</text>'
    return s


def seg(a, b, color, text=None, dash=False, width=3, dx=0, dy=0):
    x1, y1 = px(*PTS[a])
    x2, y2 = px(*PTS[b])
    d = ' stroke-dasharray="6 4"' if dash else ""
    s = f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{color}" stroke-width="{width}"{d}/>'
    if text:
        s += (f'<text x="{(x1 + x2) / 2 + dx}" y="{(y1 + y2) / 2 + dy}" fill="{color}" '
              f'font-weight="bold">{text}</text>')
    return s


def band(alpha="0.12"):
    x1, _ = px(LINE_X - D, 0)
    x2, _ = px(LINE_X + D, 0)
    top, bot = px(0, YMAX)[1], px(0, YMIN)[1]
    return (f'<rect x="{x1}" y="{top}" width="{x2 - x1}" height="{bot - top}" fill="{GREEN}" fill-opacity="{alpha}"/>'
            f'<line x1="{x1}" y1="{top}" x2="{x1}" y2="{bot}" stroke="{GREEN}" stroke-width="1.5"/>'
            f'<line x1="{x2}" y1="{top}" x2="{x2}" y2="{bot}" stroke="{GREEN}" stroke-width="1.5"/>')


def box(y0, height, color, opacity, dash=False, label=None):
    x1, _ = px(LINE_X - D, 0)
    x2, _ = px(LINE_X + D, 0)
    _, ytop = px(0, y0 + height)
    _, ybot = px(0, y0)
    d = ' stroke-dasharray="6 4"' if dash else ""
    s = (f'<rect x="{x1}" y="{ytop}" width="{x2 - x1}" height="{ybot - ytop}" fill="{color}" '
         f'fill-opacity="{opacity}" stroke="{color}" stroke-width="2"{d}/>')
    if label:
        s += f'<text x="{x2 + 6}" y="{ytop + 4}" fill="{color}" font-weight="bold">{label}</text>'
    return s


def note(x, y, text, color=INK, anchor="start", size=12):
    X, Y = px(x, y)
    return f'<text x="{X}" y="{Y}" fill="{color}" text-anchor="{anchor}" font-size="{size}">{text}</text>'


def write(name, body, w=W, h=H):
    with open(os.path.join(OUT, name), "w") as f:
        f.write(svg(body, w, h))


# 1. The points
write("a2-1-points.svg", grid() + "".join(point(n, INK) for n in PTS))

# 2. Split
body = grid() + divider()
body += "".join(point(n, BLUE if n in LEFT else ORANGE) for n in PTS)
body += note(0.3, 10.0, "left half: A, F, C", BLUE, size=13)
body += note(8.2, 10.0, "right half: D, B, E", ORANGE, size=13)
write("a2-2-split.svg", body)

# 3. Solve each half
body = grid() + divider()
body += seg("F", "C", BLUE, "≈ 4.5", dx=-46, dy=-2)
body += seg("D", "B", ORANGE, "≈ 3.6", dx=8, dy=-4)
body += "".join(point(n, BLUE if n in LEFT else ORANGE) for n in PTS)
body += note(0.3, 10.0, "left best: F–C ≈ 4.5", BLUE, size=13)
body += note(8.2, 10.0, "right best: D–B ≈ 3.6", ORANGE, size=13)
body += note(8.2, 7.6, "best so far: d ≈ 3.6", INK, size=14)
write("a2-3-halves.svg", body)

# 4. x strip
body = grid() + band() + divider()
for n in PTS:
    body += point(n, GREY if n in ("A", "E") else (BLUE if n in LEFT else ORANGE))
body += note(4, 7.3, "← d →    ← d →", GREEN, anchor="middle", size=13)
body += note(-0.8, 1.3, "A: too far", "#6b7280", size=11)
body += note(9.3, 3.2, "8 from the line: too far", "#6b7280", size=11)
body += note(LINE_X + D + 0.2, 10.0, "x strip: within d of the line", GREEN, size=13)
write("a2-4-xstrip.svg", body)


def strip_points(active):
    s = ""
    for n in PTS:
        if n in ("A", "E"):
            s += point(n, GREY, label=False)
        else:
            s += point(n, INK if n == active else (BLUE if n in LEFT else ORANGE))
    return s


# 5a. B's turn
body = grid() + band("0.06") + divider(label=False)
body += box(2, D, GREEN, "0.18", label="B's y strip: up to d above B")
body += seg("B", "C", GREY, "≈ 4.2", dash=True, width=2, dx=-52, dy=10)
body += seg("B", "D", GREY, "≈ 3.6", dash=True, width=2, dx=8, dy=-6)
body += strip_points("B")
body += note(LINE_X + D + 0.2, 8.5, "F is 7 above B: stop", "#6b7280", size=12)
body += note(LINE_X + D + 0.2, 1.0, "C and D: checked, not closer", "#6b7280", size=12)
write("a2-5-b-turn.svg", body)

# 5b. C's turn
body = grid() + band("0.06") + divider(label=False)
body += box(5, D, GREEN, "0.08", dash=True, label="C's y strip at the start (d ≈ 3.6)")
body += box(5, 1, GREEN, "0.28", label=None)
body += note(LINE_X + D + 0.2, 6.1, "after C–D: d = 1, strip shrinks", GREEN, size=12)
body += seg("C", "D", GREEN, "1", width=4, dx=-4, dy=-12)
body += strip_points("C")
body += note(LINE_X + D + 0.2, 9.4, "F is 4 above C: stop", "#6b7280", size=12)
write("a2-5-c-turn.svg", body)

# 6. Answer
body = grid() + divider()
body += seg("C", "D", GREEN, "d² = 1", width=5, dx=-22, dy=-30)
body += "".join(point(n, BLUE if n in LEFT else ORANGE) for n in PTS)
body += note(5.5, 7.4, "answer: C, D (crosses the line)", GREEN, size=14)
write("a2-6-answer.svg", body)

# 7. Levels of splitting (illustrative 12 points)
LV = [(0.5, 2), (1.5, 7), (2.5, 4), (3.5, 8.5), (4.5, 1), (5.5, 6),
      (7, 3), (8, 8.5), (9, 5), (10, 2), (11, 7), (12, 4)]
body = grid()
lines = [(6.25, 3.5, "split 1", "combined last"), (3.0, 2, "split 2", "combined first"),
         (9.5, 2, "split 2", "combined first")]
for x, w, name, when in lines:
    X, _ = px(x, 0)
    body += (f'<line x1="{X}" y1="{px(0, YMAX)[1]}" x2="{X}" y2="{px(0, YMIN)[1]}" stroke="{INK}" '
             f'stroke-width="{w}" stroke-dasharray="7 5"/>')
    body += note(x + 0.15, 10.15, name, INK, size=13)
    body += note(x + 0.15, 9.7, when, "#6b7280", size=11)
for i, (x, y) in enumerate(LV):
    X, Y = px(x, y)
    color = [BLUE, "#7c3aed", ORANGE, "#db2777"][i // 3]
    body += f'<circle cx="{X}" cy="{Y}" r="6" fill="{color}"/>'
write("levels.svg", body)
