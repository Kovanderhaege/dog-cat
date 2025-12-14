import os
import torch

import numpy as np
from sklearn.metrics import confusion_matrix
from torch.utils.data import DataLoader

from dataloader import SplitDataset
from model import build_model
from transforms import build_transform

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
DATASET = "J:/IA/data/dog-cat/dataset_v0/raw"
SPLITS = os.path.join(os.path.dirname(DATASET), "splits")
LABELS = ["cat", "dog", "other"]
OTHER_IDX = 2

ds = SplitDataset(DATASET, f"{SPLITS}/val.txt", build_transform())
dl = DataLoader(ds, batch_size=32, shuffle=False)

model = build_model().to(DEVICE)
model.load_state_dict(torch.load("model_v0.pt", map_location=DEVICE))
model.eval()

probas, ys = [], []
with torch.no_grad():
    for x, y in dl:
        x = x.to(DEVICE)
        p = torch.softmax(model(x), dim=1).cpu().numpy()
        probas.append(p)
        ys.extend(y.tolist())

probas = np.vstack(probas)
ys = np.array(ys)

def eval_tau(tau):
    preds = []
    for p in probas:
        if p.max() < tau:
            preds.append(OTHER_IDX)
        else:
            preds.append(p.argmax())
    cm = confusion_matrix(ys, preds, labels=[0,1,2])
    # erreurs critiques: other -> dog/cat
    other_to_dc = cm[2,0] + cm[2,1]
    return cm, other_to_dc

for tau in [0.5, 0.6, 0.7, 0.8, 0.9]:
    cm, crit = eval_tau(tau)
    print(f"tau={tau} | other->dog/cat={crit}")