import torch

from train.model import build_model

DEVICE = "cpu"  # export CPU
model = build_model()
model.load_state_dict(torch.load("../train/model_v0.pt", map_location=DEVICE))
model.eval()

dummy = torch.randn(1, 3, 224, 224)
torch.onnx.export(
    model,
    dummy,
    "model_v0.onnx",
    input_names=["input"],
    output_names=["logits"],
    dynamic_axes={"input": {0: "batch"}, "logits": {0: "batch"}},
    opset_version=18,
    do_constant_folding=True,
    dynamo=False,
)

print("Exported model_v0.onnx")