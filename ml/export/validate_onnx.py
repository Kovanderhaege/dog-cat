import numpy as np
import onnxruntime as ort
import torch

from train.model import build_model

# charge pytorch
pt = build_model()
pt.load_state_dict(torch.load("../train/model_v0.pt", map_location="cpu"))
pt.eval()

# charge onnx
ort_sess = ort.InferenceSession("model_v0.onnx", providers=["CPUExecutionProvider"])

x = torch.randn(4,3,224,224)
with torch.no_grad():
    pt_out = pt(x).numpy()

onnx_out = ort_sess.run(None, {"input": x.numpy()})[0]
onnx_out = onnx_out.astype("float32")

print("max abs diff:", np.max(np.abs(pt_out - onnx_out)))