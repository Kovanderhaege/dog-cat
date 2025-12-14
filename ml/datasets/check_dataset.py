from pathlib import Path

from PIL import Image

DATASET_ROOT = Path("J:/IA/data/dog-cat/dataset_v0/raw")
MIN_SIZE = 128
VALID_EXT = {".jpg", ".jpeg", ".png"}

stats = {}

for cls_dir in ["cat", "dog", "other"]:
    cls_path = DATASET_ROOT / cls_dir
    assert cls_path.exists(), f"Missing folder: {cls_dir}"

    files = list(cls_path.iterdir())
    total = 0
    invalid = 0
    sizes = []

    for f in files:
        if f.suffix.lower() not in VALID_EXT:
            invalid += 1
            continue

        try:
            with Image.open(f) as img:
                w, h = img.size
                if w < MIN_SIZE or h < MIN_SIZE:
                    invalid += 1
                    continue
                sizes.append((w, h))
                total += 1
        except Exception:
            invalid += 1

    stats[cls_dir] = {
        "valid_images": total,
        "invalid_images": invalid,
        "min_size": min(sizes) if sizes else None,
        "max_size": max(sizes) if sizes else None,
    }

print("Dataset sanity check:")
for cls, s in stats.items():
    print(f"{cls}: {s}")