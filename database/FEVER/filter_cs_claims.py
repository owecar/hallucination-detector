
import json
import re
from pathlib import Path
from collections import Counter

folder = Path(__file__).resolve().parent

source = folder / "train.jsonl"
output = folder / "fever_cs_train.jsonl"

# More specific Computer Science terminology
patterns = [
    r"\bcomputer science\b",
    r"\bcomputer scientist\b",
    r"\bcomputer programming\b",
    r"\bprogramming language\b",
    r"\bsoftware engineer(?:ing)?\b",
    r"\bsoftware developer\b",
    r"\bsource code\b",
    r"\boperating system\b",
    r"\bcomputer network\b",
    r"\bcomputer security\b",
    r"\bcybersecurity\b",
    r"\bmachine learning\b",
    r"\bartificial intelligence\b",
    r"\bneural network\b",
    r"\bdatabase management\b",
    r"\brelational database\b",
    r"\bsearch algorithm\b",
    r"\bdata structure\b",
    r"\bcompiler\b",
    r"\bencryption\b",
    r"\bcryptography\b",
    r"\bpython programming\b",
    r"\bjava programming\b",
    r"\bjavascript\b",
    r"\blinux kernel\b",
    r"\bSQL\b",
    r"\bTCP/IP\b",
    r"\bHTTP protocol\b",
]

matches = []
counts = Counter()

with source.open("r", encoding="utf-8") as file:
    for line in file:
        record = json.loads(line)
        claim = record["claim"]

        if any(
            re.search(pattern, claim, re.IGNORECASE)
            for pattern in patterns
        ):
            matches.append(record)
            counts[record["label"]] += 1

with output.open("w", encoding="utf-8") as file:
    for record in matches:
        file.write(json.dumps(record) + "\n")

print("Technical candidate claims:", len(matches))
print("Label distribution:", dict(counts))

print("\nMatching claims:")
for record in matches[:30]:
    print(
        f"[{record['label']}] "
        f"{record['claim']}"
    )

print("\nSaved to:", output)
