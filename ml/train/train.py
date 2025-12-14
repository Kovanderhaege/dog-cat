import os
import torch

import torch.nn as nn
from torch.utils.data import DataLoader

from dataloader import SplitDataset
from metrics import macro_f1
from model import build_model
from transforms import build_transform

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
DATASET = "J:/IA/data/dog-cat/dataset_v0/raw"
SPLITS = os.path.join(os.path.dirname(DATASET), "splits")

train_ds = SplitDataset(DATASET, f"{SPLITS}/train.txt", build_transform())
val_ds   = SplitDataset(DATASET, f"{SPLITS}/val.txt", build_transform())

train_loader = DataLoader(train_ds, batch_size=32, shuffle=True)
val_loader   = DataLoader(val_ds, batch_size=32, shuffle=False)

model = build_model().to(DEVICE)
criterion = nn.CrossEntropyLoss()
optimizer = torch.optim.Adam(model.fc.parameters(), lr=1e-3)

best_f1, patience = 0.0, 0

for epoch in range(20):
    model.train()
    for x, y in train_loader:
        x, y = x.to(DEVICE), y.to(DEVICE)
        optimizer.zero_grad()
        loss = criterion(model(x), y)
        loss.backward()
        optimizer.step()

    model.eval()
    ys, ps = [], []
    with torch.no_grad():
        for x, y in val_loader:
            x = x.to(DEVICE)
            out = model(x).argmax(dim=1).cpu()
            ys.extend(y.tolist())
            ps.extend(out.tolist())

    f1 = macro_f1(ys, ps)
    print(f"Epoch {epoch} | val macro-F1 = {f1:.4f}")

    if f1 > best_f1:
        best_f1 = f1
        patience = 0
        torch.save(model.state_dict(), "model_v0.pt")
    else:
        patience += 1
        if patience >= 3:
            break