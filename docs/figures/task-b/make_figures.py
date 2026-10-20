# Draws the Task B walkthrough figures used in the slide deck. Run: python3 docs/figures/task-b/make_figures.py <output-dir>
"""Draws the Task B walkthrough figures (two-pointer merge and recursion) as SVG files."""
import os
import sys

OUT = sys.argv[1]
os.makedirs(OUT, exist_ok=True)

W, H = 960, 560
INK, MUTED, FAINT = "#1f2937", "#6b7280", "#d1d5db"
BLUE, ORANGE, GREEN, RED = "#2563eb", "#ea580c", "#16a34a", "#dc2626"
FONT = 'font-family="Helvetica, Arial, sans-serif"'


def svg(body, w=W, h=H):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}" {FONT}>'
            f'<rect width="{w}" height="{h}" fill="#ffffff"/>{body}</svg>')


def text(x, y, s, size=22, color=INK, anchor="middle", weight="normal"):
    return (f'<text x="{x}" y="{y}" font-size="{size}" fill="{color}" text-anchor="{anchor}" '
            f'font-weight="{weight}">{s}</text>')


def box(x, y, label, style="plain", w=80, h=56):
    fills = {"plain": ("#ffffff", INK, INK), "done": ("#f3f4f6", FAINT, "#9ca3af"),
             "wait": ("#dbeafe", BLUE, INK), "right": ("#ffedd5", ORANGE, INK), "out": ("#dcfce7", GREEN, INK)}
    fill, stroke, color = fills[style]
    return (f'<rect x="{x - w / 2}" y="{y - h / 2}" width="{w}" height="{h}" rx="10" fill="{fill}" '
            f'stroke="{stroke}" stroke-width="2.5"/>' + text(x, y + 8, label, 24, color, weight="bold"))


def pointer(x, y, label, color):
    # Downward triangle above a box, with its name
    return (f'<path d="M {x - 12} {y - 62} L {x + 12} {y - 62} L {x} {y - 44} z" fill="{color}"/>'
            + text(x, y - 68, label, 18, color, weight="bold"))


LEFT, RIGHT = [1, 3, 4, 7], [2, 5, 6, 8]
LX = [300 + i * 100 for i in range(4)]
MX = [196 + i * 90 for i in range(8)]
LY, RY, MY = 150, 290, 450


def frame(name, title, left_done, waiting, right_done, right_now, merged, total, note, note_color=INK,
          lp=None, rp=None):
    body = text(W / 2, 46, title, 26, INK, weight="bold")
    body += text(60, LY + 8, "Left", 22, MUTED, "start") + text(60, RY + 8, "Right", 22, MUTED, "start")
    body += text(60, MY + 8, "Merged", 22, MUTED, "start")
    for i, v in enumerate(LEFT):
        style = "done" if i < left_done else ("wait" if i in waiting else "plain")
        body += box(LX[i], LY, str(v), style)
    for i, v in enumerate(RIGHT):
        style = "done" if i < right_done else ("right" if i == right_now else "plain")
        body += box(LX[i], RY, str(v), style)
    for i, v in enumerate(merged):
        body += box(MX[i], MY, str(v), "out", w=76)
    for i in range(len(merged), 8):
        body += (f'<rect x="{MX[i] - 38}" y="{MY - 28}" width="76" height="56" rx="10" fill="none" '
                 f'stroke="{FAINT}" stroke-width="2" stroke-dasharray="6 5"/>')
    if lp is not None:
        body += pointer(LX[lp], LY, "L", BLUE)
    if rp is not None:
        body += pointer(LX[rp], RY, "R", ORANGE)
    if waiting:
        x0, x1 = LX[min(waiting)] - 48, LX[max(waiting)] + 48
        body += (f'<rect x="{x0}" y="{LY - 40}" width="{x1 - x0}" height="80" rx="14" fill="none" '
                 f'stroke="{BLUE}" stroke-width="3" stroke-dasharray="8 6"/>')
    body += (f'<rect x="{W - 200}" y="{RY - 46}" width="160" height="92" rx="14" fill="#fef2f2" '
             f'stroke="{RED}" stroke-width="2.5"/>')
    body += text(W - 120, RY - 14, "count", 20, RED)
    body += text(W - 120, RY + 28, str(total), 40, RED, weight="bold")
    body += text(W / 2, 535, note, 24, note_color, weight="bold")
    with open(os.path.join(OUT, name), "w") as f:
        f.write(svg(body))


