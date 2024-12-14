#!/usr/bin/env python3
# -*- encoding: utf-8 -*-
# Copyright FunASR (https://github.com/alibaba-damo-academy/FunASR). All Rights Reserved.
#  MIT License  (https://opensource.org/licenses/MIT)

# method1, inference from model hub

from funasr import AutoModel

model = AutoModel(
    model="iic/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-pytorch",
    device="cpu",
)

res = model.export(type="torchscript", quantize=False)
print(res)


# # method2, inference from local path
# from funasr import AutoModel

# model = AutoModel(
#     model="/Users/zhifu/.cache/modelscope/hub/iic/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-pytorch",
#     device="cpu",
# )

# res = model.export(type="onnx", quantize=False)
# print(res)
