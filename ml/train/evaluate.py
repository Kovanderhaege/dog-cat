import os
import torch

from sklearn.metrics import classification_report, confusion_matrix
from torch.utils.data import DataLoader

from dataloader import SplitDataset
from model import build_model
from transforms import build_transform

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
DATASET = "J:/IA/data/dog-cat/dataset_v0/raw"
SPLITS = os.path.join(os.path.dirname(DATASET), "splits")
LABELS = ["cat", "dog", "other"]

# Dataset / loader
test_ds = SplitDataset(DATASET, f"{SPLITS}/test.txt", build_transform())
test_loader = DataLoader(test_ds, batch_size=32, shuffle=False)

# Model
model = build_model().to(DEVICE)
model.load_state_dict(torch.load("model_v0.pt", map_location=DEVICE))
model.eval()

ys, ps = [], []
with torch.no_grad():
    for x, y in test_loader:
        x = x.to(DEVICE)
        logits = model(x)
        preds = logits.argmax(dim=1).cpu().tolist()
        ps.extend(preds)
        ys.extend(y.tolist())

print("Classification report:")
print(classification_report(ys, ps, target_names=LABELS, digits=4))

print("Confusion matrix:")
print(confusion_matrix(ys, ps))