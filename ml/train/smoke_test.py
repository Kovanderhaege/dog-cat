import os

from torch.utils.data import DataLoader

from dataloader import SplitDataset
from transforms import build_transform

DATASET = "J:/IA/data/dog-cat/dataset_v0/raw"
SPLITS = os.path.join(os.path.dirname(DATASET), "splits")

ds = SplitDataset(DATASET, f"{SPLITS}/train.txt", build_transform())
loader = DataLoader(ds, batch_size=8, shuffle=True)

x, y = next(iter(loader))
print(x.shape, y)