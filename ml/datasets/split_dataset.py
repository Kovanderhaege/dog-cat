import random
from pathlib import Path

from PIL import Image

DATASET_ROOT = Path("J:/IA/data/dog-cat/dataset_v0/raw")
OUT_DIR = DATASET_ROOT.parent / "splits"

CLASSES = ["cat", "dog", "other"]
SEED = 42
RATIOS = (0.70, 0.15, 0.15)
MIN_SIZE = 128
VALID_EXT = {".jpg", ".jpeg", ".png"}

random.seed(SEED)
OUT_DIR.mkdir(parents=True, exist_ok=True)

def is_valid_image(p: Path) -> bool:
    if p.suffix.lower() not in VALID_EXT:
        return False
    try:
        with Image.open(p) as img:
            w, h = img.size
            return w >= MIN_SIZE and h >= MIN_SIZE
    except Exception:
        return False

train, val, test = [], [], []

for cls in CLASSES:
    files = [
        p for p in (DATASET_ROOT / cls).iterdir()
        if p.is_file() and is_valid_image(p)
    ]
    random.shuffle(files)

    n = len(files)
    n_train = int(n * RATIOS[0])
    n_val = int(n * RATIOS[1])

    train += [f"{cls}/{p.name}" for p in files[:n_train]]
    val   += [f"{cls}/{p.name}" for p in files[n_train:n_train+n_val]]
    test  += [f"{cls}/{p.name}" for p in files[n_train+n_val:]]

def write(name, items):
    with open(OUT_DIR / name, "w", encoding="utf-8") as f:
        f.write("\n".join(items))

write("train.txt", train)
write("val.txt", val)
write("test.txt", test)

print("Split completed:")
print(f"train: {len(train)}")
print(f"val:   {len(val)}")
print(f"test:  {len(test)}")