frame("ptr-0.svg", "Start: both halves sorted, both pointers at the front",
      0, [], 0, 0, [], 0, "Right number now: 2. Move L until it finds a number bigger than 2.", lp=0, rp=0)
frame("ptr-1.svg", "Right number 2",
      1, [1, 2, 3], 1, None, [1, 2], 3, "L passed 1 and stopped at 3. Waiting: 3, 4, 7 → +3", RED, lp=1, rp=1)
frame("ptr-2.svg", "Right number 5 (L continues from 3, never back)",
      3, [3], 2, None, [1, 2, 3, 4, 5], 4, "L passed 3 and 4, stopped at 7. Waiting: 7 → +1", RED, lp=3, rp=2)
frame("ptr-3.svg", "Right number 6",
      3, [3], 3, None, [1, 2, 3, 4, 5, 6], 5, "L is already at 7, bigger than 6. Waiting: 7 → +1", RED, lp=3, rp=3)
frame("ptr-4.svg", "Right number 8: left runs out",
      4, [], 4, None, [1, 2, 3, 4, 5, 6, 7, 8], 5, "L passed 7: left is empty. 8 is a leftover → +0. Total 5.",
      GREEN)


# Recursion trees for B1: ranks [1, 3, 4, 2, 5]
def piece(x, y, nums, style="plain", count=None):
    w = 46 * len(nums) + 20
    fill, stroke = {"plain": ("#ffffff", INK), "out": ("#dcfce7", GREEN), "top": ("#fef2f2", RED)}[style]
    s = (f'<rect x="{x - w / 2}" y="{y - 26}" width="{w}" height="52" rx="10" fill="{fill}" '
         f'stroke="{stroke}" stroke-width="2.5"/>')
    s += text(x, y + 8, "  ".join(str(n) for n in nums), 24, INK, weight="bold")
    if count is not None:
        s += text(x + w / 2 + 10, y + 8, f"+{count}", 22, RED if count else MUTED, "start", weight="bold")
    return s


def edge(x1, y1, x2, y2):
    return f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{FAINT}" stroke-width="2.5"/>'


# Node layout: (x, y) per piece id
POS = {"root": (480, 100), "L": (290, 220), "R": (700, 220), "LL": (190, 340), "LR": (390, 340),
       "LLL": (130, 460), "LLR": (250, 460), "RL": (620, 340), "RR": (780, 340)}
TREE = [("root", "L"), ("root", "R"), ("L", "LL"), ("L", "LR"), ("LL", "LLL"), ("LL", "LLR"),
        ("R", "RL"), ("R", "RR")]

down = {"root": [1, 3, 4, 2, 5], "L": [1, 3, 4], "R": [2, 5], "LL": [1, 3], "LR": [4],
        "LLL": [1], "LLR": [3], "RL": [2], "RR": [5]}
body = text(W / 2, 40, "Going down: split until each piece has 1 number", 26, INK, weight="bold")
for a, b in TREE:
    body += edge(POS[a][0], POS[a][1] + 26, POS[b][0], POS[b][1] - 26)
for k, nums in down.items():
    body += piece(*POS[k], nums)
body += text(W / 2, 535, "Pieces of 1 number have no pairs: count 0", 24, MUTED)
with open(os.path.join(OUT, "rec-down.svg"), "w") as f:
    f.write(svg(body))

up = {"root": ([1, 2, 3, 4, 5], 2, "top"), "L": ([1, 3, 4], 0, "out"), "R": ([2, 5], 0, "out"),
      "LL": ([1, 3], 0, "out"), "LR": ([4], None, "plain"), "LLL": ([1], None, "plain"),
      "LLR": ([3], None, "plain"), "RL": ([2], None, "plain"), "RR": ([5], None, "plain")}
body = text(W / 2, 40, "Coming up: each merge returns a sorted piece and its count", 26, INK, weight="bold")
for a, b in TREE:
    body += edge(POS[a][0], POS[a][1] + 26, POS[b][0], POS[b][1] - 26)
for k, (nums, c, style) in up.items():
    body += piece(*POS[k], nums, style, c)
body += text(W / 2, 535, "Total = 0 + 0 + 0 + 2 = 2 (only the top merge finds flipped pairs)", 24, RED, weight="bold")
with open(os.path.join(OUT, "rec-up.svg"), "w") as f:
    f.write(svg(body))
