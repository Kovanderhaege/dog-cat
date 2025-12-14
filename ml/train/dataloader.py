from pathlib import Path

from PIL import Image
from torch.utils.data import Dataset


class SplitDataset(Dataset):
    def __init__(self, dataset_root, split_file, transform):
        self.dataset_root = Path(dataset_root)
        self.transform = transform
        with open(split_file, "r", encoding="utf-8") as f:
            self.items = [line.strip() for line in f if line.strip()]

    def __len__(self):
        return len(self.items)

    def __getitem__(self, idx):
        rel = self.items[idx]
        label = rel.split("/")[0]
        img_path = self.dataset_root / rel

        img = Image.open(img_path).convert("RGB")
        img = self.transform(img)

        label_map = {"cat": 0, "dog": 1, "other": 2}
        return img, label_map[label]